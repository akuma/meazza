/* 
 * @(#)SqlUtilsTest.java    Created on 2013-7-25
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author akuma
 */
public class SqlUtilsTest {

    @Test
    public void testGenerateCountSql() {
        String countSql = SqlUtils.generateCountSql("select * from t where id = ?");
        assertEquals("SELECT COUNT(1) FROM t where id = ?", countSql);

        countSql = SqlUtils.generateCountSql("select a, b, c from t where id = ?");
        assertEquals("SELECT COUNT(1) FROM t where id = ?", countSql);

        countSql = SqlUtils.generateCountSql("select   a,\t b,\n c \nfrom t where id = ?");
        assertEquals("SELECT COUNT(1) FROM t where id = ?", countSql);

        String sql = "   \n \t  SelEct\na.* , b.* \nfroM   "
                + "\n( select a from bb)\n aaa,  \t test a, \ntest1 b where a.id = b.id "
                + "and exists (select 1 \nfrom c where c.id = a.id) order by id";
        countSql = SqlUtils.generateCountSql(sql);
        assertEquals("SELECT COUNT(1) FROM "
                + "( select a from bb)\n aaa,  \t test a, \ntest1 b where a.id = b.id "
                + "and exists (select 1 \nfrom c where c.id = a.id) order by id", countSql);

        String origninSql = "select id, count(1) num from t where name = ? group by id order by id desc";
        countSql = SqlUtils.generateCountSql(origninSql);
        assertEquals("SELECT COUNT(1) FROM (" + origninSql + ") AS tmp_count_result", countSql);

        origninSql = "select id, count(1) num from t where name = ?   group    by id order by id desc";
        countSql = SqlUtils.generateCountSql(origninSql);
        assertEquals("SELECT COUNT(1) FROM (" + origninSql + ") AS tmp_count_result", countSql);

        origninSql = "select id, count(1) num from t where name = ?   \ngroup  \n  by id order by id desc";
        countSql = SqlUtils.generateCountSql(origninSql);
        assertEquals("SELECT COUNT(1) FROM (" + origninSql + ") AS tmp_count_result", countSql);

        origninSql = "select id, count(1) num from t where name = ?   \n  group \t \n  by\nid order by id desc";
        countSql = SqlUtils.generateCountSql(origninSql);
        assertEquals("SELECT COUNT(1) FROM (" + origninSql + ") AS tmp_count_result", countSql);
    }

}
