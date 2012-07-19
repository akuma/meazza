/* 
 * @(#)RegexPathMatcher.java    Created on 2012-7-18
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.util.regex.Pattern;

/**
 * 基于正则表达式的路径匹配器。
 * 
 * @author akuma
 * @since 0.0.7
 */
public class RegexPathMatcher implements PatternMatcher {

    @Override
    public boolean matches(String pattern, String source) {
        return Pattern.matches(pattern, source);
    }

}
