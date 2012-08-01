/* 
 * @(#)SqlCreatorTest.java    Created on 2012-8-1
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author akuma
 */
public class SqlCreatorTest {

    @Test
    public void testGetCountSQL() {
        String normalSql = "select * from base_user";
        String countSql = "select COUNT(1) from base_user";
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = "select * from base_user where id =?";
        countSql = "select COUNT(1) from base_user where id =?";
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = "select * from base_user where id =? order by unit_id";
        countSql = "select COUNT(1) from base_user where id =?";
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = "select * from base_user group by dept_id";
        countSql = "select count(1) from (select * from base_user group by dept_id) temp_rs";
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = "select * from base_user where unit_id = ? group by dept_id";
        countSql = "select count(1) from (select * from base_user where unit_id = ? group by dept_id) temp_rs";
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = "select id, name from test where id = 'test'";
        countSql = SqlCreator.getCountSQL("select count(1) from test where id = 'test'");
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = SqlCreator.getCountSQL("select sch.name school_name,adcsch.ec_code,sch.region_id,a.name,"
                + "a.card_number1 card_number,a.phone,r.school_id,r.result,r.record_time,"
                + "r.creation_time,r.device_id FROM su_account_executive a,si_record r,eb_school sch "
                + "left join adc_school adcsch on adcsch.school_id=sch.id WHERE a.card_number1 = r.card_number "
                + "and a.id = r.owner_id and sch.id=r.school_id UNION ALL "
                + "select sch.name school_name,adcsch.ec_code,sch.region_id,a.name,a.card_number2 card_number,"
                + "a.phone,r.school_id,r.result,r.record_time,r.creation_time,r.device_id "
                + "FROM su_account_executive a,si_record r,eb_school sch "
                + "left join adc_school adcsch on adcsch.school_id=sch.id WHERE a.card_number2 = r.card_number"
                + " and a.id = r.owner_id and sch.id=r.school_id");
        countSql = SqlCreator
                .getCountSQL("select count(1) from (select sch.name school_name,adcsch.ec_code,sch.region_id,a.name,"
                        + "a.card_number1 card_number,a.phone,r.school_id,r.result,r.record_time,"
                        + "r.creation_time,r.device_id FROM su_account_executive a,si_record r,eb_school sch "
                        + "left join adc_school adcsch on adcsch.school_id=sch.id WHERE a.card_number1 = r.card_number "
                        + "and a.id = r.owner_id and sch.id=r.school_id UNION ALL "
                        + "select sch.name school_name,adcsch.ec_code,sch.region_id,a.name,a.card_number2 card_number,"
                        + "a.phone,r.school_id,r.result,r.record_time,r.creation_time,r.device_id "
                        + "FROM su_account_executive a,si_record r,eb_school sch "
                        + "left join adc_school adcsch on adcsch.school_id=sch.id WHERE a.card_number2 = r.card_number"
                        + " and a.id = r.owner_id and sch.id=r.school_id) temp_rs");
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));

        normalSql = SqlCreator.getCountSQL("select id, name from test group by id");
        countSql = SqlCreator.getCountSQL("select count(1) from (select id, name from test group by id) temp_rs");
        assertTrue(countSql.equalsIgnoreCase(SqlCreator.getCountSQL(normalSql)));
    }

}
