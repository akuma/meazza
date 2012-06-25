/* 
 * @(#)SqlServerDialect.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.dao;

/**
 * 适用于 MS SQL Server 数据库的 SQL 方言类。
 * 
 * @author akuma
 */
public class SqlServerDialect implements Dialect {

    /**
     * 获取带有 LIMIT 的 SQL 查询语句。SQL 采用了 MS SQL Server 的 row_number() 函数，排序字段写死为 <b>id</b>，这要求被查询的表结构中必须带有字段：id。
     */
    @Override
    public String getLimitSql(String sql, int offset, int limit) {
        return getLimitSql(sql, offset, limit, "id");
    }

    /**
     * 获取带有 LIMIT 的 SQL 查询语句。SQL 采用了 MS SQL Server 的 row_number() 函数，排序字段通过 <code>orderBy</code> 参数指定。
     * 
     * <p>
     * <b>注意：由于iBATIS 的 API 不易于扩展，所以此方法目前没有被使用。</b>
     */
    @Override
    public String getLimitSql(String sql, int offset, int limit, String orderBy) {
        boolean hasOffset = offset > 0;

        sql = sql.trim();

        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        pagingSelect
                .append("select row_.* from (select row_number() over(order by " + orderBy + ") rownum, t.* from (");
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(") t) row_ where row_.rownum between " + (offset + 1) + " and " + limit);
        } else {
            pagingSelect.append(") t) row_ where row_.rownum <= " + limit);
        }

        return pagingSelect.toString();
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

}
