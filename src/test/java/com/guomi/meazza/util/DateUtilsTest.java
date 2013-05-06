/* 
 * @(#)DateUtilsTest.java    Created on 2013-5-6
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * @author akuma
 */
public class DateUtilsTest {

    @Test
    public void testFormatToDateDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 6);
        String str = DateUtils.formatToDate(cal.getTime());
        assertEquals("2013-05-06", str);
    }

    @Test
    public void testFormatToDateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONDAY, 4);
        cal.set(Calendar.DATE, 6);
        String str = DateUtils.formatToDate(cal);
        assertEquals("2013-05-06", str);
    }

    @Test
    public void testFormatToDateTimeDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 49);
        String str = DateUtils.formatToDateTime(cal.getTime());
        assertEquals("2013-05-06 15:23:49", str);
    }

    @Test
    public void testFormatToDateTimeCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 49);
        String str = DateUtils.formatToDateTime(cal);
        assertEquals("2013-05-06 15:23:49", str);
    }

    @Test
    public void testFormatDateString() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 49);
        cal.set(Calendar.MILLISECOND, 998);
        String str = DateUtils.format(cal.getTime(), "yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals("2013-05-06 15:23:49.998", str);
    }

    @Test
    public void testFormatCalendarString() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 49);
        cal.set(Calendar.MILLISECOND, 998);
        String str = DateUtils.format(cal, "yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals("2013-05-06 15:23:49.998", str);
    }

    @Test
    public void testParseByDate() {
        Date date = DateUtils.parseByDate("2013-05-06");
        assertEquals("2013-05-06", DateUtils.formatToDate(date));
    }

    @Test
    public void testParseByDateTime() {
        Date date = DateUtils.parseByDateTime("2013-05-06 04:25:47");
        assertEquals("2013-05-06 04:25:47", DateUtils.formatToDateTime(date));
    }

    @Test
    public void testParse() {
        Date date = DateUtils.parse("2013-05-06 04:25:47.234", "yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals("2013-05-06 04:25:47.234", DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
    }

}
