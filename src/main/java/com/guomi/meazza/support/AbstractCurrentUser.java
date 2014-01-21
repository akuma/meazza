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

    private static final String INITIATIVE_LOGOUT_KEY = "initiativeLogout";

    private static final long serialVersionUID = -9090439224327688149L;

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
    public boolean isInitiativeLogout(ServletWebRequest request) {
        Boolean result = (Boolean) request.getRequest().getSession().getAttribute(INITIATIVE_LOGOUT_KEY);
        return result != null && result;
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
        request.getRequest().getSession().setAttribute(INITIATIVE_LOGOUT_KEY, true);
        request.getRequest().getSession().invalidate();
    }

}
