/*
 * @(#)StringUtilsTest.java    Created on 2012-8-11
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

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

    @Test(expected = NumberFormatException.class)
    public void testSplitToNumberList() {
        List<Integer> a = StringUtils.splitToIntList(",1, ,3,, 4, , 5 , 7,8,", ",");
        assertEquals("[1, 3, 4, 5, 7, 8]", a.toString());

        List<Long> b = StringUtils.splitToLongList(",1, ,3,, 4, , 5 , 7,8 ,1234234", ",");
        assertEquals("[1, 3, 4, 5, 7, 8, 1234234]", b.toString());

        // throw NumberFormatException
        StringUtils.splitToLongList(",1, ,3,, 4, , 5 , 7,8 ,1234234, a", ",");
    }

    @Test
    public void testGetEmailWebsite() {
        assertEquals("http://mail.163.com", StringUtils.getEmailWebsite("foo@163.com"));
        assertEquals("http://mail.126.com", StringUtils.getEmailWebsite("foo@126.com"));
        assertEquals("http://mail.139.com", StringUtils.getEmailWebsite("foo@139.com"));
        assertEquals("http://gmail.com", StringUtils.getEmailWebsite("foo@gmail.com"));
        assertEquals("http://hotmail.com", StringUtils.getEmailWebsite("foo@hotmail.com"));
        assertEquals("http://mail.qq.com", StringUtils.getEmailWebsite("foo@qq.com"));
        assertEquals("http://mail.msn.com", StringUtils.getEmailWebsite("foo@msn.com"));
        assertEquals("http://mail.yahoo.com", StringUtils.getEmailWebsite("foo@yahoo.com"));
        assertNull(StringUtils.getEmailWebsite("fooyahoo.com"));
        assertNull(StringUtils.getEmailWebsite(""));
        assertNull(StringUtils.getEmailWebsite(null));
    }

    @Test
    public void testisEnglishLetter() {
        assertTrue(StringUtils.isEnglishLetter("abcdefghijklmnopqrstuvwxyz"));
        assertTrue(StringUtils.isEnglishLetter("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue(StringUtils.isEnglishLetter("AbcdefgHIJKLmnopqRStuvwxYZ"));
        assertTrue(!StringUtils.isEnglishLetter("AbcdefgHIJKL mnopqRStuvwxYZ"));
        assertTrue(!StringUtils.isEnglishLetter("AbcdefgHIJKL_mnopqRStuvwxYZ"));
        assertTrue(!StringUtils.isEnglishLetter("AbcdefgHIJKLmnopqRStuvwxYZ9"));
        assertTrue(!StringUtils.isEnglishLetter("AbcdefgHIJKL测试mnopqRStuvwxYZ9"));
        assertTrue(!StringUtils.isEnglishLetter("1AbcdefgHIJKLmnopqRStuvwxYZ9"));
        assertTrue(!StringUtils.isEnglishLetter("测试即"));
        assertTrue(!StringUtils.isEnglishLetter("AbcdefgHIJKLmnopqRStuvwxYZ2"));
        assertTrue(!StringUtils.isEnglishLetter("  "));
        assertTrue(!StringUtils.isEnglishLetter("  1"));
        assertTrue(!StringUtils.isEnglishLetter(null));
    }

}
