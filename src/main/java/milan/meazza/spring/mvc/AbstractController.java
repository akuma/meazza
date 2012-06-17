/* 
 * @(#)AbstractController.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.spring.mvc;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * <code>Controller</code> 抽象类，可供业务 <code>Controller</code> 继承。主要提供了以下功能：
 * 
 * <ul>
 * <li>提供了添加错误信息、提示信息的相关方法。</li>
 * <li>对异常进行统一拦截，如果是 AJAX 请求会 dispatch 到 JSON 异常页面，否则 dispatch 到普通异常页面。</li>
 * </ul>
 * 
 * @author akuma
 */
public abstract class AbstractController implements ValidationSupport {

    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志对象

    private static final String ACTION_ERRORS = "actionErrors";
    private static final String ACTION_MESSAGES = "actionMessages";
    private static final String FIELD_ERRORS = "fieldErrors";

    private static final String AJAX_REQUEST_HEADER_NAME = "X-Requested-With";
    private static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";

    private String exceptionViewName = "exceptionPage";
    private String exceptionJsonViewName = "exceptionJson";

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
        message.setData(model.asMap());
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
     * 处理异常 {@link Exception} 的方法，它会记录系统日志然后跳转到异常页面。
     * 
     * @param ex
     *            异常信息
     * @param request
     *            Spring WebRequest
     * @return 异常视图
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);

        StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));

        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(ex.getMessage());
        response.setStackTrace(stackTrace.toString());
        request.setAttribute("exception", response, RequestAttributes.SCOPE_REQUEST);

        return isAjaxRequest(request) ? exceptionJsonViewName : exceptionViewName;
    }

    /**
     * 设置异常拦截时需要跳转的视图名称，默认为 exceptionPage。
     * 
     * @param exceptionViewName
     *            view name
     */
    public void setExceptionViewName(String exceptionViewName) {
        this.exceptionViewName = exceptionViewName;
    }

    /**
     * 设置异常拦截时需要跳转的 JSON 视图名称，默认为 exceptionJson。
     * 
     * @param exceptionJsonViewName
     *            view name
     */
    public void setExceptionJsonViewName(String exceptionJsonViewName) {
        this.exceptionJsonViewName = exceptionJsonViewName;
    }

    /**
     * 判断请求是否是 Ajax 请求。
     * 
     * @param request
     *            Spring WebRequest
     * @return true/false
     */
    protected boolean isAjaxRequest(WebRequest request) {
        String header = request.getHeader(AJAX_REQUEST_HEADER_NAME);
        return AJAX_REQUEST_HEADER_VALUE.equalsIgnoreCase(header);
    }

    /**
     * 异常消息响应封装类。
     */
    public static class ExceptionResponse {

        private String message;
        private String stackTrace;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }

    }

}
