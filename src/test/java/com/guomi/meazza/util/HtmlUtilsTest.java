/*
 * @(#)HtmlUtilsTest.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.springframework.util.StopWatch;

/**
 * @author akuma
 */
public class HtmlUtilsTest {

    //    @Test
    //    public void testEscapeHtml() {
    //        System.out.println(HtmlUtils.escapeHtmlExcludeImg(""));
    //        System.out.println(HtmlUtils.escapeHtmlExcludeImg("&a\"\nb"));
    //        System.out.println(HtmlUtils.escapeHtmlExcludeImg("\n<strong>a</strong>"));
    //        System.out.println(HtmlUtils.escapeHtmlExcludeImg("&nbsp;<img src='http://a.cn/a.png'>"));
    //        System.out.println(HtmlUtils.escapeHtmlExcludeImg("<b>a</b> <img src='http://a.cn/a.png'> <script>1</script>"));
    //    }

    @Test
    public void testStripHtmlTags() throws IOException {
        assertEquals("", HtmlUtils.stripHtmlToEmpty(null));
        assertEquals("", HtmlUtils.stripHtmlToEmpty(""));
        assertEquals("", HtmlUtils.stripHtmlToEmpty("     "));
        assertEquals("", HtmlUtils.stripHtmlToEmpty("&nbsp;&nbsp;"));
        assertEquals("a  b", HtmlUtils.stripHtmlToEmpty("&nbsp;a&nbsp;&nbsp;b&nbsp;"));
        assertEquals("test",
                HtmlUtils.stripHtmlToEmpty("   <div>    &nbsp;\ttest  </span><script>alert(1);</script>  "));

        String html = "<!DOCTYPE html><html><head><meta charset='utf-8'><title><>html strip test</title></head>"
                + "<body><b>test 测试</b><div style='color:red;'>伦敦奥运会开幕了！\n&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<font size=\"5\"><p>some text</p>看比赛吗？<br/>\n&nbsp;&nbsp;&nbsp;你想怎样？<br>\n</div></body></html>";
        assertEquals("test 测试 伦敦奥运会开幕了！      some text看比赛吗？    你想怎样？", HtmlUtils.stripHtmlToEmpty(html));
        //        System.out.println(HtmlUtils.stripHtmlToEmpty(html));

        // Simple Performance Test
        String largeSample = FileUtils.readFileToString(new File("src/test/java/com/guomi/meazza/util/htmlSample.txt"),
                "utf-8");
        // System.out.println(htmlSample);
        // System.out.println(HtmlUtils.stripHtml(htmlSample));
        int times = 1000;
        StopWatch watch = new StopWatch("html strip test(sample length: " + largeSample.length() + ")");
        watch.start("Call HtmlUtils.stripHtml(String) " + times + " times");
        for (int i = 0; i < times; i++) {
            HtmlUtils.stripHtmlToEmpty(largeSample);
        }
        watch.stop();
        //        System.out.println(watch.prettyPrint());
    }

    @Test
    public void testIsBlank() {
        String[] blankSamples = { null, "", "   ", "&nbsp;", "<br>", "<div></div>", "<p></p>", "<div>&nbsp;</div>",
                "<p>&nbsp; </p>", "<div>&nbsp;</div><br><p>&nbsp;</p>",
                "<DIV>&nbsp;</DIV>\n<DIV> &nbsp;</DIV>\n<DIV>&nbsp; </dIv>",
                "<p>&nbsp;</p><ul style='color:red'></ul>" };
        for (String s : blankSamples) {
            // System.out.println("isBlank: " + s);
            assertTrue(HtmlUtils.isBlank(s));
            assertTrue(HtmlUtils.isBlank(s, false));
        }

        assertTrue(HtmlUtils.isBlank("<div></div><div><image alt='test'></div>"));
        assertFalse(HtmlUtils.isBlank("<div></div><div><image alt='test'></div>", false));

        String[] notBlankSamples = { "abc", "abc<div>test</div>", "  abc&nbsp; <div></div>",
                "<div></div><div><image alt='test'></div>", "<image src='asdf' /><span></span><image title='asdf'>" };
        for (String s : notBlankSamples) {
            // System.out.println("isBlank: " + s);
            assertFalse(HtmlUtils.isBlank(s, false));
        }

        // Simple Performance Test
        String largeSample = "<p>下列自然景物的描写，带给你怎样的联想与启发？选择正确的答案。</p><p>《泊船瓜洲》中的&ldquo;"
                + "明月&rdquo;让人联想到：（ ）</p></div><div class=\"answer_box\"><div class=\"answer_option\">"
                + "A、<label><p>&ldquo;我&rdquo;的行程漫长又险阻，特别是&ldquo;风雪&rdquo;"
                + "一次，结合词的意境，给人一种环境严酷的感觉。</p></label></div>"
                + "<div class=\"answer_option\">B、<label><p>&ldquo;举头望明月，低头思故乡&rdquo;，"
                + "&ldquo;月是故乡明&rdquo;等，望月思乡的情绪是游子们的共同特点。</p></label></div>"
                + "<div class=\"answer_option\">C、<label><p>落叶满地、秋风萧瑟的景象，有一些落寞、孤寂的感觉。</p></label></div>";
        int times = 1000;
        StopWatch watch = new StopWatch("HtmlUtils.isBlank(String) Performance Test");
        watch.start("Call HtmlUtils.isBlank(String) " + times + " times");
        for (int i = 0; i < times; i++) {
            HtmlUtils.isBlank(largeSample);
        }
        watch.stop();
        //        System.out.println(watch.prettyPrint());
    }

