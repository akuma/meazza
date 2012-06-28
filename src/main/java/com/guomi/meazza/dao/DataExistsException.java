/* 
 * @(#)DataExistsException.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.dao;

import org.springframework.core.NestedRuntimeException;

/**
 * 表示类似数据已经存在的异常类。例如，添加一个数据库中已经存在的同名用户时，应当抛出此异常。
 * 
 * @author akuma
 */
public class DataExistsException extends NestedRuntimeException {

    private static final long serialVersionUID = 6019586042924994573L;

    public DataExistsException(String msg) {
        super(msg);
    }

    public DataExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
