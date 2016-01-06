/*
 * @(#)AbstractAppSettings.java    Created on 2013-5-27
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 封装了比较通用的系统配置参数的抽象类。
 *
 * @author akuma
 */
public abstract class AbstractAppSettings implements Serializable {

    private static final long serialVersionUID = -8794135442659506482L;

    private static final String EXTENSION_JS = "js";
    private static final String EXTENSION_CSS = "css";
    private static final String ASSETS_MIN_FLAG = ".min.";
    private static final String ASSETS_VERION_FILENAME = "version.json";

    private static ObjectMapper MAPPER = new ObjectMapper();

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("#{appProperties['app.environment']}")
    private String environment;

    @Value("#{appProperties['assets.path']}")
    private String assetsPath;
    @Value("#{appProperties['assets.dist']}")
    private String assetsDist;

    @Value("#{appProperties['page.tracker.enable']}")
    private Boolean pageTrackerEnable;
    @Value("#{appProperties['page.tracker.id']}")
    private String pageTrackerId;
    @Value("#{appProperties['page.tracker.domain']}")
    private String pageTrackerDomain;

    // 资源前缀地址列表
    private List<String> assetsPaths = new ArrayList<>();

    // 资源版本映射表（js、css、img）
    private Map<String, Map<String, String>> assetsVersions = new HashMap<>();

    // 定时读取 assets verion 文件的服务
    private ScheduledExecutorService executor;

    @PostConstruct
    public void init() {
        if (!StringUtils.isEmpty(assetsPath)) {
            String[] paths = assetsPath.split(",");
            for (String path : paths) {
                if (!StringUtils.isEmpty(path)) {
                    assetsPaths.add(path);
                }
            }
        }

        String path = StringUtils.isEmpty(assetsDist) ? assetsPath : assetsDist;
        if (StringUtils.isEmpty(path)) {
            return;
        }

        final String assetsVersionPath = path;
        initAssetsVersion(assetsVersionPath);

        // 如果是测试环境，则开启定时读取 assets version 文件的任务
        if (isTest()) {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(() -> initAssetsVersion(assetsVersionPath), 60, 10, TimeUnit.SECONDS);
        }
    }

    @PreDestroy
    public void destory() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
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
     * 获取资源文件路径前缀，默认返回第一个路径前缀，即 <code>group == 1</code>。例如：http://static.foo.bar/assets
     */
    public String getAssetsPath() {
        return getAssetsPathByGroup(1);
    }

    /**
     * 根据 <code>group</code> 获取资源文件路径前缀。例如：http://static.foo.bar/assets
     */
    public String getAssetsPathByGroup(int group) {
        if (assetsPaths.isEmpty()) {
            return null;
        }

        if (group <= 0 || group > assetsPaths.size()) {
            group = 1;
        }
        return assetsPaths.get(group - 1);
    }

