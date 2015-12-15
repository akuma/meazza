/*
 * @(#)EncryptUtils.java    Created on 2013-1-30
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加解密工具类。
 *
 * @author akuma
 */
public abstract class EncryptUtils {

    private static final Logger logger = LoggerFactory.getLogger(EncryptUtils.class);

    private static final char[] chs = { 'L', 'K', 'J', '4', 'D', 'G', 'F', 'V', 'R', 'T', 'Y', 'B', 'N', 'U', 'P', 'W',
            '3', 'E', '5', 'H', 'M', '7', 'Q', '9', 'S', 'A', 'Z', 'X', '8', 'C', '6', '2' };

    // DES 加密算法, 可用 DES, DESede, Blowfish
    private static final String ALGORITHM_DES = "DESede";

    // RSA 加密算法
    private static final String ALGORITHM_RSA = "RSA";

    // RSA 签名算法
    private static final String ALGORITHM_RSA_SIG = "SHA1withRSA";

    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 自身混淆加密，最多只能加密 30 个字节长度的字符串。
     *
     * <p>
     * <b>对同一个字符串，加密后的密文可能是不相同的，所以在判断密码是否相等时，不能采用密文进行比对，必须采用明文比对。</b>
     * </p>
     *
     * @param source
     *            源字符串
     * @return 加密后字符串
     * @see {@link #decodeBySelf(String)}
     */
    public static String encodeBySelf(String source) {
        if (source == null) {
            throw new NullPointerException("source can't be null");
        }

        if (source.length() > 30) {
            throw new IllegalArgumentException(
                    "the length of source must be less than 31, actual was " + source.length());
        }

        String plainText = source;
        byte[] plainTextBytes = plainText.getBytes();

        byte[] encodedBytes1 = new byte[30];
        byte[] encodedBytes2 = new byte[30];

        int n1 = 0, n2 = 0;
        for (int i = 0; i < plainTextBytes.length; i++) {
            if ((i + 1) % 2 != 0) { // 奇数位
                encodedBytes1[n1++] = (byte) get32Hi(plainTextBytes[i] * 4);
                encodedBytes1[n1++] = (byte) get32Low(plainTextBytes[i] * 4);
            } else { // 偶数位
                encodedBytes2[n2++] = (byte) get32Hi(plainTextBytes[i] * 4);
                encodedBytes2[n2++] = (byte) get32Low(plainTextBytes[i] * 4);
            }
        }

        while (n1 < 30) {
            encodedBytes1[n1++] = (byte) getRandom(32);
        }

        while (n2 < 30) {
            encodedBytes2[n2++] = (byte) getRandom(32);
        }

        int pos1 = getRandom(plainTextBytes.length);
        int pos2 = getRandom(plainTextBytes.length);
        sort(encodedBytes1, pos1);
        sort(encodedBytes2, pos2);
        int check = (sumSqual(encodedBytes1) + sumSqual(encodedBytes2)) % 32;

        byte[] encodedArray = new byte[64];
        encodedArray[0] = (byte) pos1;
        encodedArray[1] = (byte) pos2;
        System.arraycopy(encodedBytes1, 0, encodedArray, 2, encodedBytes1.length);
        System.arraycopy(encodedBytes2, 0, encodedArray, 2 + encodedBytes1.length, encodedBytes2.length);
        encodedArray[encodedArray.length - 2] = (byte) plainText.length();
        encodedArray[encodedArray.length - 1] = (byte) check;
        byte[] ps = new byte[encodedArray.length];

        for (int i = 0; i < encodedArray.length; i++) {
            ps[i] = (byte) chs[encodedArray[i]];
        }

        return new String(ps);
    }

    /**
     * 自身混淆解密。如果不是合法的加密串（长度不是64个字节），会直接返回原字符串。
     *
     * @param str
     *            加密的字符串
     * @return 解密后字符串
     * @see {@link #encodeBySelf(String)}
     */
    public static String decodeBySelf(String str) {
        // 如果不是合法的加密串，则直接返回
        if (str == null || str.length() != 64) {
            return str;
        }

        byte[] bb = new byte[str.length()];
        byte[] sb = str.getBytes();

        for (int i = 0; i < sb.length; i++) {
            for (int j = 0; j < 32; j++) {
                if (((byte) chs[j]) == sb[i]) {
                    bb[i] = (byte) j;
                    break;
                }
            }
        }

        int sl = bb[bb.length - 2];
        int p1 = bb[0];
        int p2 = bb[1];

        byte[] bb1 = new byte[30];
        byte[] bb2 = new byte[30];

        int bb2l;
        if (sl % 2 == 0) {
            bb2l = sl;
        } else {
            bb2l = sl - 1;
        }

        System.arraycopy(bb, 2, bb1, 0, bb1.length);
        System.arraycopy(bb, 2 + bb1.length, bb2, 0, bb2.length);

        unsort(bb1, p1);
        unsort(bb2, p2);
        byte[] oldb = new byte[sl];
        for (int i = 0; i < sl; i += 2) {
            oldb[i] = (byte) (getIntFrom32(bb1[i], bb1[i + 1]) / 4);
            if (i + 1 < bb2l) {
                oldb[i + 1] = (byte) (getIntFrom32(bb2[i], bb2[i + 1]) / 4);
            }
        }

        return new String(oldb);
    }

    /**
     * 使用 3DES 加密，然后使用 Base64 编码。
     *
     * @param str
     *            源字符串
     * @param key
     *            密钥
     * @return 加密后字符串
     * @see {@link #decodeBy3DESAndBase64(String, String)}
     */
    public static String encodeBy3DESAndBase64(String str, String key) {
        String encoded = null;

        try {
            byte[] rawkey = key.getBytes();
            DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
            SecretKey deskey = keyfactory.generateSecret(keyspec);

            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] cipherText = cipher.doFinal(str.getBytes());
            encoded = new String(Base64.encodeBase64(cipherText));
        } catch (Exception e) {
            logger.error("Encode by 3DES & Base64 error", e);
        }

        return encoded;
    }

