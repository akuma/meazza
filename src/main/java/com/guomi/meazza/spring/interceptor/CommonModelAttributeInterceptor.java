/* 
 * @(#)CommonModelAttributeInterceptor.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.interceptor;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.guomi.meazza.util.ServletUtils;

/**
 * 拦截 <code>Controller</code> 方法，将公共数据作为属性添加到 Spring <code>Model</code> 对象中的拦截器。 <br>
 * 主要用于页面上通用信息的展示，这些信息不适合交给 <code>Controller</code> 去设置。
 * 
 * <p>
 * 默认已经添加的属性是：
 * 
 * <table border="1" cellpadding="5" cellspacing="1">
 * <tr>
 * <td>属性名</td>
 * <td>属性值</td>
 * </tr>
 * <tr>
 * <td>realRemoteAddr</td>
 * <td>客户端真实 IP 地址（如果使用了代理服务器，直接使用 {@link ServletRequest#getRemoteAddr()} 获取到的地址是代理服务器的地址），例如： 60.190.244.158</td>
 * </tr>
 * </table>
 * 
 * <p>
 * <b>注意： <code>Controller</code> 的方法必须要满足以下条件才会被拦截：</b>
 * 
 * <ul>
 * <li>返回结果是 <code>ModelAndView</code> 对象或者 <code>View</code> 名称</li>
 * <li>请求方式不能是 Redirect 方式的</li>
 * </ul>
 * 
 * @author akuma
 */
public class CommonModelAttributeInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CommonModelAttributeInterceptor.class);

    private Map<String, Object> commonAttributes;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 如果不存在 ModelAndView 或者 ModelAndView 里没有指定 View，则直接返回
        if (modelAndView == null || !modelAndView.hasView()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Request {} has no modelAndView.", request.getRequestURI());
            }
            return;
        }

        String viewName = modelAndView.getViewName();
        boolean isRedirectView = (modelAndView.getView() instanceof RedirectView)
                || (viewName != null && viewName.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX));
        if (logger.isDebugEnabled()) {
            logger.debug("Request {} {} a redirect uri.", request.getRequestURI(), isRedirectView ? "is" : "is not");
        }

        // 如果 ModelAndView 是 Redirect 方式，则直接返回
        if (isRedirectView) {
            return;
        }

        // 添加公共属性到 Model 中
        addCommonModelData(request, modelAndView);
        if (logger.isDebugEnabled()) {
            logger.debug("Added common model data for {}.", request.getRequestURI());
        }
    }

    /**
     * 添加共同数据到请求对象中。
     * 
     * @param request
     *            http 请求对象
     * @param modelAndView
     *            Spring ModelAndView
     */
    public void addCommonModelData(HttpServletRequest request, ModelAndView modelAndView) {
        // 添加用户真实地址
        modelAndView.addObject("realRemoteAddr", getRealRemoteAddr(request));

        // 添加通用属性
        if (commonAttributes != null) {
            modelAndView.addAllObjects(commonAttributes);
        }
    }

    /**
     * 设置通用属性。这些属性会被设置到 <code>Request</code> 对象中，用于页面展现。
     * 
     * @param commonAttributes
     *            需要设置的通用属性 Map，key 是属性的名称，value 是属性值
     */
    public void setCommonAttributes(Map<String, Object> commonAttributes) {
        this.commonAttributes = commonAttributes;
    }

    /**
     * 获取用户真实的 IP 地址。
     * 
     * @param request
     *            http 请求对象
     * @return 用户真实的 IP 地址
     */
    private String getRealRemoteAddr(HttpServletRequest request) {
        return ServletUtils.getRealRemoteAddr(request);
    }

}
