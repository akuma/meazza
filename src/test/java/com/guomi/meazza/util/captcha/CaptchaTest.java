/* 
 * @(#)CaptchaTest.java    Created on 2013-2-1
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.captcha;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * @author akuma
 */
public class CaptchaTest {

    @Test
    public void test() {
        try (FileOutputStream jpg = new FileOutputStream("target/test.jpg");
                FileOutputStream png = new FileOutputStream("target/test.png");) {
            Captcha captcha = new Captcha.Builder(200, 50).addText().addBackground().addNoise().build();

            long time = System.currentTimeMillis();
            ImageIO.write(captcha.getImage(), "png", png);
            System.out.printf("Export png elapsed: %d ms\f", (System.currentTimeMillis() - time));

            time = System.currentTimeMillis();
            ImageIO.write(captcha.getImage(), "jpg", jpg);
            System.out.printf("Export jpg elapsed: %d ms\f", (System.currentTimeMillis() - time));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
