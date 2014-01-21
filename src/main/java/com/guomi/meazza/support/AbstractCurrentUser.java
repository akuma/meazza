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

    // 用户是否主动退出系统，被动退出的情况就是 session 超时
    private boolean initiativeLogout;

    protected T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    /**
     * 判断用户是否已经主动退出了。
     */
    public boolean isInitiativeLogout() {
        return initiativeLogout;
    }

    /**
     * 将 CurrentUser 保存到 Session 中，用于保持回话。
     */
    public void saveSession(WebRequest request) {
        request.setAttribute(SESSION_KEY, this, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 销毁当前 Session。
     */
    public void destorySession(ServletWebRequest request) {
        //  将标记用户为主动退出并销毁 Session
        initiativeLogout = true;
        request.getRequest().getSession().invalidate();
    }

}
