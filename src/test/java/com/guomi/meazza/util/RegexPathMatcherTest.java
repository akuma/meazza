/* 
 * @(#)RegexPathMatcherTest.java    Created on 2012-7-18
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author akuma
 */
public class RegexPathMatcherTest {

    @Test
    public void testMatches() {
        RegexPathMatcher rpm = new RegexPathMatcher();

        assertTrue(rpm.matches("/user/.*User\\.htm", "/user/listUser.htm"));
        assertTrue(rpm.matches("/user/.*User.*", "/user/listUser.htm"));
        assertFalse(rpm.matches("/user/.*User\\.htm", "/user/listUser.do"));
        assertFalse(rpm.matches("/user/.*User", "/user/listUser.htm"));
        assertFalse(rpm.matches("/user/.*Foo", "/user/listUser.htm"));
        assertFalse(rpm.matches("/foo/.*User.*", "/user/listUser.htm"));

        int n = 100000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            rpm.matches("/user/(.*User.htm|.*base.*\\.htm)", "/user/listUser.htm");
        }
        System.out.format("Path match %d times, elapsed %d ms.", n, (System.currentTimeMillis() - time));
    }

}
