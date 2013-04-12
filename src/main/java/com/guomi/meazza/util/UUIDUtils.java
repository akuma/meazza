/* 
 * @(#)UUIDUtils.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.util;

import java.net.InetAddress;

/**
 * UUID 工具类。
 * 
 * @author akuma
 */
public abstract class UUIDUtils {

    private static UUIDGenerator uuid = new UUIDGenerator();

    /**
     * 产生新的UUID
     * 
     * @return 32位长的UUID字符串
     */
    public static String newId() {
        return uuid.generateHex();
    }

    private static class UUIDGenerator {

        private static final int IP;
        static {
            int ipadd;
            try {
                ipadd = toInt(InetAddress.getLocalHost().getAddress());
            } catch (Exception e) {
                ipadd = 0;
            }
            IP = ipadd;
        }

        private static short counter = (short) 0;
        private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

        /**
         * 构造方法
         */
        public UUIDGenerator() {
        }

        /**
         * 生成16进制表达的字符串 UUID，其中字母采用小写形式。
         * 
         * @return 32个字节长度的 UUID 字符串
         */
        public String generateHex() {
            StringBuilder sb = new StringBuilder(32);
            sb.append(format(getIP()));
            sb.append(format(getJVM()));
            sb.append(format(getHighTime()));
            sb.append(format(getLowTime()));
            sb.append(format(getCount()));
            return sb.toString();
        }

        private String format(int intval) {
            String formatted = Integer.toHexString(intval);
            StringBuilder buf = new StringBuilder("00000000");
            buf.replace(8 - formatted.length(), 8, formatted);
            return buf.toString();
        }

        private String format(short shortval) {
            String formatted = Integer.toHexString(shortval);
            StringBuilder buf = new StringBuilder("0000");
            buf.replace(4 - formatted.length(), 4, formatted);
            return buf.toString();
        }

        /**
         * Unique across JVMs on this machine (unless they load this class in the same quater second - very unlikely)
         */
        private int getJVM() {
            return JVM;
        }

        /**
         * Unique in a millisecond for this JVM instance (unless there are > Short.MAX_VALUE instances created in a
         * millisecond)
         */
        private short getCount() {
            synchronized (UUIDGenerator.class) {
                if (counter < 0) {
                    counter = 0;
                }
                return counter++;
            }
        }

        /**
         * Unique in a local network
         */
        private int getIP() {
            return IP;
        }

        /**
         * Unique down to millisecond
         */
        private short getHighTime() {
            return (short) (System.currentTimeMillis() >>> 32);
        }

        private int getLowTime() {
            return (int) System.currentTimeMillis();
        }

        private static int toInt(byte[] bytes) {
            int result = 0;
            for (int i = 0; i < 4; i++) {
                result = (result << 8) - Byte.MIN_VALUE + bytes[i];
            }
            return result;
        }
    }

}
