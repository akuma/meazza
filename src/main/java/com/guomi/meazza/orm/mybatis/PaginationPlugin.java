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

import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

import com.guomi.meazza.dao.Dialect;
import com.guomi.meazza.dao.MySqlDialect;
import com.guomi.meazza.util.Pagination;
import com.guomi.meazza.util.StringUtils;

/**
 * @author akuma
 */
// @Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
// RowBounds.class, ResultHandler.class }) })
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationPlugin implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(PaginationPlugin.class);

    private String sqlPattern;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler);

        DefaultParameterHandler defaultParameterHandler = (DefaultParameterHandler) metaStatementHandler
                .getValue("delegate.parameterHandler");
        Object parameterObject = defaultParameterHandler.getParameterObject();

        Pagination page = null;
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

        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        logger.debug("Original SQL: {}", originalSql);

        // 得到总记录数
        Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
        Connection connection = configuration.getEnvironment().getDataSource().getConnection();
        int rowCount = getCount(originalSql, connection);

        // 分页计算
        page.setRowCount(rowCount);
        page.initialize();

        // 分页查询 本地化对象 修改数据库注意修改实现
        String pageSql = generatePageSql(originalSql, page, new MySqlDialect());
        if (logger.isDebugEnabled()) {
            logger.debug("Pagination SQL: {}", pageSql);
        }

        metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

        // Object[] args = invocation.getArgs();
        // logger.debug("args: {}", Arrays.asList(args));

        // final MappedStatement mappedStatement = (MappedStatement) args[0];
        // if (mappedStatement.getId().matches(sqlPattern)) { // 拦截需要分页的SQL
        // Object parameterObject = args[1];
        //
        // Pagination page = null;
        // Object parameterObjectWithoutPage = null;
        // if (parameterObject instanceof Map) {
        // @SuppressWarnings("unchecked")
        // Map<String, Object> params = (Map<String, Object>) parameterObject;
        // for (Map.Entry<String, Object> entry : params.entrySet()) {
        // Object parameter = entry.getValue();
        // if (parameter instanceof Pagination) {
        // page = (Pagination) parameter;
        // } else {
        // parameterObjectWithoutPage = parameter;
        // }
        // }
        // }
        //
        // if (page == null) {
        // return invocation.proceed();
        // }
        //
        // BoundSql boundSql = mappedStatement.getBoundSql(parameterObjectWithoutPage);
        // String originalSql = boundSql.getSql();
        // logger.debug("originalSql: {}", originalSql);
        //
        // // 得到总记录数
        // Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource()
        // .getConnection();
        // int rowCount = getCount(originalSql, connection, mappedStatement, parameterObjectWithoutPage, boundSql);
        //
        // // 分页计算
        // page.setRowCount(rowCount);
        // page.initialize();
        //
        // // 分页查询 本地化对象 修改数据库注意修改实现
        // String pageSql = generatePageSql(originalSql, page, new MySqlDialect());
        // if (logger.isDebugEnabled()) {
        // logger.debug("Pagination SQL: {}", pageSql);
        // }
        //
        // BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
        // boundSql.getParameterMappings(), parameterObjectWithoutPage);
        // MappedStatement newMappedStatement = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(
        // newBoundSql));
        //
        // args[0] = newMappedStatement;
        // args[1] = parameterObjectWithoutPage;
        // }

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
            throw new RuntimeException("sqlPattern property is not found!");
        }
    }

    private static int getCount(final String sql, final Connection connection, final MappedStatement mappedStatement,
            final Object parameterObject, final BoundSql boundSql) throws SQLException {
        final String countSql = "SELECT COUNT(1) FROM (" + sql + ") AS tmp_count";
        logger.debug("Count SQL: {}", countSql);

        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), parameterObject);

            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject,
                    countBoundSql);
            parameterHandler.setParameters(countStmt);

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

    private static int getCount(final String sql, final Connection connection) throws SQLException {
        final String countSql = "SELECT COUNT(1) FROM (" + sql + ") AS tmp_count";
        logger.debug("Count SQL: {}", countSql);

        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
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

    public static String generatePageSql(String sql, Pagination page, Dialect dialect) {
        if (dialect.supportsLimit()) {
            int limit = page.getPageSize();
            int offset = (page.getPageNum() - 1) * limit;
            return dialect.getLimitSql(sql, offset, limit);
        }

        return sql;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
    }

    private static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}
