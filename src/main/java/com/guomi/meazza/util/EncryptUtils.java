/*
 * @(#)EncryptUtils.java    Created on 2013-1-30
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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

    private static final char[] CHARS = { 'L', 'K', 'J', '4', 'D', 'G', 'F', 'V', 'R', 'T', 'Y', 'B', 'N', 'U', 'P',
            'W', '3', 'E', '5', 'H', 'M', '7', 'Q', '9', 'S', 'A', 'Z', 'X', '8', 'C', '6', '2' };

    private static final String DEFAULT_CHARSET = "UTF-8";

    // 工作模式：ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128
    // 填充方式：Nopadding/PKCS5Padding/ISO10126Padding

    // 秘钥生成算法
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";

    // 算法的 iteration 数量
    private static final int ITERATION_COUNT = 1000;

    // AES 加密算法
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 128; // 128 bit
    private static final int AES_SALT_SIZE = AES_KEY_SIZE / 8; // 16 byte

    // DES 加密算法, 可用 DES, DESede, Blowfish
    private static final String DES_ALGORITHM = "DESede"; // 3 DES
    private static final String DES_CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding";
    private static final int DES_KEY_SIZE = 192; // 64 * 3 = 168 bit
    private static final int DES_SALT_SIZE = 8; // 8 bytes

    // RSA 加密算法、签名算法
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_SIG_ALGORITHM = "SHA1withRSA";

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
        byte[] plainTextBytes = getBytes(plainText);

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
            ps[i] = (byte) CHARS[encodedArray[i]];
        }

        String result = null;
        try {
            result = new String(ps, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Encode by self error", e);
        }
        return result;
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
        byte[] sb = getBytes(str);

        for (int i = 0; i < sb.length; i++) {
            for (int j = 0; j < 32; j++) {
                if (((byte) CHARS[j]) == sb[i]) {
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

        String result = null;
        try {
            result = new String(oldb, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Decode by self error", e);
        }
        return result;
    }

    /**
     * 使用 AES 加密，然后使用 Base64 编码。
     *
     * @param str
     *            源字符串
     * @param password
     *            密码
     * @return 加密后字符串
     * @see {@link #decodeByAES(String, String)}
     */
    public static String encodeByAES(String str, String password) {
        return encodeByPBE(str, password, AES_ALGORITHM, AES_CIPHER_ALGORITHM, AES_KEY_SIZE, AES_SALT_SIZE);
    }

    /**
     * 使用 Base64 解码，然后使用 AES 解密。
     *
     * @param str
     *            加密的字符串
     * @param password
     *            密钥
     * @return 解密后字符串
     * @see {@link #encodeByAES(String, String)}
     */
    public static String decodeByAES(String str, String password) {
        return decodeByPBE(str, password, AES_ALGORITHM, AES_CIPHER_ALGORITHM, AES_KEY_SIZE, AES_SALT_SIZE);
    }

    /**
     * 使用 3DES 加密，然后使用 Base64 编码。
     *
     * @param str
     *            源字符串
     * @param password
     *            密钥
     * @return 加密后字符串
     * @see {@link #decodeBy3DES(String, String)}
     */
    public static String encodeBy3DES(String str, String password) {
        return encodeByPBE(str, password, DES_ALGORITHM, DES_CIPHER_ALGORITHM, DES_KEY_SIZE, DES_SALT_SIZE);
    }

    /**
     * 使用 Base64 解码，然后使用 3DES 解密。
     *
     * @param str
     *            加密的字符串
     * @param password
     *            密钥
     * @return 解密后字符串
     * @see {@link #encodeBy3DES(String, String)}
     */
    public static String decodeBy3DES(String str, String password) {
        return decodeByPBE(str, password, DES_ALGORITHM, DES_CIPHER_ALGORITHM, DES_KEY_SIZE, DES_SALT_SIZE);
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
            SecretKeyFactory factory = SecretKeyFactory.getInstance(DES_ALGORITHM);
            SecretKey secretKey = factory.generateSecret(new DESedeKeySpec(key.getBytes()));

            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(str.getBytes());
            encoded = Base64.encodeBase64String(cipherText);
        } catch (Exception e) {
            logger.error("Encode by 3DES error", e);
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
            SecretKeyFactory factory = SecretKeyFactory.getInstance(DES_ALGORITHM);
            SecretKey secretKey = factory.generateSecret(new DESedeKeySpec(key.getBytes()));

            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] clearText = cipher.doFinal(Base64.decodeBase64(str));
            decoded = new String(clearText, DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("Decode by 3DES error", e);
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
            KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
            RSAPublicKey pubKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(byteKey));

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            // 加密时超过 blockSize 字节就报错，为此采用分段加密
            int blockSize = pubKey.getModulus().bitLength() / 8 - 11;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = getBytes(str);
            for (int i = 0; i < data.length; i += blockSize) {
                byte[] block = cipher.doFinal(ArrayUtils.subarray(data, i, i + blockSize));
                out.write(block);
            }
            encoded = Base64.encodeBase64String(out.toByteArray());
        } catch (Exception e) {
            logger.error("Encode by RSA error", e);
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
            KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
            RSAPrivateKey priveKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(byteKey));

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, priveKey);

            // 解密时超过 blockSize 字节就报错，为此采用分段加密
            int blockSize = priveKey.getModulus().bitLength() / 8;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = Base64.decodeBase64(str);
            for (int i = 0; i < data.length; i += blockSize) {
                byte[] block = cipher.doFinal(ArrayUtils.subarray(data, i, i + blockSize));
                out.write(block);
            }
            decoded = new String(out.toByteArray(), DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("Decode by RSA error", e);
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
            KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
            byte[] byteKey = Base64.decodeBase64(privateKey);
            PrivateKey priveKey = factory.generatePrivate(new PKCS8EncodedKeySpec(byteKey));

            Signature sig = Signature.getInstance(RSA_SIG_ALGORITHM);
            sig.initSign(priveKey);
            sig.update(getBytes(str));
            byte[] signed = sig.sign();
            result = Base64.encodeBase64String(signed);
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
            KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
            byte[] byteKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = factory.generatePublic(new X509EncodedKeySpec(byteKey));

            Signature sig = Signature.getInstance(RSA_SIG_ALGORITHM);
            sig.initVerify(pubKey);
            sig.update(getBytes(str));
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

    /**
     * 使用基于 password 的 AES/3DES 等算法加密，然后使用 Base64 编码。
     *
     * @param str
     *            源字符串
     * @param password
     *            密码
     * @return 加密后字符串
     * @see {@link #decodeByPBE(String, String)}
     */
    private static String encodeByPBE(String str, String password, String algorithm, String cipherAlgorithm,
            int keySize, int saltSize) {
        String encoded = null;

        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[saltSize];
            random.nextBytes(salt);
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, keySize);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            byte[] rawKey = factory.generateSecret(keySpec).getEncoded();
            SecretKey secretKey = new SecretKeySpec(rawKey, algorithm);

            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            byte[] iv = new byte[cipher.getBlockSize()];
            random.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            byte[] cipherText = cipher.doFinal(getBytes(str));

            encoded = Base64.encodeBase64String(salt)
                    + ":"
                    + Base64.encodeBase64String(iv)
                    + ":"
                    + Base64.encodeBase64String(cipherText);
        } catch (Exception e) {
            logger.error("Encode by " + algorithm + " error", e);
        }

        return encoded;
    }

    private static String decodeByPBE(String str, String password, String algorithm, String cipherAlgorithm,
            int keySize, int saltSize) {
        String decoded = null;

        try {
            String[] fields = str.split(":");
            if (fields.length != 3) {
                logger.error("Decode by " + algorithm + " error: encrpyted string wrong");
                return decoded;
            }

            byte[] salt = Base64.decodeBase64(fields[0]);
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, keySize);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            byte[] rawKey = factory.generateSecret(keySpec).getEncoded();
            SecretKey secretKey = new SecretKeySpec(rawKey, algorithm);

            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            IvParameterSpec ivParams = new IvParameterSpec(Base64.decodeBase64(fields[1]));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            byte[] clearText = cipher.doFinal(Base64.decodeBase64(fields[2]));
            decoded = new String(clearText, DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("Decode by " + algorithm + " error", e);
        }

        return decoded;
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

    private static byte[] getBytes(String str) {
        return getBytes(str, null);
    }

    private static byte[] getBytes(String str, String charset) {
        if (StringUtils.isBlank(charset)) {
            charset = DEFAULT_CHARSET;
        }

        try {
            return str.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
