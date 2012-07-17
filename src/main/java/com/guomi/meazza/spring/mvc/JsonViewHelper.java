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
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
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
     * 将指定的 model 以 JSON 格式输出到 HTTP 响应中。
     * 
     * @param model
     *            数据对象
     * @param response
     *            HTTP Response
     * @return 返回 null
     */
    public static ModelAndView render(Object model, HttpServletResponse response) {
        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();

        try {
            jsonConverter.write(model, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        } catch (HttpMessageNotWritableException | IOException e) {
            logger.error("Render jsonView error", e);
        }

        return null;
    }

}
