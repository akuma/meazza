/* 
 * @(#)PaginationPlugin.java    Created on 2013-1-23
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.orm.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.orm.jpa.vendor.Database;

import com.guomi.meazza.dao.Dialect;
import com.guomi.meazza.dao.MySqlDialect;
import com.guomi.meazza.dao.OracleDialect;
import com.guomi.meazza.dao.SqlServerDialect;
import com.guomi.meazza.util.Pagination;
import com.guomi.meazza.util.StringUtils;

/**
 * 支持 MyBatis 物理 SQL 分页查询的插件类。
 * 
 * @author akuma
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class MyBatisPagePlugin implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisPagePlugin.class);

    private static final String DELEGATE_BOUND_SQL = "delegate.boundSql.sql";
    private static final String DELEGATE_CONFIGURATION = "delegate.configuration";
    private static final String DELEGATE_MAPPED_STATEMENT = "delegate.mappedStatement";
    private static final String DELEGATE_ROW_BOUNDS_LIMIT = "delegate.rowBounds.limit";
    private static final String DELEGATE_ROW_BOUNDS_OFFSET = "delegate.rowBounds.offset";

    private static final String DEFAULT_DIALECT = Database.MYSQL.toString();

    private static final String COUNT_SQL_TEMPLATE = "SELECT COUNT(1) FROM (%s) AS tmp_count_result";

    private String sqlPattern;
    private Dialect dialect;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler);

        Pagination page = null;
        ParameterHandler parameterHandler = statementHandler.getParameterHandler();
        Object parameterObject = parameterHandler.getParameterObject();
        if (parameterObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) parameterObject;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object parameter = entry.getValue();
                if (parameter instanceof Pagination) {
                    page = (Pagination) parameter;
                    break;
                }
            }
        }

        if (page == null) {
            return invocation.proceed();
        }

        String originalSql = statementHandler.getBoundSql().getSql();
        logger.debug("Original SQL: {}", originalSql);

        // 查询总记录数
        int rowCount = getCount(metaObject, statementHandler.getBoundSql(), parameterObject);

        // 根据查询得到的总记录数初始化分页对象
        page.setRowCount(rowCount);
        page.initialize();

        // 分页查询 本地化对象 修改数据库注意修改实现
        String pageSql = generatePageSql(originalSql, page, dialect);
        if (logger.isDebugEnabled()) {
            logger.debug("Pagination SQL: {}", pageSql);
        }

        metaObject.setValue(DELEGATE_BOUND_SQL, pageSql);
        metaObject.setValue(DELEGATE_ROW_BOUNDS_OFFSET, RowBounds.NO_ROW_OFFSET);
        metaObject.setValue(DELEGATE_ROW_BOUNDS_LIMIT, RowBounds.NO_ROW_LIMIT);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        sqlPattern = properties.getProperty("sqlPattern");
        if (StringUtils.isBlank(sqlPattern)) {
            throw new RuntimeException("sqlPattern property must be specified");
        }

        String dialectType = StringUtils.defaultString(properties.getProperty("dialect"), DEFAULT_DIALECT);
        if (dialectType.equalsIgnoreCase(Database.MYSQL.toString())) {
            dialect = new MySqlDialect();
        } else if (dialectType.equalsIgnoreCase(Database.ORACLE.toString())) {
            dialect = new OracleDialect();
        } else if (dialectType.equalsIgnoreCase(Database.SQL_SERVER.toString())) {
            dialect = new SqlServerDialect();
        } else {
            throw new RuntimeException("dialect '" + dialectType + "' not supported");
        }
    }

    /**
     * 获取 SQL 查询结果的记录数。
     */
    private static int getCount(final MetaObject metaObject, final BoundSql boundSql, final Object parameterObject)
            throws SQLException {
        Configuration configuration = (Configuration) metaObject.getValue(DELEGATE_CONFIGURATION);
        Connection connection = configuration.getEnvironment().getDataSource().getConnection();

        String originSql = boundSql.getSql();
        String countSql = String.format(COUNT_SQL_TEMPLATE, originSql);
        logger.debug("Count SQL: {}", countSql);

        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue(DELEGATE_MAPPED_STATEMENT);
            BoundSql countBoundSql = new BoundSql(configuration, countSql, boundSql.getParameterMappings(),
                    parameterObject);
            setParameters(countStmt, mappedStatement, countBoundSql, parameterObject);

            rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(countStmt);
        }
    }

    /**
     * 根据原始 SQL 生成分页查询 SQL。
     */
    private static String generatePageSql(final String originSql, Pagination page, Dialect dialect) {
        if (!dialect.supportsLimit()) {
            return originSql;
        }

        int offset = (page.getCurrentRowNum() <= 0) ? 0 : (page.getCurrentRowNum() - 1);
        int limit = page.getPageSize();
        return dialect.getLimitSql(originSql, offset, limit);
    }

    /**
     * 设置 Count 语句中的参数，复制了
     * {@link org.apache.ibatis.executor.parameter.DefaultParameterHandler#setParameters(PreparedStatement)} 中的代码。
     */
    private static void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
            Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
                            && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(
                                    propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    @SuppressWarnings("unchecked")
                    TypeHandler<Object> typeHandler = (TypeHandler<Object>) parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
                                + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

}
