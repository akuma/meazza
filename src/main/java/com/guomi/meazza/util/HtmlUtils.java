/* 
 * @(#)HtmlUtils.java    Created on 2012-7-28
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 包含处理 HTML 代码方法的工具类。
 * 
 * @author akuma
 * @since 0.0.15
 */
public abstract class HtmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    // private static final Pattern START_NBSP_REPLACE_REGEX = Pattern.compile("^(&nbsp;)+");

    /**
     * 默认在 jsoup 的 {@link org.jsoup.safety.Whitelist#basicWithImages()} 白名单的基础上添加 div、table 标签
     */
    private static final Whitelist whitelist = Whitelist.basicWithImages()
            .addTags("div", "table", "tbody", "td", "tfoot", "th", "thead", "tr").addAttributes("div", "style")
            .addAttributes("p", "style").addAttributes("table", "width")
            .addAttributes("td", "colspan", "rowspan", "width").addAttributes("th", "colspan", "rowspan", "width")
            .preserveRelativeLinks(true);

    private static final String DEFAULT_JSOUP_BASE_URI = "http://static.gm.com";

    public static String stripHtml(String html) {
        if (StringUtils.isBlank(html)) {
            return html;
        }

        Document doc = Jsoup.parse(html);
        String text = doc.body().text();
        // jsoup 会把 &nbsp; 替换为 \u00A0 字符，这里再替换为普通空格
        return StringUtils.strip(text).replaceAll("\u00A0", " ");
    }

    /**
     * 将 HTML 代码中的标签、转义字符删除，只保留表示内容的文本。<br>
     * <em>注意：script、style 等节点中的文字不会被过滤。</em>
     * 
     * @param html
     *            HTML 代码
     * @return 移除 HTML 标签、转义字符之后的文本
     */
    public static String stripHtmlToEmpty(String html) {
        if (StringUtils.isBlank(html)) {
            return StringUtils.EMPTY;
        }

        return stripHtml(html);
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

    /**
     * 判断 HTML 代码是否为空白。如果 {@code html} 只包含空串、空格、空的 html 标签则认为 HTML 代码为空白。
     * 
     * @param html
     *            需要测试的 html 代码
     * @return true/false
     */
    public static boolean isBlank(String html) {
        if (StringUtils.isBlank(html)) {
            return true;
        }

        String striped = stripHtmlToEmpty(html);
        return StringUtils.isBlank(striped);
    }

    /**
     * 判断 HTML 代码是否为空。如果 {@code html} 只包含空串、空格、&amp;nbsp; 则认为 HTML 代码为空。
     * 
     * @param html
     *            需要测试的 html 代码
     * @return true/false
     */
    public static boolean isEmpty(String html) {
        if (StringUtils.isBlank(html)) {
            return true;
        }

        return Pattern.matches("(&nbsp;| )+", html);
    }

    /**
     * 处理从编辑器输入的 HTML 代码，输出更加安全的代码。
     * <p>
     * 删除首尾的空白字符（空串、空格、回车、换行等）、空白标签、&amp;&nbsp;，并且过滤掉不包含在白名单的标签、标签属性。
     * 
     * @param html
     *            需要处理的 HTML 代码
     * @return 处理之后的 HTML 代码
     */
    public static String cleanEditorHtml(String html) {
        if (StringUtils.isBlank(html)) {
            return StringUtils.EMPTY;
        }

        // 移除首尾空白字符
        String stripedHtml = StringUtils.stripToEmpty(html);

        // 对 html 标签、标签属性进行白名单过滤，得到安全的 html 代码
        String safeHtml = Jsoup.clean(stripedHtml, DEFAULT_JSOUP_BASE_URI, whitelist);
        logger.trace("safeHtml: {}", safeHtml);

        // 解析 html 代码
        Document doc = Jsoup.parse(safeHtml);

        if (logger.isTraceEnabled()) {
            logger.trace("Body html (Before remove start blank tags):\n{}\n", doc.body().html());
            logger.trace("Body children size (Before remove start blank tags): {}", doc.body().childNodes().size());
        }
        stripStartBlankNodes(doc.body());
        if (logger.isTraceEnabled()) {
            logger.trace("Body html (After removed start blank tags):\n{}\n", doc.body().html());
            logger.trace("Body children size (After removed start blank tags): {}", doc.body().childNodes().size());
        }

        stripEndBlankNodes(doc.body());
        if (logger.isTraceEnabled()) {
            logger.trace("Body html (After removed end blank tags):\n{}\n", doc.body().html());
            logger.trace("Body children size (After removed end blank tags): {}", doc.body().childNodes().size());
        }

        convertNodePToDiv(doc.body());
        unwrapFirstNotBlankBlock(doc.body());

        return doc.body().html();
    }

    /**
     * 获取 jsoup 白名单对象。<em>修改该对象属性的时候需要注意线程安全问题。</em>
     * 
     * @return jsoup 白名单对象
     */
    public static Whitelist getWhitelist() {
        return whitelist;
    }

    /**
     * 删除 HTML 元素左边所有连续的空白字符、空节点。
     */
    private static boolean stripStartBlankNodes(Element element) {
        // 查找直属于当前元素的空白文本节点并将内容设置为空串
        // 对于非空白的文本节点，将文字左边的空白字符删除
        List<TextNode> childNodes = element.textNodes();
        for (TextNode tn : childNodes) {
            if (StringUtils.isBlank(tn.text())) {
                tn.text("");
            } else {
                tn.text(StringUtils.stripStart(tn.text()));
            }
        }

        Elements children = element.children();
        for (Element c : children) {
            // 如果发现空节点则标记为待删除
            boolean canElementRemove = false;
            if (isBlankElement(c)) {
                canElementRemove = true;
            }

            // 查找和当前元素相邻的空白文本节点并删除
            // 对于非空白的文本节点，将文字左边的空白字符删除并结束递归
            Node node = c.nextSibling();
            if (node instanceof TextNode) {
                TextNode tn = (TextNode) node;
                if (StringUtils.isBlank(tn.text())) {
                    tn.remove();
                } else {
                    logger.trace("Strip start on textNode: {}", tn);
                    tn.text(StringUtils.stripStart(tn.text()));

                    if (canElementRemove) {
                        c.remove();
                        logger.debug("Removed element:\n{}\n", c);
                    }

                    return true;
                }
            }

            // 按需删除当前元素
            if (canElementRemove) {
                c.remove();
                continue;
            }

            // 递归子节点
            if (stripStartBlankNodes(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 删除 HTMl 元素中右边所有连续的空白字符、空节点。
     * TODO 目前对于直接位于 body 的空白文本不会删除，待完善
     */
    private static boolean stripEndBlankNodes(Element element) {
        Elements children = element.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            Element c = children.get(i);
            // 如果发现空节点则删除掉
            if (isBlankElement(c)) {
                c.remove();
                continue;
            }

            // 如果节点不空白且无子节点，表明已经找到了 HTML 最后一个非空节点，跳出本次循环并结束递归
            if (c.children().isEmpty()) {
                return true;
            }

            // 递归子节点
            if (stripEndBlankNodes(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断节点是否是空白的，图片节点不算空白。
     */
    private static boolean isBlankElement(Element element) {
        return StringUtils.isBlank(element.text()) && element.select("img").isEmpty();
    }

    /**
     * 将所有 {@code <p>} 标签转换为 {@code <div>} 标签。
     */
    private static void convertNodePToDiv(Element element) {
        Elements elements = element.select("p");
        for (Element e : elements) {
            Element div = new Element(Tag.valueOf("div"), "");
            div.html(e.html());
            // 尝试将 <p> 的样式复制到 <div>
            if (!StringUtils.isBlank(e.attr("style"))) {
                div.attr("style", e.attr("style"));
            }
            e.replaceWith(div);
            logger.trace("Replaced {} with {}", e.nodeName(), div.nodeName());
        }
    }

    /**
     * 将块元素中的第一个非空白文本节点独立成 body 的第一个节点。
     */
    private static boolean unwrapFirstNotBlankBlock(Node node) {
        List<Node> childNodes = node.childNodes();
        for (Node child : childNodes) {
            // 如果是文本节点，且节点内容为空白，则继续递归，否则结束递归
            if (child instanceof TextNode) {
                TextNode tn = (TextNode) child;
                if (StringUtils.isBlank(tn.text())) {
                    // tn.text("");
                    continue;
                }

                return true;
            }

            if (child instanceof Element) {
                Element e = (Element) child;
                // 如果节点不是块元素，则结束递归
                if (!e.isBlock()) {
                    return true;
                }

                // 将块元素节点内的内容独立出来，如果还有节点存在则继续递归
                Node firstChild = e.unwrap();
                logger.debug("Unwraped block element: {}, firstChild: {}", e.nodeName(), (firstChild == null ? null
                        : firstChild.nodeName()));
                if (firstChild == null || unwrapFirstNotBlankBlock(firstChild.parent())) {
                    return true;
                }
            }
        }

        return false;
    }

}
