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
        cal.set(2013, 4, 6);
        String str = DateUtils.formatToDate(cal.getTime());
        assertEquals("2013-05-06", str);
    }

    @Test
    public void testFormatToDateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 4, 6);
        String str = DateUtils.formatToDate(cal);
        assertEquals("2013-05-06", str);
    }

    @Test
    public void testFormatToDateTimeDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 4, 6, 15, 23, 49);
        String str = DateUtils.formatToDateTime(cal.getTime());
        assertEquals("2013-05-06 15:23:49", str);
    }

    @Test
    public void testFormatToDateTimeCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 4, 6, 15, 23, 49);
        String str = DateUtils.formatToDateTime(cal);
        assertEquals("2013-05-06 15:23:49", str);
    }

    @Test
    public void testFormatDateString() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 4, 6, 15, 23, 49);
        cal.set(Calendar.MILLISECOND, 998);
        String str = DateUtils.format(cal.getTime(), DateUtils.PATTERN_DATE_TIME_FULL);
        assertEquals("2013-05-06 15:23:49.998", str);
    }

    @Test
    public void testFormatCalendarString() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 4, 6, 15, 23, 49);
        cal.set(Calendar.MILLISECOND, 998);
        String str = DateUtils.format(cal, DateUtils.PATTERN_DATE_TIME_FULL);
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
        Date date = DateUtils.parse("2013-05-06 04:25:47.234", DateUtils.PATTERN_DATE_TIME_FULL);
        assertEquals("2013-05-06 04:25:47.234", DateUtils.format(date, DateUtils.PATTERN_DATE_TIME_FULL));
    }

    @Test
    public void testGetDateBegin() {
        Date start = DateUtils.getDateBegin(new Date());
        System.out.println(DateUtils.format(start, DateUtils.PATTERN_DATE_TIME_FULL));

        Date today = DateUtils.getTodayBegin();
        System.out.println(DateUtils.formatToDateTime(today));
        Date yestoday = DateUtils.getYestodyBegin();
        System.out.println(DateUtils.formatToDateTime(yestoday));

        Date monthStart = DateUtils.getMonthBegin(2013, 2);
        System.out.println(DateUtils.formatToDateTime(monthStart));
        monthStart = DateUtils.getMonthBegin(2013, 3);
        System.out.println(DateUtils.formatToDateTime(monthStart));
        monthStart = DateUtils.getMonthBegin(2013, 4);
        System.out.println(DateUtils.formatToDateTime(monthStart));
        monthStart = DateUtils.getMonthBegin(2013, 12);
        System.out.println(DateUtils.formatToDateTime(monthStart));

        monthStart = DateUtils.getMonthBegin();
        System.out.println(DateUtils.formatToDateTime(monthStart));
    }

    @Test
    public void testGetDateEnd() {
        Date end = DateUtils.getDateEnd(new Date());
        System.out.println(DateUtils.format(end, DateUtils.PATTERN_DATE_TIME_FULL));

        Date today = DateUtils.getTodayEnd();
        System.out.println(DateUtils.formatToDateTime(today));
        Date yestoday = DateUtils.getYestodyEnd();
        System.out.println(DateUtils.formatToDateTime(yestoday));

        Date monthEnd = DateUtils.getMonthEnd(2011, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2012, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2013, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2014, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2014, 7);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2014, 8);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2014, 11);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(2014, 12);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getMonthEnd(1, 4);
        System.out.println(DateUtils.formatToDateTime(monthEnd));

        monthEnd = DateUtils.getMonthEnd();
        System.out.println(DateUtils.formatToDateTime(monthEnd));
    }

    @Test
    public void testGetChineseWeek() {
        assertEquals(0, DateUtils.getChineseWeek(null));

        Date date = DateUtils.parseByDateTime("2013-12-28 04:25:47");
        assertEquals(6, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-23 04:25:47");
        assertEquals(4, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-24 04:25:47");
        assertEquals(5, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-25 04:25:47");
        assertEquals(6, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-26 04:25:47");
        assertEquals(7, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-27 04:25:47");
        assertEquals(1, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-28 04:25:47");
        assertEquals(2, DateUtils.getChineseWeek(date));

        date = DateUtils.parseByDateTime("2014-01-29 04:25:47");
        assertEquals(3, DateUtils.getChineseWeek(date));
    }

    @Test
    public void testGetChineseWeekOfYear() {
        Date date = DateUtils.parseByDateTime("2014-3-1 04:25:47");
        assertEquals(9, DateUtils.getChineseWeekOfYear(date));

        date = DateUtils.parseByDateTime("2014-3-2 04:25:47");
        assertEquals(9, DateUtils.getChineseWeekOfYear(date));

        date = DateUtils.parseByDateTime("2014-3-3 04:25:47");
        assertEquals(10, DateUtils.getChineseWeekOfYear(date));
    }

}
