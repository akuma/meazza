/*
 * @(#)JsonpAdvice.java    Created on 2015年8月28日
 * Copyright (c) 2015 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

/**
 * 让 Spring MVC 支持 JSONP 响应的 Advice。
 *
 * @author akuma
 */
@ControllerAdvice
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonpAdvice() {
        super("jsoncallback", "jsonCallback");
    }

}
