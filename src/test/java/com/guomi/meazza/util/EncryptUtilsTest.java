/*
 * @(#)EncryptUtilsTest.java    Created on 2015年11月23日
 * Copyright (c) 2015 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

/**
 * @author akuma
 */
public class EncryptUtilsTest {

    @Test
    public void testRSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        KeyPair key = keyGen.generateKeyPair();
        String publicKey = Base64.encodeBase64String(key.getPublic().getEncoded());
        String privateKey = Base64.encodeBase64String(key.getPrivate().getEncoded());
        System.out.println(publicKey);
        System.out.println(privateKey);

        String plainText = "Hello RSA!";
        String encodeText = EncryptUtils.encodeByRSAAndBase64(plainText, publicKey);
        String decodeText = EncryptUtils.decodeByRSAAndBase64(encodeText, privateKey);
        assertEquals(plainText, decodeText);
    }

}
