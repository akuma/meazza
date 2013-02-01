/* 
 * @(#)DefaultTextProducer.java    Created on 2013-1-31
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.captcha.text;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Produces text of a given length from a given array of characters.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 */
public class DefaultTextProducer implements TextProducer {

    private static final Random RAND = new SecureRandom();
    private static final int DEFAULT_LENGTH = 4;
    private static final char[] DEFAULT_CHARS = new char[] { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f',
            'F', 'g', 'G', 'h', 'H', 'k', 'K', 'm', 'M', 'n', 'N', 'p', 'P', 'r', 'R', 'w', 'W', 'x', 'X', 'y', 'Y',
            '2', '3', '4', '5', '6', '7', '8', };

    private final int _length;
    private final char[] _srcChars;

    public DefaultTextProducer() {
        this(DEFAULT_LENGTH, DEFAULT_CHARS);
    }

    public DefaultTextProducer(int length, char[] srcChars) {
        _length = length;
        _srcChars = copyOf(srcChars, srcChars.length);
    }

    @Override
    public String getText() {
        String capText = "";
        for (int i = 0; i < _length; i++) {
            capText += _srcChars[RAND.nextInt(_srcChars.length)];
        }

        return capText;
    }

    private static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

}
