/* 
 * @(#)CustomStringToNumberConverterFactory.java    Created on 2013-1-21
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;

import com.guomi.meazza.util.StringUtils;

/**
 * 自定义的 字符串-数字 转换器工厂类。<br>
 * 和 Spring Framework 的 {@link org.springframework.core.convert.support.StringToNumberConverterFactory}
 * 的区别是，如果出现格式错误不会抛出异常，而是将返回结果设置为 <code>null</code>。
 * 
 * @author akuma
 */
public class CustomStringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber<T>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }

            String trimmed = StringUtils.trim(source);

            T result = null;
            try {
                result = NumberUtils.parseNumber(trimmed, targetType);
            } catch (Exception e) {
                // Just ignore convert error
            }
            return result;
        }
    }

}
