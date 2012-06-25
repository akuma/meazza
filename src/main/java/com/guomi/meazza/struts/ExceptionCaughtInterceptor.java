/* 
 * @(#)ExceptionCaughtInterceptor.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.struts;

import org.apache.struts2.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guomi.meazza.dao.DataExistsException;
import com.guomi.meazza.dao.DataMissingException;
import com.guomi.meazza.dao.DataModifiedException;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


/**
 * 对异常捕获并根据条件进行包装的拦截器。
 * 
 * <p>
 * 对于输出方式为 JSON 的 Action，目前有两种处理方式：
 * 
 * <ul>
 * <li>如果抛出异常为 <code>DataExistsException</code>、<code>DataMissingException</code>、<code>DataModifiedException</code>，会添加
 * Action 出错提示信息；</li>
 * <li>否则捕获异常后会封装为 <code>org.apache.struts2.json.JSONException</code> 后抛出。</li>
 * </ul>
 * 
 * @author akuma
 */
public class ExceptionCaughtInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -7368264899101543792L;

    private static Logger logger = LoggerFactory.getLogger(ExceptionCaughtInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String namespace = invocation.getProxy().getNamespace();
        String actionName = invocation.getProxy().getActionName();
        logger.debug("Request action info: namespace={}, actionName={}", namespace, actionName);

        try {
            return invocation.invoke();
        } catch (Exception e) {
            if (StrutsActionUtils.isJsonAction(invocation)) {
                if (e instanceof DataExistsException || e instanceof DataMissingException
                        || e instanceof DataModifiedException) {
                    Object action = invocation.getProxy().getAction();
                    if (action instanceof ActionSupport) {
                        ActionSupport actionSupport = (ActionSupport) action;
                        actionSupport.addActionError(e.getMessage());
                        return Action.SUCCESS;
                    }
                }

                logger.error("Action invoke error", e);
                throw new JSONException(e);
            } else {
                logger.error("Action invoke error", e);
                throw e;
            }
        }
    }

}
