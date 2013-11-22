/*
 * @(#)AbstractCurrentUser.java    Created on 2013-4-17
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.io.Serializable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * 用于存放在 session 中保持回话的用户抽象类。
 * 
 * @author akuma
 */
public class AbstractCurrentUser<T> implements Serializable {

    /**
     * CurrentUser 对象在 session 中的 key。
     */
    public static final String SESSION_KEY = "currentUser";

    private static final long serialVersionUID = -9090439224327688149L;

    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    /**
     * 将 CurrentUser 保存到 Session 中，用于保持回话。
     */
    public void saveSession(WebRequest request) {
        request.setAttribute(SESSION_KEY, this, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 从 Session 中删除 CurrentUser 并销毁当前 Session。
     */
    public void destorySession(ServletWebRequest request) {
        //request.removeAttribute(SESSION_KEY, RequestAttributes.SCOPE_SESSION);
        request.getRequest().getSession().invalidate();
    }

}
