/* 
 * @(#)HtmlUtilsTest.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.StopWatch;

/**
 * @author akuma
 */
public class HtmlUtilsTest {

    @Test
    public void testStripHtmlTags() throws IOException {
        String html = "<html><head><title>html strip test</title></head>"
                + "<body><b>test 测试</b><div>伦敦奥运会开幕了！\n&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<font size=\"5\"><p>some text</p>看比赛吗？<br/>\n&nbsp;&nbsp;&nbsp;你想怎样？<br>\n</div></body></html>";
        System.out.println(HtmlUtils.stripHtml(html));

        String htmlSample = FileUtils.readFileToString(new File("src/test/java/com/guomi/meazza/util/htmlSample.txt"),
                "utf-8");
        // System.out.println(htmlSample);
        // System.out.println(HtmlUtils.stripHtml(htmlSample));

        StopWatch watch = new StopWatch("html strip test(sample length: " + htmlSample.length() + ")");
        watch.start("HtmlUtils.stripHtml(String)");
        for (int i = 0; i < 1000; i++) {
            HtmlUtils.stripHtml(htmlSample);
        }
        watch.stop();
        System.out.println(watch.prettyPrint());
    }

}
