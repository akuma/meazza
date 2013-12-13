/*
 * @(#)AbstractAppSettings.java    Created on 2013-5-27
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * 封装了比较通用的系统配置参数的抽象类。
 * 
 * @author akuma
 */
public abstract class AbstractAppSettings implements Serializable {

    private static final long serialVersionUID = -8794135442659506482L;

    private static final String EXTENSION_CSS = "css";
    private static final String EXTENSION_JS = "js";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("#{appProperties['app.environment']}")
    private String environment;

    @Value("#{appProperties['assets.path']}")
    private String assetsPath;

    @Value("#{appProperties['page.tracker.enable']}")
    private Boolean pageTrackerEnable;
    @Value("#{appProperties['page.tracker.id']}")
    private String pageTrackerId;
    @Value("#{appProperties['page.tracker.domain']}")
    private String pageTrackerDomain;

    private Map<String, String> assetsVersion = new HashMap<>(); // 资源版本映射表
    private ScheduledExecutorService executor;

    @PostConstruct
    public void init() {
        final String assetsVersionUrl = getAssetsVersionUrl();
        if (assetsVersionUrl == null) {
            return;
        }

        initAssetsVersion(assetsVersionUrl);

        // 如果是测试环境，则开启定时读取 assets version 文件的任务
        if (isTest()) {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    initAssetsVersion(assetsVersionUrl);
                }
            }, 60, 10, TimeUnit.SECONDS);
        }
    }

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
     * 判断是否启用网页访问分析脚本（比如 google analytics）。
     */
    public boolean isPageTrackerEnable() {
        return BooleanUtils.isTrue(pageTrackerEnable);
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

    /**
     * 从资源版本映射表中获取带有版本号的资源路径（相对路径），如果获取不到，则返回原始路径。
     * <p>
     * 例如：js/global.min.js -> js/global.min.78b76e3e.js
     */
    public String versionedAsset(String originAssetPath) {
        return versionedAsset(originAssetPath, null);
    }

    /**
     * 从资源版本映射表中获取带有版本号的资源路径（相对路径），如果获取不到，则返回带有 css/、js/ 前缀的原始路径。
     * <p>
     * 例如：js/global.min.js -> js/global.min.78b76e3e.js
     */
    private String versionedAsset(String originAsset, String preVersionedAsset) {
        if (StringUtils.isEmpty(originAsset)) {
            return StringUtils.EMPTY;
        }

        // 如果资源以 / 开头，则删除该字符
        if (originAsset.startsWith("/")) {
            originAsset = originAsset.substring(1);
        }

        // 获取资源后缀名
        String extension = FilenameUtils.getExtension(originAsset);

        // 如果资源不是 http、https 开头的地址，则尝试给资源添加 css/、js/ 这样的前缀目录
        if (!originAsset.startsWith("http://") && !originAsset.startsWith("https://")) {

            String prefix = StringUtils.EMPTY;
            if (EXTENSION_CSS.equalsIgnoreCase(extension)) {
                prefix = "css/";
            } else if (EXTENSION_JS.equalsIgnoreCase(extension)) {
                prefix = "js/";
            }

            if (!originAsset.startsWith(prefix)) {
                originAsset = prefix + originAsset;
            }
        }

        // 获取带版本号的资源
        String versionedAsset = getVersionedAsset(originAsset, preVersionedAsset);
        if (!StringUtils.isEmpty(versionedAsset)) {
            return versionedAsset;
        }

        // 如果没有获取到，则再尝试根据压缩版本的名称去获取资源（e.g. xxx.min.js）
        if (!originAsset.contains(".min.")) {
            String path = FilenameUtils.getPath(originAsset);
            String basename = FilenameUtils.getBaseName(originAsset);
            String minAssetPath = path + basename + ".min." + extension;
            versionedAsset = getVersionedAsset(minAssetPath, preVersionedAsset);
        }

        return StringUtils.isEmpty(versionedAsset) ? originAsset : versionedAsset;
    }

    private String getVersionedAsset(String originAsset, String preVersionedAsset) {
        String versionedAsset = assetsVersion.get(originAsset);
        if (!StringUtils.isEmpty(versionedAsset) && !versionedAsset.equals(preVersionedAsset)) {
            logger.trace("Got versioned asset: {} -> {}", originAsset, versionedAsset);
        }
        return versionedAsset;
    }

    /**
     * 初始化资源版本映射表。
     */
    private void initAssetsVersion(String versionUrl) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream version = new URL(versionUrl).openStream();
            assetsVersion = mapper.reader(Map.class).readValue(version);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                logger.info("Assets version file not found: {}", versionUrl);
            } else {
                logger.error("Read assets version file error", e);
            }
            return;
        }
    }

    private String getAssetsVersionUrl() {
        return StringUtils.isBlank(assetsPath) ? null : assetsPath + "/version.json";
    }

}
