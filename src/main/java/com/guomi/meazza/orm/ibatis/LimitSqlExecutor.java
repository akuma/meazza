/* 
 * @(#)LimitSqlExecutor.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.orm.ibatis;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.guomi.meazza.dao.Dialect;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;

/**
 * 扩展 iBATIS 的 {@link com.ibatis.sqlmap.engine.execution.SqlExecutor}，提供了限制数据库查询记录条数的功能。
 * 
 * @author akuma
 */
public class LimitSqlExecutor extends SqlExecutor {

    private static final Logger logger = LoggerFactory.getLogger(LimitSqlExecutor.class);

    private boolean enableLimit = true;
    private Dialect dialect;

    @Override
    public void executeQuery(StatementScope statementScope, Connection conn, String sql, Object[] parameters,
            int skipResults, int maxResults, RowHandlerCallback callback) throws SQLException {
        executeQuery(statementScope, conn, sql, parameters, skipResults, maxResults, null, callback);
    }

    public void executeQuery(StatementScope statementScope, Connection conn, String sql, Object[] parameters,
            int skipResults, int maxResults, String orderBy, RowHandlerCallback callback) throws SQLException {
        String executeSql = sql;
        if ((skipResults != NO_SKIPPED_RESULTS || maxResults != NO_MAXIMUM_RESULTS) && supportsLimit()) {
            if (StringUtils.isEmpty(orderBy)) {
                executeSql = dialect.getLimitSql(sql, skipResults, maxResults);
            } else {
                executeSql = dialect.getLimitSql(sql, skipResults, maxResults, orderBy);
            }

            logger.debug("originSql: {}", sql);
            logger.debug("limitSql: {}", executeSql);

            skipResults = NO_SKIPPED_RESULTS;
            maxResults = NO_MAXIMUM_RESULTS;
        }
        super.executeQuery(statementScope, conn, executeSql, parameters, skipResults, maxResults, callback);
    }

    /**
     * 判断是否开启 LIMIT SQL 支持。
     * 
     * @return true/false
     */
    public boolean isEnableLimit() {
        return enableLimit;
    }

    /**
     * 设置是否开启 LIMIT SQL 支持。
     * 
     * @param enableLimit
     *            是否开启 LIMIT SQL 支持
     */
    public void setEnableLimit(boolean enableLimit) {
        this.enableLimit = enableLimit;
    }

    /**
     * 获取 SQL 方言对象。
     * 
     * @return SQL 方言对象
     */
    public Dialect getDialect() {
        return dialect;
    }

    /**
     * 设置 SQL 方言对象。
     * 
     * @param dialect
     *            SQL 方言对象
     */
    @Autowired(required = false)
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * 判断是否支持 limit 查询。
     * 
     * @return true/false
     */
    public boolean supportsLimit() {
        return enableLimit && (dialect != null && dialect.supportsLimit());
    }

}
