/* 
 * @(#)WordRenderer.java    Created on 2013-2-1
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.captcha.text;

import java.awt.image.BufferedImage;

/**
 * Render the answer for the CAPTCHA onto the image.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 */
public interface WordRenderer {

    /**
     * Render a word to a BufferedImage.
     * 
     * @param word
     *            The sequence of characters to be rendered.
     * @param image
     *            The image onto which the word will be rendered.
     */
    void render(String word, BufferedImage image);

}