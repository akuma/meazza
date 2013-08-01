/* 
 * @(#)ForbiddenException.java    Created on 2013-8-1
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 表示客户端无权访问某个资源的异常。
 * 
 * @author akuma
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = -8846759249114235850L;

    private int code = HttpStatus.FORBIDDEN.value();

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