    @Test
    public void testCleanEditorHtml() throws IOException {
        assertEquals("test", HtmlUtils.cleanEditorHtml("test"));
        assertEquals("test", HtmlUtils.cleanEditorHtml("  test  "));
        assertEquals("test", HtmlUtils.cleanEditorHtml("  test &nbsp; "));
        assertEquals("test", HtmlUtils.cleanEditorHtml(" &nbsp;   test &nbsp; "));
        assertEquals("test", HtmlUtils.cleanEditorHtml("  <div>&nbsp;test</div>  "));
        assertEquals("test", HtmlUtils.cleanEditorHtml(" test<br>  "));
        assertEquals("test<b>test</b>test", HtmlUtils.cleanEditorHtml(" test<b>test</b>test  ", false));
        assertEquals("test<br><br>test", HtmlUtils.cleanEditorHtml("&nbsp;<div>&nbsp; test<br><br/>test  ", false));
        assertEquals("test<br><br>test", HtmlUtils.cleanEditorHtml(" test<br><br/>test  "));
        assertEquals("a<sub>3</sub> b<sup>4</sup>",
                HtmlUtils.cleanEditorHtml("&nbsp; a<sub>3</sub> b<sup>4</sup> &nbsp;<p>", false));
        assertEquals("", HtmlUtils.cleanEditorHtml(null));
        assertEquals("", HtmlUtils.cleanEditorHtml(""));
        assertEquals("", HtmlUtils.cleanEditorHtml(" "));
        assertEquals("", HtmlUtils.cleanEditorHtml("&nbsp;"));
        assertEquals("", HtmlUtils.cleanEditorHtml(" &nbsp; "));
        assertEquals("", HtmlUtils.cleanEditorHtml(" <br> "));
        assertEquals("", HtmlUtils.cleanEditorHtml(" <p>&nbsp;</p> "));
        assertEquals("", HtmlUtils.cleanEditorHtml("&nbsp; <p>&nbsp;</p> <div></div> "));
        assertEquals("", HtmlUtils.cleanEditorHtml("<p>&nbsp;</p> <div></div> "));
        assertEquals("a&nbsp;&nbsp;b", HtmlUtils.cleanEditorHtml("&nbsp;a&nbsp;&nbsp;b&nbsp;"));

        String html = "<!DOCTYPE html><html><head><meta charset='utf-8'><title><>html strip test</title></head>"
                + "<body><b>test 测试</b><div style='color:red;'>伦敦奥运会开幕了！\n&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<font size=\"5\"><p>some text</p>看比赛吗？<br/>\n&nbsp;&nbsp;&nbsp;你想怎样？<br>\n</div></body></html>";
        System.out.println(HtmlUtils.cleanEditorHtml(html));

        String sample = "&nbsp; &nbsp; &nbsp;<div>&nbsp;</div>&nbsp;<div>&nbsp;<div>&nbsp;</div><div>foo</div>"
                + "<div>&nbsp;</div><div>&nbsp;<b>bb</b></div></div>"
                + "<div>&nbsp;a</div><div></div><div><p>&nbsp;</p></div>&nbsp; &nbsp; ";
        System.out.println("------------------ origin\n" + Jsoup.parse(sample).body().html());
        System.out.println("------------------ cleaned\n" + HtmlUtils.cleanEditorHtml(sample));

        sample = "<div>&nbsp;a</div><div></div><div>&nbsp;</div>b<br><div>&nbsp;</div><div>c</div>";
        System.out.println("------------------ origin\n" + Jsoup.parse(sample).body().html());
        System.out.println("------------------ cleaned\n" + HtmlUtils.cleanEditorHtml(sample));

        assertEquals("<div style=\"color:red\">testb<span>b</span></div>",
                HtmlUtils.cleanEditorHtml("<p style='color:red'>testb<span>b</span></p>"));

        sample = "<p>体积为4&times;10<sup>-3</sup>m<sup>3</sup>的铜球．其质量为24 kg．试判断这个铜球是空心的还是实心的，"
                + "(铜的密度是8．9&times;10<sup>3</sup>kg／m<sup>3</sup>)</p>还有什么<br><br>没有了？";
        System.out.println("cleaned html:\n" + HtmlUtils.cleanEditorHtml(sample));

        sample = "<DIV><SPAN style=\"FONT-FAMILY: 宋体; FONT-SIZE: 10.5pt; mso-bidi-font-family: 'Times New Roman';"
                + " mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN;"
                + " mso-bidi-language: AR-SA; mso-bidi-font-size: 10.0pt; mso-bidi-font-weight: bold\">"
                + "</SPAN>如图3-43，正方形ABCD中，过点D作DP交AC于点M，交AB于点N，交CB的延长线于点P. 若MN = 1，PN = 3，"
                + "则DM的长为{blank}.</DIV><DIV><IMG src=\"http://ilearn.gm.com/ilearn/upload/2012-08-10/"
                + "ff808081390ab64d01390f29d5560891.jpg\"></DIV>";
        System.out.println("safeHtml html:\n" + HtmlUtils.cleanEditorHtml(sample));

        // Simple Performance Test
        String largeSample = FileUtils.readFileToString(new File("src/test/java/com/guomi/meazza/util/htmlSample.txt"),
                "utf-8");
        int times = 20;
        StopWatch watch = new StopWatch(
                "HtmlUtils.cleanEditorHtml(String) Performance Test (sample length: " + largeSample.length() + ")");
        watch.start("Call HtmlUtils.cleanEditorHtml(String) " + times + " times");
        for (int i = 0; i < times; i++) {
            HtmlUtils.cleanEditorHtml(largeSample);
        }
        watch.stop();
        //        System.out.println(watch.prettyPrint());
    }

