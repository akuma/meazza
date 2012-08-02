/* 
 * @(#)HtmlUtils.java    Created on 2012-7-28
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

/**
 * 包含处理 HTML 代码方法的工具类。
 * 
 * @author akuma
 * @since 0.0.15
 */
public abstract class HtmlUtils {

    /**
     * 将 HTML 代码中的标签、转义字符删除，只保留表示内容的文本。<br>
     * <b>注意：script、style 等节点中的文字不会被过滤。</b>
     * 
     * @param html
     *            HTML 代码
     * @return 移除 HTML 标签、转义字符之后的文本
     */
    public static String stripHtml(String html) {
        if (html == null || html.trim().length() == 0) {
            return html;
        }

        // 移除 html 标签
        String noHtml = html.replaceAll("\\<.*?\\>", "");

        // 移除 html 转义字符
        noHtml = noHtml.replaceAll("&.*?;", "");

        // 将回车、换行符号替换为空格
        noHtml = noHtml.replaceAll("\r", " ").replaceAll("\n", " ");
        return noHtml;
    }

    /**
     * HTML 文本过滤，如果 value 为 <code>null</code> 或为空串，则返回 "&amp;nbsp;"。
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
    public static String escapeHtml(String html) {
        if (html == null || html.length() == 0) {
            return "&nbsp;";
        }

        return html.replaceAll("&", "&amp;").replaceAll("\t", "    ").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("\n", "<br/>");
    }

    /**
     * HTML 文本过滤，如果 value 为 <code>null</code> 或为空串，则返回空串。
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
    public static String escapeHtmlToEmpty(String html) {
        if (html == null || html.length() == 0) {
            return "";
        }

        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

}
