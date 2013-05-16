/* 
 * @(#)DateUtils.java    Created on 2013-5-6
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 对日期进行转换处理的工具类。
 * 
 * @author akuma
 */
public abstract class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * 日期格式：yyyy-MM-dd
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * 日期格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String PATTERN_DATE_TIME_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String formatToDate(Date date) {
        if (date == null) {
            return null;
        }

        return DateFormatUtils.ISO_DATE_FORMAT.format(date);
    }

    public static String formatToDate(Calendar cal) {
        if (cal == null) {
            return null;
        }

        return DateFormatUtils.ISO_DATE_FORMAT.format(cal);
    }

    public static String formatToDateTime(Date date) {
        return format(date, PATTERN_DATE_TIME);
    }

    public static String formatToDateTime(Calendar cal) {
        return format(cal, PATTERN_DATE_TIME);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        return DateFormatUtils.format(date, pattern);
    }

    public static String format(Calendar cal, String pattern) {
        if (cal == null) {
            return null;
        }

        return DateFormatUtils.format(cal, pattern);
    }

    public static Date parseByDate(String str) {
        return parse(str, PATTERN_DATE);
    }

    public static Date parseByDateTime(String str) {
        return parse(str, PATTERN_DATE_TIME);
    }

    public static Date parse(String str, String pattern) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        Date result = null;
        try {
            result = parseDate(str, pattern);
        } catch (ParseException e) {
            // ignore
        }
        return result;
    }

    public static Date getBeginDate() {
        return getBeginDate(new Date());
    }

    public static Date getBeginDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return getBeginDate(cal.getTime());
    }

    public static Date getBeginDate(Date date) {
        if (date == null) {
            return null;
        }

        return truncate(date, Calendar.DATE);
    }

    public static Date getEndDate() {
        return getEndDate(new Date());
    }

    public static Date getEndDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndDate(cal.getTime());
    }

    public static Date getEndDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

}
