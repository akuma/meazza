/* 
 * @(#)PatternMatcher.java    Created on 2012-7-18
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

/**
 * 此类引用自 Apache Shiro，用于验证字符串是否和某个规则匹配。规则可以是 Ant 方式的，也可以是正则表达式，或者是其他方式。
 * 之所以直接引用此类是因为不想让 meazza 在工具类上必须依赖于 Apache Shiro。
 * 
 * @author akuma
 * @see RegexPathMatcher
 */
public interface PatternMatcher {

    /**
     * 如果给定的 <code>source</code> 和 <code>pattern</code> 匹配，则返回 <code>true</code>，否则返回 <code>false</code>。
     * 
     * @param pattern
     *            匹配规则
     * @param source
     *            需要匹配的字符串
     * @return 如果 <code>source</code> 和 <code>pattern</code> 匹配，则返回 <code>true</code>，否则返回 <code>false</code>。
     */
    boolean matches(String pattern, String source);

}
