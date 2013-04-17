/* 
 * @(#)AbstractMemoryUser.java    Created on 2013-4-17
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.io.Serializable;

/**
 * 用于存放在 session 中保持回话的用户抽象类。
 * 
 * @author akuma
 */
public class AbstractMemoryUser<T> implements Serializable {

    /**
     * AbstractMemoryUser 对象在 session 中的 key。
     */
    public static final String KEY = "memoryUser";

    private static final long serialVersionUID = -9090439224327688149L;

    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

}
