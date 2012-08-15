/* 
 * @(#)CustomStringToDateConverterTest.java    Created on 2012-8-15
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

/**
 * @author akuma
 */
public class CustomStringToDateConverterTest {

    @Test
    public void testConvert() {
        CustomStringToDateConverter converter = new CustomStringToDateConverter();

        Date date = converter.convert("2012-8-15");
        Calendar cal = DateUtils.toCalendar(date);
        assertEquals(2012, cal.get(Calendar.YEAR));
        assertEquals(8 - 1, cal.get(Calendar.MONTH));
        assertEquals(15, cal.get(Calendar.DATE));

        date = converter.convert(null);
        assertNull(date);
        date = converter.convert("");
        assertNull(date);
        date = converter.convert("2012");
        assertNull(date);
    }

}
