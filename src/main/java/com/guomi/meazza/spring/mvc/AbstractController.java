/* 
 * @(#)AbstractController.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guomi.meazza.util.ServletUtils;

/**
 * <code>Controller</code> 抽象类，可供业务 <code>Controller</code> 继承。
 * <p>
 * 主要提供了以下功能：
 * 
 * <ul>
 * <li>提供了添加错误信息、提示信息的相关方法。</li>
 * <li>对异常进行统一拦截，如果是 AJAX 请求会直接返回 JSON 格式的出错响应，否则 dispatch 到出错视图。</li>
 * <li>对于非 AJAX 的请求，默认的出错视图名称为 'error'，可以使用 {@link #setDefaultErrorView(String)} 修改默认值，也可以使用
 * {@link #setExceptionMappings(Map)} 给不同的异常指定不同的出错视图。</li>
 * </ul>
 * 
 * @author akuma
 */
public abstract class AbstractController implements ValidationSupport {

    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志对象

    private static final String ACTION_ERRORS = "actionErrors";
    private static final String ACTION_MESSAGES = "actionMessages";
    private static final String FIELD_ERRORS = "fieldErrors";

    private static final String EXCEPTION_ATTRIBUTE_NAME = "exception";
    private static final String EXCEPTION_MESSAGE_ATTRIBUTE_NAME = "message";
    private static final String EXCEPTION_STACKTRACE_ATTRIBUTE_NAME = "stackTrace";

    private String defaultErrorView = "error";

    protected Map<String, String> exceptionMappings = Collections.emptyMap();

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
     * @param errors
     *            Spring Model
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
     * @param errors
     *            Spring Errors
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
            fieldErrors.add(messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(),
                    null));
        }
        message.setFieldErrors(fieldErrorsMap);

        return message;
    }

    /**
     * 拦截 Controller 中抛出的 {@link Exception}，将异常信息记录到日志并添加到 HTTP 响应中。<br>
     * 对于不同的请求，对异常反馈方式有所不同：
     * <ul>
     * <li>对于 AJAX 请求，异常信息会以 JSON 格式的报文返回，格式为：{"exception":{"message":"foo","stackTrace":"bar"}</li>
     * <li>对于普通请求，异常信息会添加到 Request Attribute 中（属性名为 exception），并 dispatch 到错误视图（默认为 error）</li>
     * </ul>
     * 
     * 另外，还有两个可以修改的设置：
     * <ul>
     * <li>允许通过 {@link #setDefaultErrorView(String)} 修改默认的错误视图名称</li>
     * <li>允许通过 {@link #setExceptionMappings(Map)} 指定不同的异常类型使用不同的错误视图</li>
     * </ul>
     * 
     * @param ex
     *            异常信息
     * @param request
     *            Spring WebRequest
     * @param response
     *            HTTP 响应
     * @return
     *         对于 AJAX 请求，返回 <code>null</code>，而对于普通请求，返回错误视图对象。
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, WebRequest request, HttpServletResponse response) {
        logger.error("Exception handler caught an exception", ex);

        StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));
        Map<String, String> exp = new LinkedHashMap<>();
        exp.put(EXCEPTION_MESSAGE_ATTRIBUTE_NAME, ex.getMessage());
        exp.put(EXCEPTION_STACKTRACE_ATTRIBUTE_NAME, stackTrace.toString());
        Map<String, Map<String, String>> exRsp = new LinkedHashMap<>();
        exRsp.put(EXCEPTION_ATTRIBUTE_NAME, exp);

        // 如果是 AJAX 请求，以 JSON 格式返回
        if (isAjaxRequest(request)) {
            return JsonViewHelper.render(exRsp, response);
        }

        // 如果是普通请求，dispatch 到错误页面
        String exName = ex.getClass().getName();
        String exView = StringUtils.defaultString(exceptionMappings.get(exName), defaultErrorView);
        return new ModelAndView(exView, exRsp);
    }

    /**
     * 设置默认的出错视图名称，默认为 'error'。
     * 
     * @param defaultErrorView
     *            默认的出错视图名称
     */
    @Autowired(required = false)
    @Qualifier("defaultErrorView")
    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    /**
     * 设置异常和出错视图名称的映射表。可以根据这个实现针对不同的异常显示不同的出错页面。
     * 
     * @param exceptionMappings
     *            the exceptionMappings to set
     */
    public void setExceptionMappings(Map<String, String> exceptionMappings) {
        this.exceptionMappings = exceptionMappings;
    }

    /**
     * 判断请求是否是 Ajax 请求。
     * 
     * @param request
     *            Spring WebRequest
     * @return true/false
     */
    protected boolean isAjaxRequest(WebRequest request) {
        String header = request.getHeader(ServletUtils.AJAX_REQUEST_HEADER);
        return ServletUtils.AJAX_REQUEST_HEADER_VALUE.equalsIgnoreCase(header);
    }

}
