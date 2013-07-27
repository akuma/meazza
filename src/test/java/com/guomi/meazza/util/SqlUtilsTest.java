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

        String originSql = "select id, count(1) num from t where name = ? group by id order by id desc";
        countSql = SqlUtils.generateCountSql(originSql);
        assertEquals("SELECT COUNT(1) FROM (" + originSql + ") AS tmp_count_result", countSql);

        originSql = "select id, count(1) num from t where name = ?   group    by id order by id desc";
        countSql = SqlUtils.generateCountSql(originSql);
        assertEquals("SELECT COUNT(1) FROM (" + originSql + ") AS tmp_count_result", countSql);

        originSql = "select id, count(1) num from t where name = ?   \ngroup  \n  by id order by id desc";
        countSql = SqlUtils.generateCountSql(originSql);
        assertEquals("SELECT COUNT(1) FROM (" + originSql + ") AS tmp_count_result", countSql);

        originSql = "select id, count(1) num from t where name = ?   \n  group \t \n  by\nid order by id desc";
        countSql = SqlUtils.generateCountSql(originSql);
        assertEquals("SELECT COUNT(1) FROM (" + originSql + ") AS tmp_count_result", countSql);

        originSql = "select"
                + "            id as id,"
                + "            name as name,"
                + "            subject as subject,"
                + "            grade as grade,"
                + "            paper_type_id as paperTypeId"
                + "        from exam_paper\n           \n      \n   \n    \n      \n    \n    \n            \n"
                + "         WHERE  subject = 102\n               \n     \n     \n    \n     \n  \n     \n   \n    \n"
                + "                and grade = 11\n      \n     \n     \n    \n    \n       \n       \n     \n"
                + "                and school_id = 10002\n             \n\n             \n                   "
                + "\n             \n             \n             \n             \n             \n             "
                + "\n             \n             \n             \n             \n             \n             "
                + "\n             \n             \n             \n             \n             \n             "
                + "                and paper_type_id = '1'";

        long start = System.currentTimeMillis();
        countSql = SqlUtils.generateCountSql(originSql);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed);
    }

}
