/* 
 * @(#)DateUtilsTest.java    Created on 2013-5-6
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    public void testGetBeginDate() {
        Date start = DateUtils.getBeginDate(new Date());
        System.out.println(DateUtils.format(start, DateUtils.PATTERN_DATE_TIME_FULL));

        final Calendar date = new GregorianCalendar();
        date.clear();
        date.set(2002, 3, 28, 13, 45, 01);
        System.out.println(date.getTime());
        System.out.println(DateUtils.ceiling(date, Calendar.HOUR).getTime());
        System.out.println(DateUtils.ceiling(date.getTime(), Calendar.HOUR));
        System.out.println(DateUtils.truncate(date, Calendar.HOUR).getTime());
        System.out.println(DateUtils.truncate(date.getTime(), Calendar.HOUR));

        Date monthStart = DateUtils.getBeginDate(2013, 2);
        System.out.println(DateUtils.formatToDateTime(monthStart));
        monthStart = DateUtils.getBeginDate(2013, 3);
        System.out.println(DateUtils.formatToDateTime(monthStart));
        monthStart = DateUtils.getBeginDate(2013, 4);
        System.out.println(DateUtils.formatToDateTime(monthStart));
        monthStart = DateUtils.getBeginDate(2013, 12);
        System.out.println(DateUtils.formatToDateTime(monthStart));
    }

    @Test
    public void testGetEndDate() {
        Date end = DateUtils.getEndDate(new Date());
        System.out.println(DateUtils.format(end, DateUtils.PATTERN_DATE_TIME_FULL));

        Date monthEnd = DateUtils.getEndDate(2011, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2012, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2013, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2014, 2);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2014, 7);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2014, 8);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2014, 11);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(2014, 12);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
        monthEnd = DateUtils.getEndDate(1, 4);
        System.out.println(DateUtils.formatToDateTime(monthEnd));
    }

}
