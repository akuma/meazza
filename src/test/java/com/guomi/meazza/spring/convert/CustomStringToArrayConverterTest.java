/* 
 * @(#)CustomStringToArrayConverterTest.java    Created on 2012-8-15
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.convert;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * @author akuma
 */
public class CustomStringToArrayConverterTest {

    @Test
    public void testConvert() {
        CustomStringToArrayConverter converter = new CustomStringToArrayConverter();
        converter.setDelimiter(";");

        String[] dest = converter.convert(null);
        assertArrayEquals(new String[] {}, dest);

        dest = converter.convert("");
        assertArrayEquals(new String[] {}, dest);

        dest = converter.convert("a");
        assertArrayEquals(new String[] { "a" }, dest);

        dest = converter.convert("a;b;");
        assertArrayEquals(new String[] { "a", "b", "" }, dest);

        dest = converter.convert("a;b;c");
        assertArrayEquals(new String[] { "a", "b", "c" }, dest);

        dest = converter.convert(";a;b;c;;");
        assertArrayEquals(new String[] { "", "a", "b", "c", "", "" }, dest);

        converter.setDelimiter("{comma}");
        dest = converter.convert(null);
        assertArrayEquals(new String[] {}, dest);

        dest = converter.convert("");
        assertArrayEquals(new String[] {}, dest);

        dest = converter.convert("a");
        assertArrayEquals(new String[] { "a" }, dest);

        dest = converter.convert("a{comma}b{comma}");
        assertArrayEquals(new String[] { "a", "b", "" }, dest);

        dest = converter.convert("a{comma}b{comma}c");
        assertArrayEquals(new String[] { "a", "b", "c" }, dest);

        dest = converter.convert("{comma}a{comma}b{comma}c{comma}{comma}");
        assertArrayEquals(new String[] { "", "a", "b", "c", "", "" }, dest);
    }

}
