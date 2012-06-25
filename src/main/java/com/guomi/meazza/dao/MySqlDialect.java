/* 
 * @(#)MySqlDialect.java.java    Created on 2012-6-25
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.dao;

/**
 * 适用于 MySQL 数据库的 SQL 方言类。
 * 
 * @author akuma
 */
public class MySqlDialect implements Dialect {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLimitSql(String aSql, int offset, int limit) {
        String sql = aSql.trim();

        StringBuilder pagingSelect = new StringBuilder(sql.length() + 64);
        pagingSelect.append("select * from (");
        pagingSelect.append(sql);
        pagingSelect.append(") temp_rs limit " + limit);

        if (offset > 0) {
            pagingSelect.append(" offset " + offset);
        }

        return pagingSelect.toString();
    }

    @Override
    public String getLimitSql(String sql, int offset, int limit, String orderBy) {
        return getLimitSql(sql, offset, limit);
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

}
