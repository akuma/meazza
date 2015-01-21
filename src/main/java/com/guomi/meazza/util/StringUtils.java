/*
 * @(#)StringUtils.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
    private static final String REGEX_MOBILE = "^1[3458]\\d{9}$";

    /**
     * UUID 的正则表达式。
     */
    private static final String REGEX_UUID = "^[0-9a-fA-F]{32}$";

    // 表示 true、false 的字符串
    private static final String BOOLEAN_TRUE_STRING = "true";
    private static final String BOOLEAN_FALSE_STRING = "false";
    private static final String BOOLEAN_TRUE_NUMBER = "1";
    private static final String BOOLEAN_FALSE_NUMBER = "0";

    private static final Map<String, String> EMAIL_WEBSITES = new HashMap<>();
    static {
        EMAIL_WEBSITES.put("gmail.com", "http://gmail.com");
        EMAIL_WEBSITES.put("hotmail.com", "http://hotmail.com");
    }

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
     * 判断字符串是否是 UUID。
     */
    public static boolean isUuid(String str) {
        return isRegexMatch(str, REGEX_UUID);
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

    /**
     * 对字符串以指定的 <code>separatorChars</code> 进行分割，然后转换为 <code>Integer</code> 数组。
     *
     * @throws NumberFormatException
     *             当数字转换出错时会抛出此异常
     */
    public static List<Integer> splitToIntList(String str, String separatorChars) {
        if (isBlank(str)) {
            return Collections.emptyList();
        }

        String[] array = split(str, separatorChars);
        if (ArrayUtils.isEmpty(array)) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>();
        for (String s : array) {
            if (isBlank(s)) {
                continue;
            }
            Integer i = NumberUtils.createInteger(s.trim());
            if (i != null) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * 对字符串以指定的 <code>separatorChars</code> 进行分割，然后转换为 <code>Long</code> 数组。
     *
     * @throws NumberFormatException
     *             当数字转换出错时会抛出此异常
     */
    public static List<Long> splitToLongList(String str, String separatorChars) {
        if (isBlank(str)) {
            return Collections.emptyList();
        }

        String[] array = split(str, separatorChars);
        if (ArrayUtils.isEmpty(array)) {
            return Collections.emptyList();
        }

        List<Long> result = new ArrayList<>();
        for (String s : array) {
            if (isBlank(s)) {
                continue;
            }
            Long i = NumberUtils.createLong(s.trim());
            if (i != null) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * 把所有参数按照 key 的字典顺序排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串。其中对参数值会进行 URL 编码。
     *
     * @param params
     *            需要排序并参与字符拼接的参数
     * @return 拼接后的字符串
     */
    public static String createSortedQueryString(Map<String, String> params) {
        return createSortedQueryString(params, true);
    }

    /**
     * 把所有参数按照 key 的字典顺序排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串。
     *
     * @param params
     *            需要排序并参与字符拼接的参数
     * @param encodeParamValue
     *            是否对参数值进行 URL 编码
     * @return 拼接后的字符串
     */
    public static String createSortedQueryString(Map<String, String> params, boolean encodeParamValue) {
        if (MapUtils.isEmpty(params)) {
            return EMPTY;
        }

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (encodeParamValue) {
                value = URLUtils.encode(value, "utf-8");
            }

            if (i == keys.size() - 1) { // 拼接时，不包括最后一个&字符
                sb.append(key + "=" + value);
            } else {
                sb.append(key + "=" + value + "&");
            }
        }

        return sb.toString();
    }

    /**
     * 判断 value 的值是否表示条件为真。例子：
     *
     * <ul>
     * <li>"1" -> true</li>
     * <li>"true" -> true</li>
     * <li>"True" -> true</li>
     * <li>"TRUE" -> true</li>
     * <li>"2" -> false</li>
     * <li>"false" -> false</li>
     * <li>"test" -> false</li>
     * </ul>
     *
     * @param str
     *            字符串
     * @return 如果 value 等于 “1” 或者 “true”（大小写无关） 返回 <code>true</code>，否则返回 <code>false</code>。
     */
    public static boolean isValueTrue(String str) {
        return BOOLEAN_TRUE_NUMBER.equals(str) || BOOLEAN_TRUE_STRING.equalsIgnoreCase(str);
    }

    /**
     * 判断 value 的值是否表示条件为假。例子：
     *
     * <ul>
     * <li>"0" -> true</li>
     * <li>"false" -> true</li>
     * <li>"False" -> true</li>
     * <li>"FALSE" -> true</li>
     * <li>"1" -> false</li>
     * <li>"true" -> false</li>
     * <li>"test" -> false</li>
     * </ul>
     *
     * @param str
     *            字符串
     * @return 如果 value 等于 “0” 或者 “false”（大小写无关） 返回 <code>true</code>，否则返回 <code>false</code>。
     */
    public static boolean isValueFalse(String str) {
        return BOOLEAN_FALSE_NUMBER.equals(str) || BOOLEAN_FALSE_STRING.equalsIgnoreCase(str);
    }

    /**
     * 判断字符串是否全是 A-Z 的英文字母（包括大小写）。
     */
    public static boolean isEnglishLetter(String str) {
        if (isBlank(str)) {
            return false;
        }

        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是字符串是否是汉字。
     */
    public static boolean isChineseCharacter(String str) {
        if (isBlank(str)) {
            return false;
        }

        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if ((c >= 0x4e00) && (c <= 0x9fbb)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否数字，包括整数、浮点数。
     */
    public static boolean isNumber(String str) {
        if (isBlank(str)) {
            return false;
        }

        return isRegexMatch(str, "\\d+(\\.\\d+)?");
    }

    /**
     * 判断字符串是否是浮点数。
     */
    public static boolean isFloatNumber(String str) {
        if (isBlank(str)) {
            return false;
        }

        return isRegexMatch(str, "\\d+\\.\\d+");
    }

    /**
     * 根据数字返回一个数字文件路径前缀，路径中的文件夹由除末两位外的其他数字按序组成。例如：
     *
     * <ul>
     * <li>-1:</li>
     * <li>0:</li>
     * <li>1:</li>
     * <li>1001: /10</li>
     * <li>101101: /10/11</li>
     * <li>1101101: 1/10/11</li>
     * <li>12101101: 12/10/11</li>
     * </ul>
     */
    public static String getNumberPrefixPath(long number) {
        if (number <= 0) {
            return EMPTY;
        }

        StringBuilder dir = new StringBuilder();
        long subdir = number;
        while (subdir > 100) {
            subdir = subdir / 100;
            dir.insert(0, "/" + subdir % 100);
        }
        return dir.toString();
    }

    /**
     * 根据邮箱获取邮箱所在的网站网址。例如：
     * <ul>
     * <li>foo@qq.com => http://mail.qq.com</li>
     * <li>foo@163.com => http://mail.163.com</li>
     * <li>foo@zuihuixue.com => http://mail.zuihuixue.com</li>
     * <li>foo@msn.com => http://mail.msn.com</li>
     * <li>foo@hotmail.com => http://hotmail.com</li>
     * <li>foo@gmail.com => http://gmail.com</li>
     * <li>test.com => null</li>
     * <li>"" => null</li>
     * <li>null => null</li>
     * </ul>
     */
    public static String getEmailWebsite(String email) {
        if (!isEmail(email)) {
            return null;
        }

        String domain = email.substring(email.indexOf("@") + 1);
        String website = EMAIL_WEBSITES.get(domain);
        return website != null ? website : "http://mail." + domain;
    }

    /**
     * 将文本内容中的多个换行替换为单个换行。
     */
    public static String removeMultiLines(String content) {
        if (isBlank(content)) {
            return content;
        }

        return content.replaceAll("[\r\n]+", "\r\n"); // 将多个换行替换为一个换行
    }

    /**
     * 将文本内容中的空格、\n 分别替换为 html 的 &amp;nbsp、&lt;br&gt;，这样在网页上显示该文本时就能保持一定的格式。
     */
    public static String textToHtml(String content) {
        if (isBlank(content)) {
            return content;
        }

        return content.replace(" ", "&nbsp;").replaceAll("\n", "<br>");
    }

}
