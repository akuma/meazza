/*
 * @(#)DateUtils.java    Created on 2013-5-6
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 对日期进行转换处理的工具类。
 * 
 * @author akuma
 */
public abstract class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * 日期格式：yyyyMMdd
     */
    public static final String PATTERN_DATE_SHORT = "yyyyMMdd";

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

    /**
     * 日期格式：yyyy-MM
     */
    public static final String PATTERN_YEAR_MONTH = "yyyy-MM";

    public static String format(Calendar cal, String pattern) {
        if (cal == null) {
            return null;
        }

        return DateFormatUtils.format(cal, pattern);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        return DateFormatUtils.format(date, pattern);
    }

    public static String formatToDate(Calendar cal) {
        if (cal == null) {
            return null;
        }

        return DateFormatUtils.ISO_DATE_FORMAT.format(cal);
    }

    public static String formatToDate(Date date) {
        if (date == null) {
            return null;
        }

        return DateFormatUtils.ISO_DATE_FORMAT.format(date);
    }

    public static String formatToDateTime(Calendar cal) {
        return format(cal, PATTERN_DATE_TIME);
    }

    public static String formatToDateTime(Date date) {
        return format(date, PATTERN_DATE_TIME);
    }

    /**
     * 获取某天的开始时间。
     */
    public static Date getDateBegin(Date date) {
        if (date == null) {
            return null;
        }

        return truncate(date, Calendar.DATE);
    }

    /**
     * 获取某天的开始时间和结束时间。
     */
    public static Pair<Date, Date> getDateBeginAndEnd(Date date) {
        return Pair.of(getDateBegin(date), getDateEnd(date));
    }

    /**
     * 获取某天的结束时间。
     */
    public static Date getDateEnd(Date date) {
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

    /**
     * 获取当前月的第一天的开始时间。
     */
    public static Date getMonthBegin() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return getMonthBegin(year, month);
    }

    /**
     * 获取某年中某月的第一天的开始时间。
     */
    public static Date getMonthBegin(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return getDateBegin(cal.getTime());
    }

    /**
     * 获取当前月的第一天的开始时间和最后一天的结束时间。
     */
    public static Pair<Date, Date> getMonthBeginAndEnd() {
        return Pair.of(getMonthBegin(), getMonthEnd());
    }

    /**
     * 获取某年中的某月的第一天的开始时间和最后一天的结束时间。
     */
    public static Pair<Date, Date> getMonthBeginAndEnd(int year, int month) {
        return Pair.of(getMonthBegin(year, month), getMonthEnd(year, month));
    }

    /**
     * 获取当前月的最后一天的结束时间。
     */
    public static Date getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return getMonthEnd(year, month);
    }

    /**
     * 获取某年中某月的最后一天的结束时间。
     */
    public static Date getMonthEnd(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getDateEnd(cal.getTime());
    }

    /**
     * 获取今天开始时间。
     */
    public static Date getTodayBegin() {
        return getDateBegin(new Date());
    }

    /**
     * 获取今天的开始时间和结束时间。
     */
    public static Pair<Date, Date> getTodayBeginAndEnd() {
        return Pair.of(getTodayBegin(), getTodayEnd());
    }

    /**
     * 获取今天的结束时间。
     */
    public static Date getTodayEnd() {
        return getDateEnd(new Date());
    }

    /**
     * 获取昨天开始时间。
     */
    public static Date getYestodyBegin() {
        Date todayBegin = getTodayBegin();
        return todayBegin == null ? null : DateUtils.addDays(todayBegin, -1);
    }

    /**
     * 获取昨天的开始时间和结束时间。
     */
    public static Pair<Date, Date> getYestodyBeginAndEnd() {
        return Pair.of(getYestodyBegin(), getYestodyEnd());
    }

    /**
     * 获取昨天的结束时间。
     */
    public static Date getYestodyEnd() {
        Date todayEnd = getTodayEnd();
        return todayEnd == null ? null : DateUtils.addDays(todayEnd, -1);
    }

    /**
     * 根据日期对象获取年份。如果 <code>date</code> 为 null，则返回 0。
     */
    public static int getYear(Date date) {
        if (date == null) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 根据日期对象获取月份。
     * 
     * <p>
     * <strong>返回值说明：</strong>
     * <ul>
     * <li>当 date 为 null 时，返回 0</li>
     * <li>其他情况返回 1-12 的数字</li>
     * </ul>
     */
    public static int getMonth(Date date) {
        if (date == null) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据日期对象获取星期几，根据中国人的传统将星期一作为星期的第一天。
     * 
     * <p>
     * <strong>返回值说明：</strong>
     * <ul>
     * <li>当 date 为 null 时，返回 0</li>
     * <li>其他情况返回 1-7 的数字</li>
     * <li>其中，星期一是 1，星期天是 7</li>
     * </ul>
     */
    public static int getWeek(Date date) {
        if (date == null) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 1 ? 7 : dayOfWeek - 1;
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

    public static Date parseByDate(String str) {
        return parse(str, PATTERN_DATE);
    }

    public static Date parseByDateTime(String str) {
        return parse(str, PATTERN_DATE_TIME);
    }

}
