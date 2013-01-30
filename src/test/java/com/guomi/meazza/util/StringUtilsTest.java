/* 
 * @(#)StringUtilsTest.java    Created on 2012-8-11
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author akuma
 */
public class StringUtilsTest {

    @Test
    public void testIsBlank() {
        char[] spaces = { '\u00A0', '\u2007', '\u202F' };
        for (char c : spaces) {
            assertTrue(StringUtils.isBlank(String.valueOf(c)));
        }

        assertTrue(StringUtils.isBlank("   "));
        assertTrue(StringUtils.isBlank("  \r \n "));
        assertTrue(StringUtils.isBlank("  \r \n \u00A0 "));
        assertFalse(StringUtils.isBlank("  \r sdlfsjdf \n "));
        assertFalse(StringUtils.isBlank("  \r  \u202F sdlfsjdf \n "));
    }

    @Test
    public void testIsEmail() {
        assertTrue(StringUtils.isEmail("test@test.com"));
        assertTrue(StringUtils.isEmail("test@test.cn"));
        assertTrue(StringUtils.isEmail("1111@test.cn"));
        assertTrue(StringUtils.isEmail("_1111@test.cn"));
        assertTrue(StringUtils.isEmail("test@test.asdf.net"));
        assertTrue(StringUtils.isEmail("test@test.asdf.az"));
        assertFalse(StringUtils.isEmail("test@test.asdf.az2"));
        assertFalse(StringUtils.isEmail("@test.com"));
        assertFalse(StringUtils.isEmail("test@test"));
        assertFalse(StringUtils.isEmail("test"));
        assertFalse(StringUtils.isEmail(null));
        assertFalse(StringUtils.isEmail(""));
        assertFalse(StringUtils.isEmail("   "));
    }

    @Test
    public void testIsMobile() {
        assertTrue(StringUtils.isMobile("13988888888"));
        assertTrue(StringUtils.isMobile("18988888888"));
        assertTrue(StringUtils.isMobile("18688888888"));
        assertTrue(StringUtils.isMobile("13488888888"));
        assertTrue(StringUtils.isMobile("15888888888"));
        assertFalse(StringUtils.isMobile(null));
        assertFalse(StringUtils.isMobile(""));
        assertFalse(StringUtils.isMobile("   "));
        assertFalse(StringUtils.isMobile("186888888888"));
        assertFalse(StringUtils.isMobile("1868888888a"));
        assertFalse(StringUtils.isMobile("08688888888"));
        assertFalse(StringUtils.isMobile("a8688888888"));
        assertFalse(StringUtils.isMobile("1898888a888"));
    }

}
