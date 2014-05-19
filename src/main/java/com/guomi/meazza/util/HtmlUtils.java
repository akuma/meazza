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
import org.jsoup.safety.Cleaner;
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

    private static final Whitelist DEFAULT_WHITELIST = getWhitelist();

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
     * <li>\n --> &lt;br&gt;</li>
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
        if (StringUtils.isEmpty(html)) {
            return "&nbsp;";
        }

        return html.replaceAll("&", "&amp;").replaceAll("\t", "    ").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("\n", "<br>");
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
        if (StringUtils.isEmpty(html)) {
            return StringUtils.EMPTY;
        }

        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

    //    public static String escapeHtmlExcludeImg(String html) {
    //        if (StringUtils.isEmpty(html)) {
    //            return "&nbsp;";
    //        }
    //
    //        Document dirty = Jsoup.parseBodyFragment(html);
    //        Elements elements = dirty.select("body *").not("img");
    //        for (Element element : elements) {
    //            element.replaceWith(new TextNode(element.toString(), ""));
    //        }
    //
    //        Cleaner cleaner = new Cleaner(Whitelist.none().addTags("img")
    //                .addAttributes("img", "align", "alt", "height", "src", "title", "width")
    //                .addProtocols("img", "src", "http", "https"));
    //        Document clean = cleaner.clean(dirty);
    //        clean.outputSettings().prettyPrint(false);
    //        return clean.body().html().replaceAll("\t", "    ").replaceAll(" ", "&nbsp;");
    //    }

    /**
     * 判断 HTML 代码是否为空白。如果 {@code html} 只包含空串、空格、空的 html 标签则认为 HTML 代码为空白。
     * 
     * @param html
     *            需要测试的 html 代码
     * @return true/false
     */
    public static boolean isBlank(String html) {
        return isBlank(html, true);
    }

    /**
     * 判断 HTML 代码是否为空白。如果 {@code html} 只包含空串、空格、空的 html 标签则认为 HTML 代码为空白。
     * 
     * @param html
     *            需要测试的 html 代码
     * @param isImageAsBlank
     *              是否把 img 标签算作空，如果是的话则在 {@code html} 仅包含 img 标签时，也会返回 <code>true</code>
     * @return true/false
     */
    public static boolean isBlank(String html, boolean isImageAsBlank) {
        if (StringUtils.isBlank(html)) {
            return true;
        }

        String striped = stripHtmlToEmpty(html);
        boolean isBlank = StringUtils.isBlank(striped);

        // 这种情况下，必须包含文字的 html 才不算空
        if (isImageAsBlank) {
            return isBlank;
        }

        // 这种情况下，仅包含图片的 html 不算空
        return isBlank && Jsoup.parse(html).select("img").isEmpty();
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
     * 根据 <code>html</code> 获取清理过的 jsoup 元素。
     */
    public static Document getCleanDoc(String html, boolean prettyPrint) {
        return getCleanDoc(html, prettyPrint, DEFAULT_WHITELIST);
    }

    /**
     * 根据 <code>html</code> 获取清理过的 jsoup 元素，白名单通过 <code>whitelist</code> 指定。
     */
    public static Document getCleanDoc(String html, boolean prettyPrint, Whitelist whitelist) {
        if (StringUtils.isBlank(html)) {
            return null;
        }

        // 移除首尾空白字符
        String stripedHtml = StringUtils.stripToEmpty(html);

        // 对 html 标签、标签属性进行白名单过滤，得到安全的 html 代码
        String safeHtml = clean(stripedHtml, DEFAULT_JSOUP_BASE_URI, whitelist, prettyPrint);
        logger.trace("safeHtml: {}", safeHtml);

        // 解析 html 代码
        Document doc = Jsoup.parse(safeHtml);
        doc.outputSettings().prettyPrint(prettyPrint);

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

        return doc;
    }

    /**
     * 处理从编辑器输入的 HTML 代码，输出更加安全的代码（避免 XSS 等漏洞）。默认输出的代码不会经过格式化。
     * <p>
     * 删除首尾的空白字符（空串、空格、回车、换行等）、空白标签、&amp;&nbsp;，并且过滤掉不包含在白名单的标签、标签属性。
     * 
     * @param html
     *            需要处理的 HTML 代码
     * @return 处理之后的 HTML 代码
     */
    public static String cleanEditorHtml(String html) {
        return cleanEditorHtml(html, false);
    }

    /**
     * 处理从编辑器输入的 HTML 代码，输出更加安全的代码（避免 XSS 等漏洞）。
     * <p>
     * 删除首尾的空白字符（空串、空格、回车、换行等）、空白标签、&amp;&nbsp;，并且过滤掉不包含在白名单的标签、标签属性。
     * 
     * @param html
     *            需要处理的 HTML 代码
     * @param prettyPrint
     *             是否输入格式化之后的 HTML 代码
     * @return 处理之后的 HTML 代码
     */
    public static String cleanEditorHtml(String html, boolean prettyPrint) {
        Document doc = getCleanDoc(html, prettyPrint);
        Element body = doc == null ? null : doc.body();
        return body == null ? StringUtils.EMPTY : body.html();
    }

    /**
     * 获取 jsoup 白名单对象，在 {@link org.jsoup.safety.Whitelist#basicWithImages()} 白名单的基础上添加 div、table、p、span 等标签。
     * <em>注意：对返回的对象的修改不会影响此类。</em>
     * 
     * @return jsoup 白名单对象
     */
    public static Whitelist getWhitelist() {
        return Whitelist.basicWithImages().addTags("div", "table", "thead", "tbody", "tfoot", "tr", "th", "td")
                .addAttributes("div", "style", "class", "data-sqth").addAttributes("p", "style")
                .addAttributes("span", "style").addAttributes("table", "width", "style")
                .addAttributes("td", "colspan", "rowspan", "width", "style")
                .addAttributes("th", "colspan", "rowspan", "width", "style").addAttributes("img", "style")
                .preserveRelativeLinks(true);
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
                Node previousSibling = tn.previousSibling();
                logger.debug("stripStartBlankNodes() -> textNode' previous sibling: {}", previousSibling);
                if (previousSibling == null) {
                    tn.text(StringUtils.stripStart(tn.text()));
                    return true;
                }
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
                    logger.debug("stripStartBlankNodes() -> Removed textNode: {}", tn.text());
                    tn.remove();
                } else {
                    logger.trace("stripStartBlank() -> Strip start on textNode: {}", tn);
                    tn.text(StringUtils.stripStart(tn.text()));

                    if (canElementRemove) {
                        c.remove();
                        logger.debug("stripStartBlankNodes() -> Removed element:\n{}\n", c);
                    }

                    return true;
                }
            }

            // 按需删除当前元素
            if (canElementRemove) {
                c.remove();
                logger.debug("stripStartBlankNodes() -> Removed element:\n{}\n", c);
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
     * 删除 HTMl 元素中右边所有连续的空白字符、空节点。 TODO 目前对于直接位于 body 的空白文本不会删除，待完善
     */
    private static boolean stripEndBlankNodes(Element element) {
        // 查找直属于当前元素的空白文本节点并将内容设置为空串
        // 对于非空白的文本节点，将文字左边的空白字符删除
        List<TextNode> childNodes = element.textNodes();
        for (int i = childNodes.size() - 1; i >= 0; i--) {
            TextNode tn = childNodes.get(i);
            if (StringUtils.isBlank(tn.text())) {
                tn.text("");
            } else {
                Node nextSibling = tn.nextSibling();
                logger.debug("stripEndBlankNodes() -> textNode' next sibling: {}", nextSibling);
                if (nextSibling == null) {
                    tn.text(StringUtils.stripEnd(tn.text()));
                    return true;
                }
            }
        }

        Elements children = element.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            Element c = children.get(i);
            // 如果发现空节点则删除掉
            if (isBlankElement(c)) {
                c.remove();
                logger.debug("stripEndBlankNodes() -> Removed element:\n{}\n", c);
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
                if (!e.isBlock() || e.hasAttr("class") || e.hasAttr("style")) {
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

    /**
     * Jsoup 的 clean 方法会对代码进行格式化，所以自定义了一个方法，允许指定是否对代码格式化。
     */
    private static String clean(String bodyHtml, String baseUri, Whitelist whitelist, boolean pretty) {
        Document dirty = Jsoup.parseBodyFragment(bodyHtml, baseUri);
        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);
        clean.outputSettings().prettyPrint(pretty);
        return clean.body().html();
    }

}
