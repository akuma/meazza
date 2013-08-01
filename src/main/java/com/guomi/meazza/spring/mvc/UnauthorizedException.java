/*
 * @(#)UnauthorizedException.java    Created on 2013-5-8
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 表示客户端的请求未授权的异常。
 * 
 * @author akuma
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = -7505326482876250758L;

    private int code = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