    /**
     * 使用 Base64 解码，然后使用 3DES 解密。
     *
     * @param str
     *            加密的字符串
     * @param key
     *            密钥
     * @return 解密后字符串
     * @see {@link #encodeBy3DESAndBase64(String, String)}
     */
    public static String decodeBy3DESAndBase64(String str, String key) {
        String decoded = null;

        try {
            byte[] rawkey = key.getBytes();
            DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
            SecretKey deskey = keyfactory.generateSecret(keyspec);

            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            byte[] clearText = cipher.doFinal(Base64.decodeBase64(str));
            decoded = new String(clearText);
        } catch (Exception e) {
            logger.error("Decode by 3DES & Base64 error", e);
        }

        return decoded;
    }

    /**
     * 使用 RSA 加密，然后使用 Base64 编码。
     *
     * @param str
     *            源字符串
     * @param publicKey
     *            公钥
     * @return 加密后字符串
     * @see {@link #decodeByRSAAndBase64(String, String)}
     */
    public static String encodeByRSAAndBase64(String str, String publicKey) {
        String encoded = null;

        try {
            byte[] byteKey = Base64.decodeBase64(publicKey);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
            RSAPublicKey pubKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(byteKey));

            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            // 加密时超过 blockSize 字节就报错，为此采用分段加密
            int blockSize = pubKey.getModulus().bitLength() / 8 - 11;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = str.getBytes();
            for (int i = 0; i < data.length; i += blockSize) {
                byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + blockSize));
                out.write(doFinal);
            }
            byte[] cipherText = out.toByteArray();
            encoded = new String(Base64.encodeBase64(cipherText));
        } catch (Exception e) {
            logger.error("Encode by RSA & Base64 error", e);
        }

        return encoded;
    }

    /**
     * 使用 Base64 解码，然后使用 RSA 解密。
     *
     * @param str
     *            加密的字符串
     * @param privateKey
     *            私钥
     * @return 解密后字符串
     * @see {@link #encodeByRSAAndBase64(String, String)}
     */
    public static String decodeByRSAAndBase64(String str, String privateKey) {
        String decoded = null;

        try {
            byte[] byteKey = Base64.decodeBase64(privateKey);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
            RSAPrivateKey priveKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(byteKey));

            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priveKey);

            // 解密时超过 blockSize 字节就报错，为此采用分段加密
            int blockSize = priveKey.getModulus().bitLength() / 8;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = Base64.decodeBase64(str);
            for (int i = 0; i < data.length; i += blockSize) {
                byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + blockSize));
                out.write(doFinal);
            }
            byte[] clearText = out.toByteArray();
            decoded = new String(clearText);
        } catch (Exception e) {
            logger.error("Decode by RSA & Base64 error", e);
        }

        return decoded;
    }

    /**
     * 使用 RSA 签名，然后使用 Base64 编码。
     *
     * @param str
     *            源字符串
     * @param privateKey
     *            私钥
     * @return 字符串的签名结果
     * @see {@link #verifyByRSA(String, String, String)}
     */
    public static String signByRSA(String str, String privateKey) {
        String result = null;

        try {
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
            byte[] byteKey = Base64.decodeBase64(privateKey);
            PrivateKey priveKey = factory.generatePrivate(new PKCS8EncodedKeySpec(byteKey));

            Signature sig = Signature.getInstance(ALGORITHM_RSA_SIG);
            sig.initSign(priveKey);
            sig.update(str.getBytes());
            byte[] signed = sig.sign();
            result = new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            logger.error("Sign by RSA error", e);
        }

        return result;
    }

    /**
     * 使用 Base64 解码，然后使用 RSA 公钥进行签名验证。
     *
     * @param str
     *            签名
     * @param publicKey
     *            公钥
     * @param expectSign
     *            期望签名结果
     * @return 签名是否验证通过
     * @see {@link #signByRSA(String, String)}
     */
    public static boolean verifyByRSA(String str, String publicKey, String expectSign) {
        boolean isSuccess = false;

        try {
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
            byte[] byteKey = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(byteKey));

            Signature sig = Signature.getInstance(ALGORITHM_RSA_SIG);
            sig.initVerify(pubKey);
            sig.update(str.getBytes());
            isSuccess = sig.verify(Base64.decodeBase64(expectSign));
        } catch (Exception e) {
            logger.error("Verify by RSA error", e);
        }

        return isSuccess;
    }

    /**
     * 带 secure、salt 参数的字符串 SHA1 签名。加密算法为：sha1(secure + str + salt)
     */
    public static String sha1Hex(String str, String secure, String salt) {
        return DigestUtils.sha1Hex(secure + str + salt);
    }

    /**
     * 带 secure 参数的字符串 MD5 签名。加密算法为：md5(str + secure)
     *
     * @param str
     *            需要签名的字符串
     * @param secure
     *            密钥
     * @return 签名结果
     */
    public static String md5Hex(String str, String secure) {
        String secureStr = str + secure;
        return DigestUtils.md5Hex(getBytes(secureStr, DEFAULT_CHARSET));
    }

    /**
     * 验证字符串签名是否匹配。签名算法为：SHA1(str + secure)
     *
     * @param str
     *            需要签名的字符串
     * @param secure
     *            密钥
     * @param expectSign
     *            期望签名结果
     * @return true/false
     */
    public static boolean verifyBySHA1(String str, String secure, String expectSign) {
        String secureStr = str + secure;
        String actualSign = DigestUtils.sha1Hex(getBytes(secureStr, DEFAULT_CHARSET));
        return actualSign.equals(expectSign);
    }

    /**
     * 验证字符串签名是否匹配。签名算法为：MD5(str + secure)
     *
     * @param str
     *            需要签名的字符串
     * @param secure
     *            密钥
     * @param expectSign
     *            期望签名结果
     * @return true/false
     */
    public static boolean verifyByMD5(String str, String secure, String expectSign) {
        String secureStr = str + secure;
        String actualSign = DigestUtils.md5Hex(getBytes(secureStr, DEFAULT_CHARSET));
        return actualSign.equals(expectSign);
    }

    /**
     * 验证字符串签名是否匹配。签名算法为：MD5(str + secure)
     *
     * @param str
     *            需要签名的字符串
     * @param secure
     *            密钥
     * @param expectSign
     *            期望签名结果
     * @return true/false
     * @deprecated 请使用 {@link #verifyByMD5(String, String, String)}
     */
    @Deprecated
    public static boolean verifyMd5Hex(String str, String secure, String expectSign) {
        return verifyByMD5(str, secure, expectSign);
    }

    private static int sumSqual(byte[] b) {
        int sum = 0;
        for (int i = 0; i < b.length; i++) {
            sum += (int) Math.pow(b[i], 2);
        }
        return sum;
    }

    private static int getRandom(int max) {
        return (int) (Math.random() * max);
    }

    private static void sort(byte[] b, int pos) {
        byte[] tmp = new byte[pos];
        System.arraycopy(b, 0, tmp, 0, pos);
        System.arraycopy(b, pos, b, 0, b.length - pos);
        System.arraycopy(tmp, 0, b, b.length - pos, pos);
    }

    private static void unsort(byte[] b, int pos) {
        byte[] tmp = new byte[pos];
        System.arraycopy(b, b.length - pos, tmp, 0, pos);
        System.arraycopy(b, 0, b, pos, b.length - pos);
        System.arraycopy(tmp, 0, b, 0, pos);
    }

    private static int get32Low(int num) {
        return num % 32;
    }

    private static int get32Hi(int num) {
        return num / 32;
    }

    private static int getIntFrom32(int hi, int low) {
        return hi * 32 + low;
    }

    private static byte[] getBytes(String str, String charset) {
        if (StringUtils.isBlank(charset)) {
            return str.getBytes();
        }

        try {
            return str.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
