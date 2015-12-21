/*
 * @(#)EncryptUtilsTest.java    Created on 2015年11月23日
 * Copyright (c) 2015 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

/**
 * @author akuma
 */
public class EncryptUtilsTest {

    private static final String TEST_PLAIN_TEXT = "Hello World!";
    private static final String TEST_PASSWORD = "Bhb7FQsrT6TxlWjZSDIpeJFCsmRxzQMHrvGiGuEX";

    @Test
    public void testAES() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        String encodeText = EncryptUtils.encodeByAES(TEST_PLAIN_TEXT, TEST_PASSWORD);
        watch.stop();
        System.out.printf("%d\tms: AES (%s)\n", watch.getTime(), encodeText);

        String decodeText = EncryptUtils.decodeByAES(encodeText, TEST_PASSWORD);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

        decodeText = EncryptUtils.decodeByAES(encodeText, TEST_PASSWORD);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

    }

    @SuppressWarnings("deprecation")
    @Test
    public void test3DES() {
        StopWatch watch = new StopWatch();
        watch.start();
        String encodeText = EncryptUtils.encodeBy3DES(TEST_PLAIN_TEXT, TEST_PASSWORD);
        watch.stop();
        System.out.printf("%d\tms: 3DES(%s)\n", watch.getTime(), encodeText);

        String decodeText = EncryptUtils.decodeBy3DES(encodeText, TEST_PASSWORD);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

        decodeText = EncryptUtils.decodeBy3DES(encodeText, TEST_PASSWORD);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

        watch.reset();
        watch.start();
        encodeText = EncryptUtils.encodeBy3DESAndBase64(TEST_PLAIN_TEXT, TEST_PASSWORD);
        watch.stop();
        System.out.printf("%d\tms: 3DES(%s)\n", watch.getTime(), encodeText);

        decodeText = EncryptUtils.decodeBy3DESAndBase64(encodeText, TEST_PASSWORD);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

        decodeText = EncryptUtils.decodeBy3DESAndBase64(encodeText, TEST_PASSWORD);
        assertEquals(TEST_PLAIN_TEXT, decodeText);
    }

    @Test
    public void testRSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        String publicKey = Base64.encodeBase64String(key.getPublic().getEncoded());
        String privateKey = Base64.encodeBase64String(key.getPrivate().getEncoded());
        System.out.println("publicKey: " + publicKey);
        System.out.println("privateKey: " + privateKey);

        StopWatch watch = new StopWatch();
        watch.start();
        String encodeText = EncryptUtils.encodeByRSA(TEST_PLAIN_TEXT, publicKey);
        watch.stop();
        System.out.printf("%d\tms: RSA (%s)\n", watch.getTime(), encodeText);

        String decodeText = EncryptUtils.decodeByRSA(encodeText, privateKey);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

        decodeText = EncryptUtils.decodeByRSA(encodeText, privateKey);
        assertEquals(TEST_PLAIN_TEXT, decodeText);

        String plainText = "body=学习周卡&buyer_email=xingken@126.com&buyer_id=2088002097358275&discount=0.00"
                + "&gmt_create=2015-12-14 19:22:57&gmt_payment=2015-12-14 19:22:58&is_total_fee_adjust=N"
                + "&notify_id=47b6a74eb3ea24ea1cc4de4995ed5a0f3i&notify_time=2015-12-14 19:26:15"
                + "&notify_type=trade_status_sync&out_trade_no=402881e5519ffd600151a03a84b5015b&payment_type=1"
                + "&price=0.01&quantity=1&seller_email=zdyaohm@163.com&seller_id=2088111858244206&subject=学习周卡"
                + "&total_fee=0.01&trade_no=2015121400001000270005361312&trade_status=TRADE_SUCCESS&use_coupon=N";
        encodeText = EncryptUtils.encodeByRSA(plainText, publicKey);
        decodeText = EncryptUtils.decodeByRSA(encodeText, privateKey);
        assertEquals(plainText, decodeText);

        String sign = EncryptUtils.signByRSA(plainText, privateKey);
        assertTrue(EncryptUtils.verifyByRSA(plainText, publicKey, sign));
    }

}
