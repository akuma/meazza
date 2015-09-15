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

import com.guomi.meazza.util.UUIDUtils;

/**
 * Controller 操作的响应消息类，主要用在以 <code>ResponseBody</code> 方式返回结果的方法中。<br>
 * 它包含了 Action 提示信息、Action 错误信息、字段错误信息以及其他响应数据。
 *
 * @author akuma
 */
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 6725085407815077443L;

    private long code = 0; // 响应代码，默认 0 表示成功
    private String requestId = UUIDUtils.newId(); // 请求的 ID，默认 UUID
    private Collection<String> actionMessages = new ArrayList<>();
    private Collection<String> actionErrors = new ArrayList<>();
    private Map<String, Collection<String>> fieldErrors = new HashMap<>();

    private Map<String, Object> data = new HashMap<>(); // 其他需要返回的数据

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

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

    public Collection<String> getActionErrors() {
        return actionErrors;
    }

    public void setActionErrors(Collection<String> actionErrors) {
        this.actionErrors = actionErrors;
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

    /**
     * 添加 Action 提示信息。
     *
     * @param message
     *            提示信息
     */
    public void addActionMessage(String message) {
        actionMessages.add(message);
    }

    /**
     * 添加 Action 错误信息。
     *
     * @param error
     *            错误信息
     */
    public void addActionError(String error) {
        actionErrors.add(error);
    }

    /**
     * 添加字段错误信息。
     *
     * @param field
     *            字段名
     * @param error
     *            错误信息
     */
    public void addFieldErrors(String field, String error) {
        Collection<String> errors = fieldErrors.get(field);
        if (errors == null) {
            errors = new ArrayList<>();
            fieldErrors.put(field, errors);
        }
        errors.add(error);
    }

    /**
     * 获取是否存在 Action 或者 Field 错误。
     *
     * @return true/false
     */
    public boolean getHasErrors() {
        return getHasActionErrors() || getHasFieldErrors();
    }

    /**
     * 获取是否存在 Action 错误。
     *
     * @return true/false
     */
    public boolean getHasActionErrors() {
        return !actionErrors.isEmpty();
    }

    /**
     * 获取是否存在 Field 错误。
     *
     * @return true/false
     */
    public boolean getHasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

}
