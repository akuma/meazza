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
    public void TestIsBlank() {
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

}
