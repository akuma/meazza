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

    private static final Pattern START_NBSP_REPLACE_REGEX = Pattern.compile("^(&nbsp;)+");

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
        // Document doc = Jsoup.parse(stripedHtml);

        stripStartBlankTags(doc);
        stripEndBlankTags(doc);
        convertNodePToDiv(doc);

        // 去掉行首的 &nbsp;
        String result = doc.body().html();
        return START_NBSP_REPLACE_REGEX.matcher(result).replaceAll("");

        // // 对 html 标签、标签属性进行白名单过滤，得到安全的 html 代码
        // String safeHtml = Jsoup.clean(doc.body().html(), DEFAULT_JSOUP_BASE_URI, whitelist);
        // logger.trace("safeHtml: {}", safeHtml);
        // return safeHtml;
    }

    public static String cleanEditorHtmlV2(String html) {
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
        stripStartBlankNodes(doc.body());
        stripEndBlankNodes(doc.body());
        convertNodePToDiv(doc);
        unwrapFirstBlock(doc);

        return doc.body().html();
    }

    /**
     * 删除所有元素开头的空白字符、空节点。
     */
    private static boolean stripStartBlankNodes(Element element) {
        List<Node> childNodes = element.childNodes();
        for (Node node : childNodes) {
            if (node instanceof TextNode) {
                TextNode tn = (TextNode) node;
                if (StringUtils.isBlank(tn.text())) { // 查找空白的文本节点并删除
                    tn.text("");
                } else { // 对于非空白的文本节点，将文字左边的空白字符删除
                    tn.text(StringUtils.stripStart(tn.text()));
                }
            }
        }

        Elements children = element.children();
        for (Element c : children) {
            // Node node = c.nextSibling();
            // if (node instanceof TextNode) {
            // TextNode tn = (TextNode) node;
            // if (StringUtils.isBlank(tn.text())) { // 查找空白的文本节点并删除
            // tn.remove();
            // } else { // 对于非空白的文本节点，将文字左边的空白字符删除
            // tn.text(StringUtils.stripStart(tn.text()));
            // }
            // }

            // 如果发现空节点则删除并检查下一个节点
            if (isBlankElement(c)) {
                c.remove();
                continue;
            }

            // 如果节点不空白且无子节点，表明已经找到了 HTML 第一个非空节点，跳出本次循环并结束递归
            if (c.children().isEmpty()) {
                c.text(StringUtils.stripStart(c.text()));
                // discardBlockParent(c);
                return false;
            }

            // 递归子节点
            return stripStartBlankNodes(c);
        }

        return true;
    }

    /**
     * 删除节点中所有末尾的空白字符、空节点。
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
                return false;
            }

            // 递归子节点
            return stripEndBlankNodes(c);
        }

        return true;
    }

    /**
     * 判断节点是否是空白的，图片节点不算空白。
     */
    private static boolean isBlankElement(Element element) {
        return StringUtils.isBlank(element.text()) && element.select("img").isEmpty();
    }

    /**
     * 删除所有开头的空白字符、空标签。
     */
    private static void stripStartBlankTags(Document doc) {
        if (logger.isTraceEnabled()) {
            logger.trace("Body html (Before remove start blank tags):\n{}\n", doc.body().html());
            logger.trace("Body children size (Before remove start blank tags): {}", doc.body().childNodes().size());
        }

        Elements allElements = doc.body().select("*");
        for (Element e : allElements) {
            // 忽略 body 标签
            if ("body".equals(e.nodeName())) {
                continue;
            }

            // 当发现第一个非空白的标签就停止删除操作
            if (hasText(e)) {
                logger.trace("First no blank tag: {}", e.nodeName());
                discardBlockParent(e);
                break;
            }

            // 删除 &nbsp;
            Node node = e.nextSibling();
            if (node instanceof TextNode) {
                TextNode tn = (TextNode) node;
                if (StringUtils.isBlank(tn.text())) {
                    tn.remove();
                }
            }

            e.remove();
            logger.trace("Removed start blank tag: {}", e.nodeName());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Body html (After removed start blank tags):\n{}\n", doc.body().html());
            logger.trace("Body children size (After removed start blank tags): {}", doc.body().childNodes().size());
        }
    }

    /**
     * 删除所有末尾的空白字符、空标签。
     */
    private static void stripEndBlankTags(Document doc) {
        Elements allElements = doc.body().select("*");
        for (int i = allElements.size() - 1; i >= 0; i--) {
            Element e = allElements.get(i);
            // 忽略 body 标签
            if ("body".equals(e.nodeName())) {
                continue;
            }

            if (hasText(e)) {
                break;
            }

            // 删除 &nbsp;
            Node node = e.nextSibling();
            if (node instanceof TextNode) {
                TextNode tn = (TextNode) node;
                if (StringUtils.isBlank(tn.text())) {
                    tn.remove();
                }
            }

            e.remove();
            logger.trace("Removed end blank tag: {}", e.nodeName());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Body html (After removed end blank tags):\n{}\n", doc.body().html());
            logger.trace("Body children size (After removed end blank tags): {}", doc.body().childNodes().size());
        }
    }

    /**
     * 将所有 {@code <p>} 标签转换为 {@code <div>} 标签。
     */
    private static void convertNodePToDiv(Document doc) {
        Elements elements = doc.select("p");
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
     * 获取 jsoup 白名单对象。<em>修改该对象属性的时候需要注意线程安全问题。</em>
     * 
     * @return jsoup 白名单对象
     */
    public static Whitelist getWhitelist() {
        return whitelist;
    }

    /**
     * 和 jsoup 的 {@code Element#hasText()} 的区别是：对于字符串是否是空白字符的判断中，增加了 none-breaking space。
     */
    private static boolean hasText(Element element) {
        List<Node> childNodes = element.childNodes();
        for (Node child : childNodes) {
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode) child;
                if (!StringUtils.isBlank(textNode.text())) {
                    // textNode.text(StringUtils.stripStart(textNode.text()));
                    return true;
                }
            } else if (child instanceof Element) {
                Element el = (Element) child;
                if (hasText(el)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如果 {@code element} 是块元素并且有子元素，则将子元素剥离出父元素并丢弃父元素。如此反复尝试直到第一个子元素是非块元素为止。
     * 这样做的目的是让最终的 HTML 代码不被块元素包含，以免引起在页面显示数据时第一行会换行的问题。
     * 
     * @param element
     *            最外层元素
     */
    private static void discardBlockParent(Element element) {
        logger.debug("discardBlockParent() element: {}", element.html());
        Elements children = element.children();
        logger.debug("discardBlockParent() children: {}", element.children().html());
        while (element.isBlock()) {
            // if (element.parent() == null) {
            // continue;
            // }
            // element.before(element.html());
            System.out.println("----------------" + element.html());
            Document doc = element.ownerDocument();
            Element body = doc.body();
            System.out.println(doc == null);
            body.prepend(element.html());
            element.remove();
            // element.unwrap();

            if (children.isEmpty()) {
                break;
            }

            element = children.get(0);
            children = element.children();
        }
    }

    private static void unwrapFirstBlock(Element element) {
        Element first = element.select("div").first();
        if (first != null) {
            // logger.debug("Unwrap element: {}", first);
            // first.unwrap();
        }
    }

}
