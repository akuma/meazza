/*
 * @(#)ObjectHelper.java    Created on 2013-4-27
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

/**
 * 处理对象的工具类。
 * 
 * @author akuma
 */
public abstract class ObjectHelper {

    private static final Logger logger = LoggerFactory.getLogger(ObjectHelper.class);

    /**
     * 创建一个 <code>targetClass</code> 对象，并从 <code>source</code> 对象中复制相同属性名的值。
     */
    public static <S, T> T copyProperties(S source, Class<T> targetClass) {
        T target = null;
        try {
            target = targetClass.newInstance();
        } catch (Exception e) {
            logger.error("Create dest object error", e);
            return null;
        }

        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            logger.error("Copy properties to target object error", e);
        }
        return target;
    }

    /**
     * 创建一个 <code>targetClass</code> 对象列表，并依次从 <code>source</code> 对象中复制相同属性名的值。
     */
    public static <S, T> List<T> copyProperties(List<S> sources, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }

        List<T> targets = new ArrayList<>();
        for (S s : sources) {
            targets.add(copyProperties(s, targetClass));
        }
        return targets;
    }

    /**
     * 获取 Bean 对象的属性值。当 name 不存在时，返回 null。
     */
    public static Object getPropertyValueQuietly(Object bean, String name) {
        try {
            return PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            // ignore
            return null;
        }
    }

    /**
     * 设置 Bean 对象的的属性值。当 name 不存在时会忽略。
     */
    public static void setPropertyValueQuietly(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            // ignore
        }
    }

}
