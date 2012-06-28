/* 
 * @(#)OracleDialect.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.dao;

/**
 * 适用于 Oracle 数据库的 SQL 方言类。
 * 
 * @author akuma
 */
public class OracleDialect implements Dialect {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLimitSql(String aSql, int offset, int limit) {
        boolean hasOffset = offset > 0;

        String sql = aSql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        final int forUpdateIndex = sql.toLowerCase().lastIndexOf("for update");

        if (forUpdateIndex > -1) {
            // Save 'for update ...' and then remove it
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }

        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from (select row_.*, rownum rownum_ from (");
        } else {
            pagingSelect.append("select * from (");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(") row_ where rownum <= " + limit + ") where rownum_ > " + offset);
        } else {
            pagingSelect.append(") where rownum <= " + limit);
        }

        if (isForUpdate) {
            pagingSelect.append(" ");
            pagingSelect.append(forUpdateClause);
        }

        return pagingSelect.toString();
    }

    @Override
    public String getLimitSql(String sql, int offset, int limit, String orderBy) {
        return getLimitSql(sql, offset, limit);
    }

    // public String getLimitString(String sql, boolean hasOffset) {
    // sql = sql.trim();
    // String forUpdateClause = null;
    // boolean isForUpdate = false;
    // final int forUpdateIndex = sql.toLowerCase().lastIndexOf("for update");
    //
    // if (forUpdateIndex > -1) {
    // // save 'for update ...' and then remove it
    // forUpdateClause = sql.substring(forUpdateIndex);
    // sql = sql.substring(0, forUpdateIndex - 1);
    // isForUpdate = true;
    // }
    //
    // StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
    // if (hasOffset) {
    // pagingSelect.append("select * from (select row_.*, rownum rownum_ from (");
    // }
    // else {
    // pagingSelect.append("select * from (");
    //
    // }
    // pagingSelect.append(sql);
    // if (hasOffset) {
    // pagingSelect.append(") row_ where rownum <= ?) where rownum_ > ?");
    // }
    // else {
    // pagingSelect.append(") where rownum <= ?");
    //
    // }
    //
    // if (isForUpdate) {
    // pagingSelect.append(" ");
    // pagingSelect.append(forUpdateClause);
    // }
    //
    // return pagingSelect.toString();
    // }

    @Override
    public boolean supportsLimit() {
        return true;
    }

}
