/*
 * @(#)DefaultControllerAdvice.java    Created on 2014年1月10日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

import com.guomi.meazza.util.ServletUtils;

/**
 * 添加 Spring {@code ControllerAdvice} 注解的类。主要提供了以下功能：
 *
 * <ul>
 * <li>对异常进行统一拦截，如果是 AJAX 请求会直接返回 JSON 格式的出错响应，否则 dispatch 到出错视图。</li>
 * <li>对于非 AJAX 的请求，默认的出错视图名称为 'error'，可以使用 {@link #setDefaultErrorView(String)} 修改默认值，也可以使用
 * {@link #setExceptionMappings(Map)} 给不同的异常指定不同的出错视图。</li>
 * </ul>
 *
 * @author akuma
 */
@ControllerAdvice
public class DefaultControllerAdvice {

    public static final String EXCEPTION_ATTRIBUTE_NAME = "exception";
    public static final String EXCEPTION_STATUS_ATTRIBUTE_NAME = "status";
    public static final String EXCEPTION_MESSAGE_ATTRIBUTE_NAME = "message";
    public static final String EXCEPTION_STACKTRACE_ATTRIBUTE_NAME = "stackTrace";

    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志对象

    // 默认的错误视图名称
    private String defaultErrorView = "error";

    // 异常类名称和错误视图名称的映射表
    protected Map<String, String> exceptionMappings = new HashMap<>();

    public DefaultControllerAdvice() {
        // 给常用异常指定默认的出错视图
        exceptionMappings.put(UnauthorizedException.class.getName(), "401");
        exceptionMappings.put(ForbiddenException.class.getName(), "403");
        exceptionMappings.put(NotFoundException.class.getName(), "404");
    }

    /**
     * 设置默认的出错视图名称，默认为 'error'。
     */
    @Autowired(required = false)
    @Qualifier("defaultErrorView")
    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    /**
     * 设置异常和出错视图名称的映射表。可以根据这个实现针对不同的异常显示不同的出错页面。
     */
    @Value("#{exceptionMappings}")
    public void setExceptionMappings(Map<String, String> exceptionMappings) {
        this.exceptionMappings = exceptionMappings;
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
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleException(Exception e, ServletWebRequest request) {
        if (e instanceof HttpRequestMethodNotSupportedException) {
            logger.info("Method " + request.getHttpMethod() + " not supported error", e);
            logger.info("Request url: {}", request.getRequest().getRequestURL());
        } else {
            logger.error("Exception handler caught an exception", e);
            logger.error("Exception request: {}", request.getRequest().getRequestURL());
        }

        return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR.value(), request);
    }

    /**
     * 拦截 {@link UnauthorizedException} 异常。
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Object handleException(UnauthorizedException e, ServletWebRequest request) {
        return handleException(e, HttpStatus.UNAUTHORIZED.value(), request);
    }

    /**
     * 拦截 {@link ForbiddenException} 异常。
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Object handleException(ForbiddenException e, ServletWebRequest request) {
        return handleException(e, HttpStatus.FORBIDDEN.value(), request);
    }

    /**
     * 拦截 {@link NotFoundException} 异常。
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Object handleException(NotFoundException e, ServletWebRequest request) {
        return handleException(e, HttpStatus.NOT_FOUND.value(), request);
    }

    /**
     * 拦截 {@link MultipartException} 异常。
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleException(MultipartException e, ServletWebRequest request) {
        return handleFileUploadException(e, HttpStatus.BAD_REQUEST.value(), "请选择您要上传的文件", request);
    }

    /**
     * 拦截 {@link MaxUploadSizeExceededException} 异常。
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public Object handleException(MaxUploadSizeExceededException e, ServletWebRequest request) {
        MaxUploadSizeExceededException ex = e;
        String maxDisplaySize = FileUtils.byteCountToDisplaySize(ex.getMaxUploadSize());
        String message = "上传的文件最大不能超过 " + maxDisplaySize;
        return handleFileUploadException(e, HttpStatus.PAYLOAD_TOO_LARGE.value(), message, request);
    }

    /**
     * 判断请求是否是 Ajax 请求。
     */
    protected boolean isAjaxRequest(WebRequest request) {
        String header = request.getHeader(ServletUtils.AJAX_REQUEST_HEADER);
        return ServletUtils.AJAX_REQUEST_HEADER_VALUE.equalsIgnoreCase(header);
    }

    /**
     * 处理异常。如果是 AJAX 请求，以 JSON 格式返回；如果是普通请求，dispatch 到错误页面。
     */
    private Object handleException(Exception e, int status, ServletWebRequest request) {
        return handleException(e, status, null, request);
    }

    /**
     * 处理异常。如果是 AJAX 请求，以 JSON 格式返回；如果是普通请求，dispatch 到错误页面。
     */
    private Object handleException(Exception e, int status, String message, ServletWebRequest request) {
        Map<String, Object> errorResponse = getErrorResponse(e, message);

        // 如果开启 debug，则将 debug 标记写入 error response 中
        boolean isDebug = BooleanUtils.toBoolean(request.getParameter("debug"));
        logger.debug("Debug is {}", isDebug ? "on" : "off");
        if (isDebug) {
            errorResponse.put("debug", true);
        }

        // AJAX 请求需要手工指定 status
        if (isAjaxRequest(request)) {
            request.getResponse().setStatus(status);
            return errorResponse;
        }

        // 如果是普通请求，dispatch 到错误页面
        String exName = e.getClass().getName();
        String exView = StringUtils.defaultString(exceptionMappings.get(exName), defaultErrorView);
        return new ModelAndView(exView, errorResponse);
    }

    /**
     * 处理文件上传异常，以 JSON 格式返回。
     */
    private Object handleFileUploadException(Exception e, int status, String message, ServletWebRequest request) {
        return getErrorResponse(e, status, message);
    }

    /**
     * 根据异常创建出错后的响应对象。
     */
    private Map<String, Object> getErrorResponse(Exception e, String message) {
        return getErrorResponse(e, 200, message);
    }

    /**
     * 根据异常创建出错后的响应对象。
     */
    private Map<String, Object> getErrorResponse(Exception e, int status, String message) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        Map<String, Object> exception = new HashMap<>();
        exception.put(EXCEPTION_STACKTRACE_ATTRIBUTE_NAME, stackTrace.toString());
        exception.put(EXCEPTION_STATUS_ATTRIBUTE_NAME, status);

        if (StringUtils.isBlank(message)) {
            exception.put(EXCEPTION_MESSAGE_ATTRIBUTE_NAME, e.getMessage());
        } else {
            exception.put(EXCEPTION_MESSAGE_ATTRIBUTE_NAME, message);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(EXCEPTION_ATTRIBUTE_NAME, exception);
        return errorResponse;
    }

}