    /**
     * 获取资源文件发布目录。例如：/opt/assets/dist
     */
    public String getAssetsDist() {
        return assetsDist;
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
     * 从默认的第一组资源版本映射表中获取带有版本号的资源路径（相对路径），如果获取不到，则返回原始路径。
     * <p>
     * 例如：js/global.min.js -> js/global.min.78b76e3e.js
     */
    public String versionedAsset(String originAssetPath) {
        return versionedAssetByGroup(originAssetPath, 0);
    }

    /**
     * 从 <code>group</code> 指定的资源版本映射表中获取带有版本号的资源路径（相对路径），如果获取不到，则返回原始路径。<br>
     * 默认 <code>group == 1</code>，即在第一组资源里获取。
     * <p>
     * 例如：js/global.min.js -> js/global.min.78b76e3e.js
     */
    public String versionedAssetByGroup(String originAssetPath, int group) {
        return versionedAsset(originAssetPath, null, group);
    }

    /**
     * 从资源版本映射表中获取带有版本号的资源路径（相对路径），如果获取不到，则返回带有 css/、js/ 前缀的原始路径。
     * <p>
     * 例如：js/global.min.js -> js/global.min.78b76e3e.js
     */
    private String versionedAsset(String originAsset, String preVersionedAsset, int group) {
        if (StringUtils.isEmpty(originAsset)) {
            return StringUtils.EMPTY;
        }

        // 如果资源以 / 开头，则删除该字符
        if (originAsset.startsWith("/")) {
            originAsset = originAsset.substring(1);
        }

        // 获取资源后缀名
        String extension = FilenameUtils.getExtension(originAsset);

        // 如果资源不以 http/https 开头，则尝试给资源添加 css/、js/ 这样的前缀目录
        if (!originAsset.startsWith("http://") && !originAsset.startsWith("https://")) {

            String prefix = StringUtils.EMPTY;
            if (EXTENSION_JS.equalsIgnoreCase(extension)) {
                prefix = EXTENSION_JS + "/";
            } else if (EXTENSION_CSS.equalsIgnoreCase(extension)) {
                prefix = EXTENSION_CSS + "/";
            }

            if (!originAsset.startsWith(prefix)) {
                originAsset = prefix + originAsset;
            }
        }

        // 获取带版本号的资源
        String versionedAsset = getVersionedAsset(originAsset, preVersionedAsset, group);
        if (!StringUtils.isEmpty(versionedAsset)) {
            return versionedAsset;
        }

        // 如果没有获取到，则再尝试根据压缩版本的名称去获取资源（e.g. xxx.min.js）
        if (!originAsset.contains(ASSETS_MIN_FLAG)) {
            String path = FilenameUtils.getPath(originAsset);
            String basename = FilenameUtils.getBaseName(originAsset);
            String minAssetPath = path + basename + ASSETS_MIN_FLAG + extension;
            versionedAsset = getVersionedAsset(minAssetPath, preVersionedAsset, group);
        }

        return StringUtils.isEmpty(versionedAsset) ? originAsset : versionedAsset;
    }

    /**
     * 根据原始 assets 路径获取带版本号的 assets 路径。
     *
     * @param originAsset
     *            原始 assets 路径
     * @param preVersionedAsset
     *            上一次获取到的带版本号的 assets 路径
     * @return 带版本号的 assets 路径，如果没有就返回 null
     */
    private String getVersionedAsset(String originAsset, String preVersionedAsset, int group) {
        if (group <= 0 || group > assetsPaths.size()) {
            group = 1;
        }

        Map<String, String> assetsVersion = assetsVersions.get(String.valueOf(group));
        if (assetsVersion == null) {
            return null;
        }

        String versionedAsset = assetsVersion.get(originAsset);
        if (!StringUtils.isEmpty(versionedAsset) && !versionedAsset.equals(preVersionedAsset)) {
            logger.trace("Got versioned asset: {} -> {}", originAsset, versionedAsset);
        }
        return versionedAsset;
    }

    /**
     * 初始化资源版本映射表。
     */
    private void initAssetsVersion(String assetsPath) {
        if (StringUtils.isBlank(assetsPath)) {
            return;
        }

        String[] assetsPaths = assetsPath.split(",");
        if (ArrayUtils.isEmpty(assetsPaths)) {
            return;
        }

        // 1 -> map1, 2 -> map2...
        for (int i = 0; i < assetsPaths.length; i++) {
            String versionPath = getAssetsVersionPath(assetsPaths[i]);

            try {
                Map<String, String> assetsVersion = MAPPER.readerFor(Map.class).readValue(new URL(versionPath));
                assetsVersions.put(String.valueOf(i + 1), assetsVersion);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    logger.info("Assets version file not found: {}", versionPath);
                } else {
                    logger.error("Read assets version file error", e);
                }
            }
        }
    }

    private String getAssetsVersionPath(String assetsPath) {
        return StringUtils.isBlank(assetsPath) ? null : assetsPath + "/" + ASSETS_VERION_FILENAME;
    }

}
