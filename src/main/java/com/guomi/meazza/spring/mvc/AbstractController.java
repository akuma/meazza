/*
 * @(#)AbstractController.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guomi.meazza.util.Pagination;
import com.guomi.meazza.util.ServletUtils;
import com.guomi.meazza.util.StringUtils;

/**
 * Spring {@code Controller} 抽象类，可供业务 {@code Controller} 继承。 提供了添加错误信息、提示信息的相关方法。
 *
 * @author akuma
 */
public abstract class AbstractController implements ValidationSupport {

    public static final String ACTION_ERRORS = "actionErrors";
    public static final String ACTION_MESSAGES = "actionMessages";
    public static final String FIELD_ERRORS = "fieldErrors";

    private static String SPRING_PACKAGE_PREFIX = "org.springframework.";
    private static String SPRING_BINDING_RESULT_PREFIX = "org.springframework.validation.BindingResult.";

    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志对象

    @Resource
    protected MessageSource messageSource;

    @Override
    public Collection<String> getActionMessages(Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    @Override
    public Collection<String> getActionErrors(Model model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    @Override
    public Map<String, Collection<String>> getFieldErrors(Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Collection<String>> errorsMap = (Map<String, Collection<String>>) model.asMap().get(FIELD_ERRORS);
        if (errorsMap == null) {
            errorsMap = new LinkedHashMap<>();
        }
        return errorsMap;
    }

    @Override
    public void addActionMessage(String aMessage, Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<>();
            model.addAttribute(ACTION_MESSAGES, messages);
        }

        messages.add(aMessage);
    }

    public void addActionMessage(String aMessage, RedirectAttributes model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<>();
            model.addFlashAttribute(ACTION_MESSAGES, messages);
        }

        messages.add(aMessage);
    }

    @Override
    public void addActionError(String anErrorMessage, Model model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<>();
            model.addAttribute(ACTION_ERRORS, errors);
        }

        errors.add(anErrorMessage);
    }

    public void addActionError(String anErrorMessage, RedirectAttributes model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<>();
            model.addFlashAttribute(ACTION_ERRORS, errors);
        }

        errors.add(anErrorMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errorsMap = (Map<String, List<String>>) model.asMap().get(FIELD_ERRORS);
        if (CollectionUtils.isEmpty(errorsMap)) {
            errorsMap = new LinkedHashMap<>();
            model.addAttribute(FIELD_ERRORS, errorsMap);
        }

        List<String> errors = errorsMap.get(fieldName);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<>();
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
        return getResponseMessage(model, ResponseMessage.SUCCESS_CODE);
    }

    /**
     * 从 Spring 的 <code>org.springframework.ui.Model</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     *
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Model model, int code) {
        ResponseMessage message = new ResponseMessage();
        message.setCode(code);

        // 将存放在 model 中的提示消息放到返回结果中
        message.setActionMessages(getActionMessages(model));
        message.setActionErrors(getActionErrors(model));
        message.setFieldErrors(getFieldErrors(model));

        // 将存放在 model 中的数据放到返回结果中
        // 需要过滤掉部分不需要的数据
        Map<String, Object> data = new HashMap<>(model.asMap());
        data.remove(ACTION_MESSAGES);
        data.remove(ACTION_ERRORS);
        data.remove(FIELD_ERRORS);

        // 从 model 中获取除 spring binding result 之外的键值对
        // 忽略例如：org.springframework.validation.BindingResult.user & user
        Set<String> ignoreDataKeys = new HashSet<>();
        for (Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(SPRING_PACKAGE_PREFIX)) {
                ignoreDataKeys.add(key);
            }

            if (key.startsWith(SPRING_BINDING_RESULT_PREFIX)) {
                String bindingResultKey = key.substring(SPRING_BINDING_RESULT_PREFIX.length());
                Object bindingResultValue = data.get(bindingResultKey);
                if (bindingResultValue == null || !Pagination.class.equals(bindingResultValue.getClass())) {
                    ignoreDataKeys.add(bindingResultKey);
                }
            }
        }
        Map<String, Object> newData = new HashMap<>();
        for (Entry<String, Object> entry : data.entrySet()) {
            if (!ignoreDataKeys.contains(entry.getKey())) {
                newData.put(entry.getKey(), entry.getValue());
            }
        }
        message.getData().putAll(newData);

        return message;
    }

    /**
     * 从 Spring 的 <code>org.springframework.validation.Errors</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     *
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Errors errors) {
        return getResponseMessage(errors, ResponseMessage.SUCCESS_CODE);
    }

    /**
     * 从 Spring 的 <code>org.springframework.validation.Errors</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     *
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Errors errors, int code) {
        ResponseMessage message = new ResponseMessage();
        message.setCode(code);

        // 设置全局错误信息
        Collection<String> actionErrors = new ArrayList<String>();
        List<ObjectError> springActionErrors = errors.getGlobalErrors();
        for (ObjectError error : springActionErrors) {
            actionErrors.add(
                    messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), null));
        }
        message.setActionErrors(actionErrors);

        // 设置字段错误信息
        Map<String, Collection<String>> fieldErrorsMap = new LinkedHashMap<>();
        List<FieldError> springFieldErrors = errors.getFieldErrors();
        for (FieldError error : springFieldErrors) {
            Collection<String> fieldErrors = fieldErrorsMap.get(error.getField());
            if (fieldErrors == null) {
                fieldErrors = new ArrayList<>();
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

    /**
     * 判断是否是移动设备的浏览器。
     */
    protected boolean isMobileAgent(WebRequest request) {
        String ua = getUserAgent(request);
        return ua.matches(ServletUtils.REGEX_MOBILE_AGENT1)
                || ua.substring(0, 4).matches(ServletUtils.REGEX_MOBILE_AGENT2);
    }

    /**
     * 判断是否是 Android 设备的浏览器。
     */
    protected boolean isAndroidAgent(WebRequest request) {
        return getUserAgent(request).contains("android");
    }

    private String getUserAgent(WebRequest request) {
        return StringUtils.defaultString(request.getHeader("User-Agent")).toLowerCase();
    }

}
