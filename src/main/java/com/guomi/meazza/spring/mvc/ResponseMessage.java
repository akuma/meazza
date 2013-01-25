/* 
 * @(#)ResponseMessage.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller 操作的响应消息类，主要用在以 <code>ResponseBody</code> 方式返回结果的方法中。<br>
 * 它包含了 Action 提示信息、Action 错误信息、字段错误信息以及其他响应数据。
 * 
 * @author akuma
 */
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 6725085407815077443L;

    private Collection<String> actionMessages = new ArrayList<>(0);
    private Collection<String> actionErrors = new ArrayList<>(0);
    private Map<String, Collection<String>> fieldErrors = new HashMap<>(0);

    private Map<String, Object> data = new HashMap<>(); // 其他需要返回的数据

    /**
     * 获取是否存在 Action 消息。
     * 
     * @return true/false
     */
    public boolean getHasActionMessages() {
        return !actionMessages.isEmpty();
    }

    public Collection<String> getActionMessages() {
        return actionMessages;
    }

    public void setActionMessages(Collection<String> actionMessages) {
        this.actionMessages = actionMessages;
    }

    /**
     * 获取是否存在 Action 错误。
     * 
     * @return true/false
     */
    public boolean getHasActionErrors() {
        return !actionErrors.isEmpty();
    }

    public Collection<String> getActionErrors() {
        return actionErrors;
    }

    public void setActionErrors(Collection<String> actionErrors) {
        this.actionErrors = actionErrors;
    }

    /**
     * 获取是否存在字段错误。
     * 
     * @return true/false
     */
    public boolean getHasFieldErrors() {
        return !getFieldErrors().isEmpty();
    }

    public Map<String, Collection<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, Collection<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 添加需要返回给客户端的属性。
     * 
     * @param name
     *            属性名称
     * @param value
     *            属性值
     */
    public void addAttribute(String name, Object value) {
        data.put(name, value);
    }

    /**
     * 批量添加需要返回给客户端的属性。
     * 
     * @param data
     *            属性 map
     */
    public void addAttributes(Map<String, Object> data) {
        this.data.putAll(data);
    }

}
