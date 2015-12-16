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
     * 判断数组是否为 {@code null}、空、仅含 1 个 {@code null} 元素的数组。
     *
     * @param array
     *            数组
     * @return 仅当数组长度大于 1，且不是仅含 1 个 {@code null} 元素时才返回 {@code true}
     */
    public static boolean isEmptyOrNull(Object[] array) {
        return isEmpty(array) || (array.length == 1 && array[0] == null);
    }

}
