/* 
 * @(#)BackgroundProducer.java    Created on 2013-1-31
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.captcha.background;

import java.awt.image.BufferedImage;

/**
 * @author akuma
 */
public interface BackgroundProducer {

    /**
     * Add the background to the given image.
     * 
     * @param image
     *            The image onto which the background will be rendered.
     * @return The image with the background rendered.
     */
    public BufferedImage addBackground(BufferedImage image);

    public BufferedImage getBackground(int width, int height);

}
