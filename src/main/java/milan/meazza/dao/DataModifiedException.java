/* 
 * @(#)DataModifiedException.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.dao;

import org.springframework.core.NestedRuntimeException;

/**
 * 表示数据已经被修改的异常类。例如，当用户修改一条记录的过程中，其他人先完成了修改并提交，那么此用户提交时应当抛出此异常。
 * 
 * @author akuma
 */
public class DataModifiedException extends NestedRuntimeException {

    private static final long serialVersionUID = 1135982038319419611L;

    public DataModifiedException(String msg) {
        super(msg);
    }

    public DataModifiedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
