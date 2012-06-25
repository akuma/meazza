/* 
 * @(#)P3PHeaderFilter.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于在 HTTP 响应内容中设置 <a href="http://baike.baidu.com/view/722330.htm">P3P</a> 头部的过滤器。
 * 
 * <p>
 * 在设置之后，响应内容中会添加类似这样的头部：
 * 
 * <pre>
 * P3P: CP="NOI CURa ADMa DEVa TAIa OUR BUS IND UNI COM NAV INT"
 * </pre>
 * 
 * web.xml 中配置实例：
 * 
 * <pre>
 *     &lt;filter&gt;
 *         &lt;filter-name&gt;p3pHeaderFilter&lt;/filter-name&gt;
 *         &lt;filter-class>com.guomi.meazza.filter.P3PHeaderFilter&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;enable&lt;/param-name&gt;
 *             &lt;param-value&gt;true&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;pattern&lt;/param-name&gt;
 *             &lt;param-value&gt;/(index.htm|login(.htm)?|logout)?&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     &lt;/filter&gt;
 * 
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;p3pHeaderFilter&lt;/filter-name&gt;
 *         &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author akuma
 */
public class P3PHeaderFilter implements Filter {

    private static final String P3P_HEADER_NAME = "P3P";
    private static final String P3P_HEADER_VALUE = "CP=\"NOI CURa ADMa DEVa TAIa OUR BUS IND UNI COM NAV INT\"";

    private static final Logger logger = LoggerFactory.getLogger(P3PHeaderFilter.class);

    private boolean isEnable = true;
    private Pattern uriPattern;
    private String headerValue = P3P_HEADER_VALUE;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String enableValue = filterConfig.getInitParameter("enable");
        logger.debug("Read enable param: {}", enableValue);
        if (!StringUtils.isEmpty(enableValue)) {
            isEnable = BooleanUtils.toBoolean(enableValue);
        }

        String patternRegex = filterConfig.getInitParameter("pattern");
        logger.debug("Read pattern param: {}", patternRegex);
        if (!StringUtils.isEmpty(patternRegex)) {
            uriPattern = Pattern.compile(filterConfig.getServletContext().getContextPath() + patternRegex);
        }

        String p3pValue = filterConfig.getInitParameter("p3pValue");
        logger.debug("Read p3pValue param: {}", p3pValue);
        if (!StringUtils.isEmpty(p3pValue)) {
            headerValue = p3pValue;
        }

        logger.info("{} initialized with {}", getClass(), isEnable ? "enable" : "disable");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (isEnable && uriPattern != null && request instanceof ServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) (request);
            String uri = httpRequest.getRequestURI();
            if (uriPattern.matcher(uri).matches()) {
                logger.info("Request path[{}] matched patter[{}]", uri, uriPattern.pattern());
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpResponse = (HttpServletResponse) (response);
                    httpResponse.setHeader(P3P_HEADER_NAME, headerValue);
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
