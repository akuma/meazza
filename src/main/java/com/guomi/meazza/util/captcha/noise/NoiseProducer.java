/* 
 * @(#)NoiseProducer.java    Created on 2013-1-31
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.captcha.noise;

import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 */
public interface NoiseProducer {

    void makeNoise(BufferedImage image);

}
