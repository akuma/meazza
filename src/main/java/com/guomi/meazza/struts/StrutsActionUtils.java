/* 
 * @(#)StrutsActionUtils.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.struts;

import java.util.Map;

import org.apache.struts2.json.JSONResult;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;

/**
 * 和 Struts 的 Action 相关的工具类。
 * 
 * @author akuma
 */
public abstract class StrutsActionUtils {

    /**
     * 判断 Action 是否为采用 JSON 为输出方式 (ResultType) 的 Action。
     * 
     * @param invocation
     *            ActionInvocation 对象
     * @return true/false
     */
    public static boolean isJsonAction(ActionInvocation invocation) {
        boolean isJsonAction = false;
        Map<String, ResultConfig> results = invocation.getProxy().getConfig().getResults();
        if (!results.isEmpty()) {
            ResultConfig resultConf = results.values().iterator().next();
            if (JSONResult.class.getName().equals(resultConf.getClassName())) {
                isJsonAction = true;
            }
        }
        return isJsonAction;
    }

}