    @Test
    public void testUnwrapBlockElement() throws IOException {
        assertEquals("te<div>st</div>", HtmlUtils.cleanEditorHtml("<div>&nbsp;<div>te</div>&nbsp;<div>st</div></div>"));

        String sample = "&nbsp; &nbsp; <div>&nbsp;</div>&nbsp;<div>&nbsp;<div>&nbsp;</div><img src='test.jsp' />"
                + "<div>foo</div><div>&nbsp;</div><div>&nbsp;<b>bb</b></div></div>"
                + "<div>&nbsp;a</div><div></div><div><p>&nbsp;</p></div>&nbsp; &nbsp; ";
        System.out.println("------------------ origin\n" + Jsoup.clean(sample, "http://a", HtmlUtils.getWhitelist()));
        System.out.println("------------------ cleaned\n" + HtmlUtils.cleanEditorHtml(sample));

        String largeSample = FileUtils.readFileToString(new File("src/test/java/com/guomi/meazza/util/htmlSample.txt"),
                "utf-8");
        System.out.println("------------------ origin\n" + Jsoup.parse(largeSample).body().html());
        System.out.println("------------------ cleaned\n" + HtmlUtils.cleanEditorHtml(largeSample));
    }

    @Test
    public void testImg() {
        String html = "<img src='test.jpg' style='float:right' alt='test' />";
        System.out.println(HtmlUtils.cleanEditorHtml(html));
    }

    @Test
    public void testUnwrap() {
        String html = "<div class='equation-wrapper'><table><tbody><tr>"
                + "<td style='border-bottom:1px solid black;padding-bottom:1px;font-size:90%'>3"
                + "<table><tbody><tr><td style='font-size: 0px'><div"
                + "><div style='width:6px;background: url(http://assets.clearn.com/equation/2df00e39.png) "
                + "repeat-y; height: 1px;overflow: hidden'></div><div style='width:6px;background: "
                + "url(http://assets.clearn.com/equation/4ef41bb5.png) no-repeat; height: 7px; "
                + "overflow: hidden'></div></div></td><td style='padding:0;padding-left: 2px; "
                + "border-top: black 1px solid;line-height:normal'>14</td></tr></tbody></table>"
                + "</td></tr><tr><td>7</td></tr></tbody></table></div>";
        String result = HtmlUtils.cleanEditorHtml(html);
        assertTrue(result.length() == html.length());
    }

}
