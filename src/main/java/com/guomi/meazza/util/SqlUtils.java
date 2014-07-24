/*
 * @(#)SqlUtils.java    Created on 2013-7-25
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 处理 SQL 语句的工具类。
 * 
 * @author akuma
 */
public abstract class SqlUtils {

    private static final String SQL_COUNT_TEMPLATE = "SELECT COUNT(1) FROM (%s) AS tmp_count_result";

    private static final String SQL_SELECT_COUNT_PREFIX = "SELECT COUNT(1) FROM ";

    private static final Pattern SQL_GROUP_BY_PATTERN = Pattern.compile(
            "^.+( |\t|\r|\n)+group( |\t|\r|\n)+by( |\t|\r|\n)+.+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern SQL_FIRST_SELECT_PREFIX_PATTERN = Pattern.compile(
            "^( |\t|\r|\n)*select( |\t|\r|\n)+.+?( |\t|\r|\n)+from( |\t|\r|\n)+", Pattern.CASE_INSENSITIVE
                    | Pattern.DOTALL);

    /**
     * 根据原始 SQL 生成 count SQL。
     */
    public static String generateCountSql(String originSql) {
        String countSql = null;
        if (SQL_GROUP_BY_PATTERN.matcher(originSql).matches()) {
            countSql = String.format(SQL_COUNT_TEMPLATE, originSql);
        } else {
            countSql = SQL_FIRST_SELECT_PREFIX_PATTERN.matcher(originSql).replaceFirst(SQL_SELECT_COUNT_PREFIX);
        }
        return countSql;
    }

    /**
     * 解析 sql 文本，把所有 sql 作为结果返回。
     */
    public static String[] splitSql(String sqlText, boolean slashComments) throws IOException {
        if (!sqlText.endsWith(";")) {
            sqlText += ";";
        }

        Reader r = new StringReader(sqlText);
        StreamTokenizer token = new StreamTokenizer(r);

        token.resetSyntax();// 重置此标记生成器的语法表，使所有字符都成为“普通”字符。
        token.slashStarComments(slashComments);// 确定标记生成器是否识别 C 样式注释。
        token.quoteChar('\'');// 指定此字符的匹配对分隔此标记生成器中的字符串常量

        List<String> sqlList = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();

        int flag = StreamTokenizer.TT_EOF;
        while (StreamTokenizer.TT_EOF != (flag = token.nextToken())) {
            switch (flag) {
            case StreamTokenizer.TT_NUMBER:
                sb.append(token.nval);
                break;
            case StreamTokenizer.TT_WORD:
                sb.append(token.sval);
                break;
            case '\'':
                sb.append('\'').append(token.sval).append('\'');
                break;
            case ';':
                if ((!sb.toString().trim().startsWith("--") || !slashComments)
                        && !StringUtils.isBlank(sb.toString().trim())) {
                    sqlList.add(sb.toString());
                }
                sb = new StringBuffer();
                break;
            default:
                sb.append((char) token.ttype);
                break;
            }
        }
        return sqlList.toArray(new String[sqlList.size()]);
    }

}
