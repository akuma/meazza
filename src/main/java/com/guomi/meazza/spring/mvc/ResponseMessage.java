/*
 * @(#)ResponseMessage.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.guomi.meazza.util.UUIDUtils;

/**
 * Controller 操作的响应消息类，主要用在以 <code>ResponseBody</code> 方式返回结果的方法中。<br>
 * 它包含了 Action 提示信息、Action 错误信息、字段错误信息以及其他响应数据。
 *
 * @author akuma
 */
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 6725085407815077443L;

    /**
     * 表示成功响应的代码。
     */
    public static final int SUCCESS_CODE = 0;

    private String id = UUIDUtils.newId(); // 请求 ID，默认 UUID
    private int code = SUCCESS_CODE; // 响应代码
    private Collection<String> actionMessages = new ArrayList<>(); // 普通提示信息
    private Collection<String> actionErrors = new ArrayList<>(); // 普通出错提示
    private Map<String, Collection<String>> fieldErrors = new LinkedHashMap<>(); // 字段出错提示
    private Map<String, Object> data = new LinkedHashMap<>(); // 业务数据

    public String getId() {
        return id;
    }

    public void setId(String requestId) {
        this.id = requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取是否存在 Action 消息。
     *
     * @return true/false
     */
    @JsonInclude(Include.NON_DEFAULT)
    public boolean getHasActionMessages() {
        return !actionMessages.isEmpty();
    }

    @JsonInclude(Include.NON_EMPTY)
    public Collection<String> getActionMessages() {
        return actionMessages;
    }

    public void setActionMessages(Collection<String> actionMessages) {
        this.actionMessages = actionMessages;
    }

    @JsonInclude(Include.NON_EMPTY)
    public Collection<String> getActionErrors() {
        return actionErrors;
    }

    public void setActionErrors(Collection<String> actionErrors) {
        this.actionErrors = actionErrors;
    }

    @JsonInclude(Include.NON_EMPTY)
    public Map<String, Collection<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, Collection<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    @JsonInclude(Include.NON_NULL)
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
    @JsonInclude(Include.NON_DEFAULT)
    public boolean getHasErrors() {
        return getHasActionErrors() || getHasFieldErrors();
    }

    /**
     * 获取是否存在 Action 错误。
     *
     * @return true/false
     */
    @JsonInclude(Include.NON_DEFAULT)
    public boolean getHasActionErrors() {
        return !actionErrors.isEmpty();
    }

    /**
     * 获取是否存在 Field 错误。
     *
     * @return true/false
     */
    @JsonInclude(Include.NON_DEFAULT)
    public boolean getHasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

}
