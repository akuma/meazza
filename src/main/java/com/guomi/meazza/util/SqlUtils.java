/* 
 * @(#)SqlUtils.java    Created on 2013-7-25
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.util.regex.Pattern;

/**
 * 处理 SQL 语句的工具类。
 * 
 * @author akuma
 */
public abstract class SqlUtils {

    private static final String SQL_COUNT_TEMPLATE = "SELECT COUNT(1) FROM (%s) AS tmp_count_result";

    private static final Pattern SQL_GROUP_BY_PATTERN = Pattern.compile(
            ".+( |\t|\r|\n)+group( |\t|\r|\n)+by( |\t|\r|\n)+.+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final String SQL_SELECT_COUNT_PREFIX = "SELECT COUNT(1) FROM ";

    private static final Pattern SQL_FIRST_SELECT_PREFIX_PATTERN = Pattern.compile(
            "^( |\t|\r|\n)*select( |\t|\r|\n)+.+?( |\t|\r|\n)+from( |\t|\r|\n)+", Pattern.CASE_INSENSITIVE
                    | Pattern.DOTALL);

    /**
     * 根据原始 SQL 生成 count SQL。
     */
    public static String generateCountSql(String originSql) {
        String countSql = null;
        if (SQL_GROUP_BY_PATTERN.matcher(originSql).find()) {
            countSql = String.format(SQL_COUNT_TEMPLATE, originSql);
        } else {
            countSql = SQL_FIRST_SELECT_PREFIX_PATTERN.matcher(originSql).replaceFirst(SQL_SELECT_COUNT_PREFIX);
        }
        return countSql;
    }

}
