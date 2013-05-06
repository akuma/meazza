/* 
 * @(#)StringUtils.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

/**
 * 继承自 {@code org.apache.commons.lang3.StringUtils} 的字符串处理工具类。
 * 
 * @author akuma
 * @since 0.0.15
 */
public abstract class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 电子邮箱的正则表达式。
     */
    private static final String REGEX_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * 手机号码的正则表达式。
     */
    private static final String REGEX_MOBILE = "^1[3458]\\d{9}$";;

    /**
     * 移除字符串首尾的空白字符。如果字符串为 null 或者是空串，则直接返回原值。
     * 
     * @param str
     *            需要操作的字符串
     * @return 移除空白字符后的字符串
     */
    public static String strip(String str) {
        if (isEmpty(str)) {
            return str;
        }

        str = stripStart(str);
        str = stripEnd(str);
        return str;
    }

    /**
     * 移除字符串首尾的空白字符。如果字符串为 null 则返回空串。
     * 
     * @param str
     *            需要操作的字符串
     * @return 移除空白字符后的字符串
     */
    public static String stripToEmpty(String str) {
        return str == null ? EMPTY : strip(str);
    }

    /**
     * 移除字符串首的空白字符。
     * 
     * @param str
     * @return
     */
    public static String stripStart(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        int start = 0;
        while (start != strLen && isWhitespace(str.charAt(start))) {
            start++;
        }
        return str.substring(start);
    }

    /**
     * 移除字符串末尾的空白字符。
     * 
     * @param str
     * @return
     */
    public static String stripEnd(String str) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        while (end != 0 && isWhitespace(str.charAt(end - 1))) {
            end--;
        }
        return str.substring(0, end);
    }

    /**
     * 和 {@link org.apache.commons.lang3.StringUtils#isBlank(CharSequence)} 的区别是认为 non-breaking space (
     * {@code '\u005Cu00A0'}, {@code '\u005Cu2007'}, {@code '\u005Cu202F'}) 也是空白字符。
     * 
     * @param cs
     *            字符串
     * @return true/false
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }

        for (int i = 0; i < strLen; i++) {
            char c = cs.charAt(i);
            if (isWhitespace(c) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符是否是空白字符。
     * <em>和 {@link Character#isWhitespace(char)} 的区别是认为 non-breaking space ( {@code '\u005Cu00A0'}, {@code '\u005Cu2007'},
     * {@code '\u005Cu202F'}) 也是空白字符。</em>
     * 
     * @param ch
     *            需要测试的字符
     * @return true/false
     */
    public static boolean isWhitespace(char ch) {
        // 添加对 '\u005Cu00A0', '\u005Cu2007', '\u005Cu202F' 的判断
        return (Character.isWhitespace(ch) || ch == '\u00A0' || ch == '\u2007' || ch == '\u202F');
    }

    /**
     * 判断字符串是否是合法的电子邮箱地址。
     * 
     * @param str
     *            字符串
     * @return true/false
     */
    public static boolean isEmail(String str) {
        return isRegexMatch(str, REGEX_EMAIL);
    }

    /**
     * 是否为手机号码。此判断比较宽松，只要是以 1 开头的 11 位的数字，就认为是合法手机号码。
     * 
     * @param str
     *            字符串
     * @return 若是合法的手机号码返回 <code>true</code>, 否则返回 <code>false</code>.
     */
    public static boolean isMobile(String str) {
        return isRegexMatch(str, REGEX_MOBILE);
    }

    /**
     * 判断是否是合法的邮编，判断标准是 6 位全数字。
     * 
     * @param str
     *            字符串
     * @return true/false
     */
    public static boolean isPostcode(String str) {
        if (isBlank(str)) {
            return false;
        }

        return str.length() == 6 && isNumeric(str);
    }

    /**
     * 判断字符串是否匹配了正则表达式。
     * 
     * @param str
     *            字符串
     * @param regex
     *            正则表达式
     * @return true/false
     */
    public static boolean isRegexMatch(String str, String regex) {
        return str != null && str.matches(regex);
    }

    /**
     * 截取固定长度的字符串，超长部分用 <code>suffix</code> 代替，最终字符串真实长度不会超过 <code>maxLength</code>。
     * 
     * @param str
     *            被处理的字符串
     * @param maxLength
     *            处理返回的最大长度
     * @param suffix
     *            超出
     * @return 截取长度后的字符串
     */
    public static String cutOut(String str, int maxLength, String suffix) {
        if (isEmpty(str)) {
            return str;
        }

        int byteIndex = 0;
        int charIndex = 0;

        while (charIndex < str.length() && byteIndex <= maxLength) {
            char c = str.charAt(charIndex);
            if (c >= 256) {
                byteIndex += 2;
            } else {
                byteIndex++;
            }
            charIndex++;
        }

        if (byteIndex <= maxLength) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, charIndex));
        sb.append(suffix);

        while (getRealLength(sb.toString()) > maxLength) {
            sb.deleteCharAt(--charIndex);
        }

        return sb.toString();
    }

    /**
     * 取得字符串的真实长度，一个汉字长度为两个字节。
     * 
     * @param str
     *            字符串
     * @return 字符串的字节数
     */
    public static int getRealLength(String str) {
        if (str == null) {
            return 0;
        }

        char separator = 256;
        int realLength = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= separator) {
                realLength += 2;
            } else {
                realLength++;
            }
        }
        return realLength;
    }

}
