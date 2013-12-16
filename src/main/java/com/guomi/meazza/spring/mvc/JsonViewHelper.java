/*
 * @(#)JsonViewHelper.java    Created on 2012-7-17
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * JSON 视图工具类。
 * 
 * @author akuma
 * @since 0.0.6
 */
public class JsonViewHelper {

    private static final Logger logger = LoggerFactory.getLogger(JsonViewHelper.class);

    /**
     * 将指定的 model 以 JSON 格式输出到 HTTP 响应中。输出内容的 MIME 类型为 text/plain。
     * 
     * @param model
     *            数据对象
     * @param request
     *            Spring ServletWebRequest
     * @return 返回 null
     */
    public static ModelAndView render(Object model, ServletWebRequest request) {
        return render(model, request, MediaType.TEXT_PLAIN);
    }

    /**
     * 将指定的 model 以 JSON 格式输出到 HTTP 响应中。
     * 
     * @param model
     *            数据对象
     * @param request
     *            Spring ServletWebRequest
     * @param mediaType
     *            输出响应的 MIME 类型
     * @return 返回 null
     */
    public static ModelAndView render(Object model, ServletWebRequest request, MediaType mediaType) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        try {
            jsonConverter.write(model, mediaType, new ServletServerHttpResponse(request.getResponse()));
        } catch (HttpMessageNotWritableException | IOException e) {
            logger.error("Render jsonView error", e);
        }

        return null;
    }

    /**
     * 将指定的 model 以 JSON 格式输出到 HTTP 响应中。此方法和 {@link #render(Object, HttpServletResponse)} 的区别是没有返回值。
     * <p>
     * <strong>注意：</strong> 为了兼容 IE 系列浏览器，返回响应的 MIME 类型设置为 text/plain，而 Spring MVC 默认的是 application/json。
     * 
     * @param model
     *            数据对象
     * @param request
     *            Spring ServletWebRequest
     */
    public static void renderJust(Object model, ServletWebRequest request) {
        render(model, request, MediaType.TEXT_PLAIN);
    }

}
