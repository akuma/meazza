/* 
 * @(#)IdEntity.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.dao;

import java.io.Serializable;

/**
 *  统一定义 id 的 entity 基类。
 * 
 * @author akuma
 * @param <T>
 *            ID 的类型
 */
public abstract class IdEntity<T> implements Serializable {

    private static final long serialVersionUID = 7685930087139789958L;

    protected T id;

    public IdEntity() {
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

}
