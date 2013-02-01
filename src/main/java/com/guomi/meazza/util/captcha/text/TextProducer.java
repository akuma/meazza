/* 
 * @(#)TextProducer.java    Created on 2013-1-31
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.captcha.text;

/**
 * Generate an answer for the CAPTCHA.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 */
public interface TextProducer {

    /**
     * Generate a series of characters to be used as the answer for the CAPTCHA.
     * 
     * @return The answer for the CAPTCHA.
     */
    public String getText();
}
