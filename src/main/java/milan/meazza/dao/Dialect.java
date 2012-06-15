/* 
 * @(#)Dialect.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.dao;

/**
 * 数据库 SQL 方言接口。
 * 
 * @author akuma
 */
public interface Dialect {

    /**
     * 数据库是否支持获取数据时指定数据区间。比如 Oracle 通过 rownum 支持，MySQL 通过 limit 支持。
     * 
     * @return <code>true</code> / <code>false</code>
     */
    boolean supportsLimit();

    /**
     * 获取带 limit 查询的 SQL。
     * 
     * @param sql
     *            原 SQL
     * @param offset
     *            结果集的偏移量
     * @param limit
     *            记录限制条数
     * @return 带 limit 查询的 SQL
     */
    String getLimitSql(String sql, int offset, int limit);

    /**
     * 获取带 limit 查询的 SQL。
     * 
     * @param sql
     *            原 SQL
     * @param offset
     *            结果集的偏移量
     * @param limit
     *            记录限制条数
     * @param orderBy
     *            排序字段
     * @return 带 limit 查询的 SQL
     */
    String getLimitSql(String sql, int offset, int limit, String orderBy);

    // String getLimitString(String sql, boolean hasOffset);

}
