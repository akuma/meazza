/* 
 * @(#)PaginationPlugin.java    Created on 2013-1-23
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.orm.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import com.guomi.meazza.dao.Dialect;
import com.guomi.meazza.dao.MySqlDialect;
import com.guomi.meazza.dao.OracleDialect;
import com.guomi.meazza.dao.SqlServerDialect;
import com.guomi.meazza.util.Pagination;
import com.guomi.meazza.util.SqlUtils;
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
    private static final String DELEGATE_ROW_BOUNDS_LIMIT = "delegate.rowBounds.limit";
    private static final String DELEGATE_ROW_BOUNDS_OFFSET = "delegate.rowBounds.offset";

    private static final String DEFAULT_DIALECT = "mysql";

    private String sqlPattern;
    private Dialect dialect;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StopWatch stopWatch = new StopWatch("MyBatis Pagination Plugin");

        stopWatch.start("Get page object");

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

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

        stopWatch.stop();

        if (page == null) {
            return invocation.proceed();
        }

        stopWatch.start("Count result");

        String originSql = statementHandler.getBoundSql().getSql();
        logger.debug("Original SQL: {}", originSql);

        // 查询总记录数
        Configuration configuration = (Configuration) metaObject.getValue(DELEGATE_CONFIGURATION);
        int rowCount = getQueryCount(configuration, statementHandler);

        stopWatch.stop();

        // 根据查询得到的总记录数初始化分页对象
        page.setRowCount(rowCount);
        page.initialize();

        stopWatch.start("Generate page sql");

        // 分页查询 本地化对象 修改数据库注意修改实现
        String pageSql = generatePageSql(originSql, page, dialect);
        logger.debug("Pagination SQL: {}", pageSql);

        metaObject.setValue(DELEGATE_BOUND_SQL, pageSql);
        metaObject.setValue(DELEGATE_ROW_BOUNDS_OFFSET, RowBounds.NO_ROW_OFFSET);
        metaObject.setValue(DELEGATE_ROW_BOUNDS_LIMIT, RowBounds.NO_ROW_LIMIT);

        stopWatch.stop();

        if (logger.isDebugEnabled()) {
            logger.debug(stopWatch.prettyPrint());
        }

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
        if (dialectType.equalsIgnoreCase("mysql")) {
            dialect = new MySqlDialect();
        } else if (dialectType.equalsIgnoreCase("oracle")) {
            dialect = new OracleDialect();
        } else if (dialectType.equalsIgnoreCase("sqlserver")) {
            dialect = new SqlServerDialect();
        } else {
            throw new RuntimeException("dialect '" + dialectType + "' not supported");
        }
    }

    /**
     * 获取 SQL 查询结果的记录数。
     */
    private static int getQueryCount(final Configuration configuration, final StatementHandler statementHandler)
            throws SQLException {
        long start = System.currentTimeMillis();

        String originSql = statementHandler.getBoundSql().getSql();
        String countSql = SqlUtils.generateCountSql(originSql);

        try (Connection connection = configuration.getEnvironment().getDataSource().getConnection();
                PreparedStatement countStmt = connection.prepareStatement(countSql)) {
            // 使用 parameterHandler 设置 count 语句中的参数
            statementHandler.getParameterHandler().setParameters(countStmt);
            ResultSet rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            logger.debug("Query page count elapsed {} ms.", elapsed);
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

}
