/*
 * @(#)FreeMarkerConfigController.java    Created on 2013-6-6
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import javax.annotation.Resource;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * 可以对 FreeMarker 进行一些初始化配置的 Controller。
 *
 * @author akuma
 */
public class FreeMarkerConfigController {

    private BeansWrapper beansWrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build();

    @Resource
    protected FreeMarkerConfigurer freeMarkerConfigurer;

    public void addVariable(String name, Object value) throws TemplateModelException {
        freeMarkerConfigurer.getConfiguration().setSharedVariable(name, value);
    }

    public void addEnumModel(Class<?> clazz) throws TemplateModelException {
        TemplateHashModel enumModels = beansWrapper.getEnumModels();
        TemplateHashModel enums = (TemplateHashModel) enumModels.get(clazz.getName());
        if (enums != null) {
            freeMarkerConfigurer.getConfiguration().setSharedVariable(clazz.getSimpleName(), enums);
        }
    }

    public void addStaticModel(Class<?> clazz) throws TemplateModelException {
        TemplateHashModel staticModels = beansWrapper.getStaticModels();
        TemplateHashModel statics = (TemplateHashModel) staticModels.get(clazz.getName());
        if (statics != null) {
            freeMarkerConfigurer.getConfiguration().setSharedVariable(clazz.getSimpleName(), statics);
        }
    }

}
