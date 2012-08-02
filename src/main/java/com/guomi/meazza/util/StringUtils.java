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
     * HTML 文本过滤，如果 value 为 <code>null</code> 或为空串，则返回 "&amp;nbsp;"。
     * 
     * <p>
     * 转换的字符串关系如下：
     * 
     * <ul>
     * <li>&amp; --> &amp;amp;</li>
     * <li>&lt; --> &amp;lt;</li>
     * <li>&gt; --> &amp;gt;</li>
     * <li>&quot; --> &amp;quot;</li>
     * <li>\n --> &lt;br/&gt;</li>
     * <li>\t --> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</li>
     * <li>空格 --> &amp;nbsp;</li>
     * </ul>
     * 
     * <strong>此方法适用于在 HTML 页面上的非文本框元素（div、span、table 等）中显示文本时调用。</strong>
     * 
     * @param html
     *            要过滤的文本
     * @return 过滤后的 HTML 文本
     */
    public static String htmlFilter(String html) {
        if (html == null || html.length() == 0) {
            return "&nbsp;";
        }

        return html.replaceAll("&", "&amp;").replaceAll("\t", "    ").replaceAll(" ", "&nbsp;")
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("\n", "<br/>");
    }

    /**
     * HTML 文本过滤，如果 value 为 <code>null</code> 或为空串，则返回空串。
     * 
     * <p>
     * 转换的字符串关系如下：
     * 
     * <ul>
     * <li>&amp; --> &amp;amp;</li>
     * <li>&lt; --> &amp;lt;</li>
     * <li>&gt; --> &amp;gt;</li>
     * <li>&quot; --> &amp;quot;</li>
     * <li>\n --> &lt;br/&gt;</li>
     * </ul>
     * 
     * <strong>此方法适用于在 HTML 页面上的文本框（text、textarea）中显示文本时调用。</strong>
     * 
     * @param html
     *            要过滤的文本
     * @return 过滤后的 HTML 文本
     */
    public static String htmlFilterToEmpty(String html) {
        if (html == null || html.length() == 0) {
            return "";
        }

        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;");
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
     * @return
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
