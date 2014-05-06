/*
 * @(#)AbstractController.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Spring {@code Controller} 抽象类，可供业务 {@code Controller} 继承。 提供了添加错误信息、提示信息的相关方法。
 * 
 * @author akuma
 */
public abstract class AbstractController implements ValidationSupport {

    public static final String ACTION_ERRORS = "actionErrors";
    public static final String ACTION_MESSAGES = "actionMessages";
    public static final String FIELD_ERRORS = "fieldErrors";

    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志对象

    @Resource
    protected MessageSource messageSource;

    @Override
    public Collection<String> getActionMessages(Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<String>(0);
        }

        return Collections.unmodifiableCollection(messages);
    }

    @Override
    public Collection<String> getActionErrors(Model model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>(0);
        }

        return Collections.unmodifiableCollection(errors);
    }

    @Override
    public Map<String, Collection<String>> getFieldErrors(Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Collection<String>> errorsMap = (Map<String, Collection<String>>) model.asMap().get(FIELD_ERRORS);
        if (CollectionUtils.isEmpty(errorsMap)) {
            errorsMap = new LinkedHashMap<String, Collection<String>>(0);
        }

        return Collections.unmodifiableMap(errorsMap);
    }

    @Override
    public void addActionMessage(String aMessage, Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<String>();
            model.addAttribute(ACTION_MESSAGES, messages);
        }

        messages.add(aMessage);
    }

    public void addActionMessage(String aMessage, RedirectAttributes model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<String>();
            model.addFlashAttribute(ACTION_MESSAGES, messages);
        }

        messages.add(aMessage);
    }

    @Override
    public void addActionError(String anErrorMessage, Model model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>();
            model.addAttribute(ACTION_ERRORS, errors);
        }

        errors.add(anErrorMessage);
    }

    public void addActionError(String anErrorMessage, RedirectAttributes model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>();
            model.addFlashAttribute(ACTION_ERRORS, errors);
        }

        errors.add(anErrorMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errorsMap = (Map<String, List<String>>) model.asMap().get(FIELD_ERRORS);
        if (CollectionUtils.isEmpty(errorsMap)) {
            errorsMap = new LinkedHashMap<String, List<String>>();
            model.addAttribute(FIELD_ERRORS, errorsMap);
        }

        List<String> errors = errorsMap.get(fieldName);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>();
            errorsMap.put(fieldName, errors);
        }

        errors.add(errorMessage);
    }

    @Override
    public boolean hasActionMessages(Model model) {
        return !getActionMessages(model).isEmpty();
    }

    @Override
    public boolean hasActionErrors(Model model) {
        return !getActionErrors(model).isEmpty();
    }

    @Override
    public boolean hasFieldErrors(Model model) {
        return !getFieldErrors(model).isEmpty();
    }

    @Override
    public boolean hasErrors(Model model) {
        return hasActionErrors(model) || hasFieldErrors(model);
    }

    /**
     * 从 Spring 的 <code>org.springframework.ui.Model</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     * 
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Model model) {
        ResponseMessage message = new ResponseMessage();
        message.setActionMessages(getActionMessages(model));
        message.setActionErrors(getActionErrors(model));
        message.setFieldErrors(getFieldErrors(model));
        return message;
    }

    /**
     * 从 Spring 的 <code>org.springframework.validation.Errors</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     * 
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Errors errors) {
        ResponseMessage message = new ResponseMessage();

        // 设置全局错误信息
        Collection<String> actionErrors = new ArrayList<String>();
        List<ObjectError> springActionErrors = errors.getGlobalErrors();
        for (ObjectError error : springActionErrors) {
            actionErrors.add(messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(),
                    null));
        }
        message.setActionErrors(actionErrors);

        // 设置字段错误信息
        Map<String, Collection<String>> fieldErrorsMap = new LinkedHashMap<String, Collection<String>>();
        List<FieldError> springFieldErrors = errors.getFieldErrors();
        for (FieldError error : springFieldErrors) {
            Collection<String> fieldErrors = fieldErrorsMap.get(error.getField());
            if (fieldErrors == null) {
                fieldErrors = new ArrayList<String>();
                fieldErrorsMap.put(error.getField(), fieldErrors);
            }
            String fieldError = messageSource.getMessage(error.getCode(), error.getArguments(),
                    error.getDefaultMessage(), null);
            fieldErrors.add(fieldError);
        }
        message.setFieldErrors(fieldErrorsMap);

        return message;
    }

    /**
     * 返回 spring mvc 重定向结果，例如：redirect:login。
     */
    protected String redirectTo(String url) {
        logger.debug("Redirect to {}", url);
        return "redirect:" + url;
    }

}
