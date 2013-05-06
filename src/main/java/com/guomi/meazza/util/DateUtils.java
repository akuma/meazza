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

    private static final String PATTERN_DATE = "yyyy-MM-dd";
    private static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

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
        Date result = null;
        try {
            result = parseDate(str, pattern);
        } catch (ParseException e) {
            // ignore
        }
        return result;
    }

}
