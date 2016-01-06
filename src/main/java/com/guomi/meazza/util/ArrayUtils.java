/*
 * @(#)ArrayUtils.java    Created on 2015年12月16日
 * Copyright (c) 2015 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

/**
 * 操作数组的工具类。
 *
 * @author akuma
 */
public abstract class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

    /**
     * 判断数组是否为空数组。包含以下几种情况：
     * <ul>
     * <li>数组为 {@code null}</li>
     * <li>数组长度为 0</li>
     * <li>数组的元素全部是 {@code null}</li>
     * </ul>
     *
     * @param array
     *            数组
     * @return true/false
     */
    public static boolean isEmptyOrNull(Object[] array) {
        if (isEmpty(array)) {
            return true;
        }

        for (Object object : array) {
            if (object != null) {
                return false;
            }
        }

        return true;
    }

}
