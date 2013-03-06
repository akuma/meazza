/* 
 * @(#)CustomStringToDateConverter.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.convert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import com.guomi.meazza.util.StringUtils;

/**
 * 将字符串转换为日期对象的自定义转换器。
 * <p>
 * 默认支持的日期格式如下：
 * <ul>
 * <li>yyyy-MM-dd</li>
 * <li>yyyy-MM-dd HH</li>
 * <li>yyyy-MM-dd HH:mm</li>
 * <li>yyyy-MM-dd HH:mm:ss</li>
 * </ul>
 * 
 * @author akuma
 */
public class CustomStringToDateConverter implements Converter<String, Date> {

    private String datePattern = "yyyy-MM-dd";

    // private final String DATE_PATTERN_REGEX = "\\d{4}\\-\\d{1,2}\\-\\d{1,2}";
    private final String HOUR_PATTERN_REGEX = "\\d{1,2}";
    private final String HOUR_MINUTE_PATTERN_REGEX = "\\d{1,2}:\\d{1,2}";
    private final String HOUR_MINUTE_SECOND_PATTERN_REGEX = "\\d{1,2}:\\d{1,2}:\\d{1,2}";

    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        DateFormat dateFormat = null;

        source = source.trim();
        String[] parts = source.split(" +");
        if (parts.length == 2) {
            String timeParts = parts[1].trim();
            if (StringUtils.isRegexMatch(timeParts, HOUR_PATTERN_REGEX)) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
            } else if (StringUtils.isRegexMatch(timeParts, HOUR_MINUTE_PATTERN_REGEX)) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            } else if (StringUtils.isRegexMatch(timeParts, HOUR_MINUTE_SECOND_PATTERN_REGEX)) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
        }

        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(datePattern);
        }

        Date retVal = null;
        try {
            retVal = dateFormat.parse(source);
        } catch (ParseException e) {
            ; // Ignore
        }
        return retVal;
    }

    /**
     * 设置日期的格式，默认为：yyyy-MM-dd。
     * 
     * @param datePattern
     *            日期的格式
     */
    public void setDatePattern(String datePattern) {
        Assert.notNull(datePattern, "The datePattern must not be null");
        this.datePattern = datePattern;
    }

}
