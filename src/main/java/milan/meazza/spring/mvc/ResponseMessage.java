/* 
 * @(#)ResponseMessage.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.spring.mvc;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller 操作的响应消息类，主要用在以 <code>ResponseBody</code> 方式返回结果的方法中。
 * 它包含了 Action 提示信息、Action 错误信息、字段错误信息以及其他响应数据。
 * 
 * @author akuma
 */
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 6725085407815077443L;

    private Collection<String> actionMessages;
    private Collection<String> actionErrors;
    private Map<String, Collection<String>> fieldErrors;
    private Map<String, Object> data = new HashMap<String, Object>(); // 其他响应数据

    public Collection<String> getActionErrors() {
        return actionErrors;
    }

    public void setActionErrors(Collection<String> actionErrors) {
        this.actionErrors = actionErrors;
    }

    public Collection<String> getActionMessages() {
        return actionMessages;
    }

    public void setActionMessages(Collection<String> actionMessages) {
        this.actionMessages = actionMessages;
    }

    public Map<String, Collection<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, Collection<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 添加需要返回给客户端的属性。
     * 
     * @param name
     *            属性名称
     * @param value
     *            属性值
     */
    public void addAttribute(String name, Object value) {
        data.put(name, value);
    }

    /**
     * 批量添加需要返回给客户端的属性。
     * 
     * @param data
     *            属性 map
     */
    public void addAttributes(Map<String, Object> data) {
        this.data.putAll(data);
    }

}
