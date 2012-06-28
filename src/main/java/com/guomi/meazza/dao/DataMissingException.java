/* 
 * @(#)DataMissingException.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.dao;

import org.springframework.core.NestedRuntimeException;

/**
 * 表示数据已经不存在的异常类。例如，一般查看或修改一条已经被删除的记录时，应当抛出此异常。
 * 
 * @author akuma
 */
public class DataMissingException extends NestedRuntimeException {

    private static final long serialVersionUID = 6386306283767125288L;

    public DataMissingException(String msg) {
        super(msg);
    }

    public DataMissingException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
