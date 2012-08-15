/* 
 * @(#)CustomStringToArrayConverter.java    Created on 2012-8-15
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 将字符串基于分割字符串分割后生成数组的自定义转换器。 <br>
 * Spring MVC 默认以参数中的逗号作为数组分割符，可能会带来一些问题。此类允许自由定义分隔符。<br>
 * <em>默认分割符为英文分号 ;</em>
 * 
 * @author akuma
 */
public class CustomStringToArrayConverter implements Converter<String, String[]> {

    private String delimiter = ";"; // 分割字符串

    @Override
    public String[] convert(String source) {
        return StringUtils.delimitedListToStringArray(source, delimiter);
    }

    /**
     * 设置字符串之间的分割字符串，分割之后的字符串都是数组的元素。
     * 
     * @param delimiter
     *            字符串分割符，默认为英文分号
     */
    public void setDelimiter(String delimiter) {
        Assert.notNull(delimiter, "The delimiter must not be null");
        this.delimiter = delimiter;
    }

}
