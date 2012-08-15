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
 * 
 * @author akuma
 */
public class CustomStringToDateConverter implements Converter<String, Date> {

    private String datePattern = "yyyy-MM-dd";

    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat(datePattern);

        Date target = null;
        try {
            target = dateFormat.parse(source);
        } catch (ParseException e) {
            ; // Ignore
        }
        return target;
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
