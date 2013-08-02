/* 
 * @(#)NotFoundException.java    Created on 2013-8-2
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 表示客户端的请求的资源不存在的异常。
 * 
 * @author akuma
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7505326482876250758L;

    private int code = HttpStatus.NOT_FOUND.value();

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
