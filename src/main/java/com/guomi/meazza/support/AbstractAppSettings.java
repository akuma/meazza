/* 
 * @(#)AbstractAppSettings.java    Created on 2013-5-27
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;

/**
 * 封装了比较通用的系统配置参数的抽象类。
 * 
 * @author akuma
 */
public abstract class AbstractAppSettings implements Serializable {

    private static final long serialVersionUID = -8794135442659506482L;

    @Value("#{appProperties['app.environment']}")
    private String environment;

    @Value("#{appProperties['assets.path']}")
    private String assetsPath;
    @Value("#{appProperties['assets.global.css']}")
    private String assetsGlobalCss;
    @Value("#{appProperties['assets.global.js']}")
    private String assetsGlobalJs;

    @Value("#{appProperties['page.tracker.enable']}")
    private boolean pageTrackerEnable;
    @Value("#{appProperties['page.tracker.id']}")
    private String pageTrackerId;
    @Value("#{appProperties['page.tracker.domain']}")
    private String pageTrackerDomain;

    /**
     * 是否是开发环境。
     */
    public boolean isDev() {
        return "dev".equalsIgnoreCase(environment);
    }

    /**
     * 是否是测试环境。
     */
    public boolean isTest() {
        return "test".equalsIgnoreCase(environment);
    }

    /**
     * 是否是生产环境。
     */
    public boolean isProd() {
        return "prod".equalsIgnoreCase(environment);
    }

    /**
     * 获取资源文件路径前缀。例如：http://static.foo.bar/assets
     */
    public String getAssetsPath() {
        return assetsPath;
    }

    /**
     * 获取非开发环境下公共 css 文件名。一般是一个压缩和合并过的文件。
     */
    public String getAssetsGlobalCss() {
        return assetsGlobalCss;
    }

    /**
     * 获取非开发环境下公共 js 文件名。一般是一个压缩和合并过的文件。
     */
    public String getAssetsGlobalJs() {
        return assetsGlobalJs;
    }

    /**
     * 判断是否启用网页访问分析脚本（比如 google analytics）。
     */
    public boolean isPageTrackerEnable() {
        return pageTrackerEnable;
    }

    /**
     * 获取应用在网页分析服务中分配的唯一标识。
     */
    public String getPageTrackerId() {
        return pageTrackerId;
    }

    /**
     * 获取应用在网页分析服务中的域名。
     */
    public String getPageTrackerDomain() {
        return pageTrackerDomain;
    }

}
