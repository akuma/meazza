/*
 * @(#)AbstractController.java    Created on 2012-6-6
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.spring.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guomi.meazza.util.StringUtils;

/**
 * Spring {@code Controller} 抽象类，可供业务 {@code Controller} 继承。 提供了添加错误信息、提示信息的相关方法。
 *
 * @author akuma
 */
public abstract class AbstractController implements ValidationSupport {

    public static final String ACTION_ERRORS = "actionErrors";
    public static final String ACTION_MESSAGES = "actionMessages";
    public static final String FIELD_ERRORS = "fieldErrors";

    private static final String REGEX_MOBILE_AGENT1 = "(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|"
            + "blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|"
            + "mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|"
            + "series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*";

    private static final String REGEX_MOBILE_AGENT2 = "(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|"
            + "abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|"
            + "au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|"
            + "bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|"
            + "craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|"
            + "em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|"
            + "gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|"
            + "hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|"
            + "iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|"
            + "keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|"
            + "\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|"
            + "mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|"
            + "n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|"
            + "nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|"
            + "phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|"
            + "\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|"
            + "sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|"
            + "sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|"
            + "t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|"
            + "tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|"
            + "voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|"
            + "wonu|x700|yas\\-|your|zeto|zte\\-";

    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志对象

    @Resource
    protected MessageSource messageSource;

    @Override
    public Collection<String> getActionMessages(Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<String>(0);
        }

        return Collections.unmodifiableCollection(messages);
    }

    @Override
    public Collection<String> getActionErrors(Model model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>(0);
        }

        return Collections.unmodifiableCollection(errors);
    }

    @Override
    public Map<String, Collection<String>> getFieldErrors(Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Collection<String>> errorsMap = (Map<String, Collection<String>>) model.asMap().get(FIELD_ERRORS);
        if (CollectionUtils.isEmpty(errorsMap)) {
            errorsMap = new LinkedHashMap<String, Collection<String>>(0);
        }

        return Collections.unmodifiableMap(errorsMap);
    }

    @Override
    public void addActionMessage(String aMessage, Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<String>();
            model.addAttribute(ACTION_MESSAGES, messages);
        }

        messages.add(aMessage);
    }

    public void addActionMessage(String aMessage, RedirectAttributes model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.asMap().get(ACTION_MESSAGES);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<String>();
            model.addFlashAttribute(ACTION_MESSAGES, messages);
        }

        messages.add(aMessage);
    }

    @Override
    public void addActionError(String anErrorMessage, Model model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>();
            model.addAttribute(ACTION_ERRORS, errors);
        }

        errors.add(anErrorMessage);
    }

    public void addActionError(String anErrorMessage, RedirectAttributes model) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) model.asMap().get(ACTION_ERRORS);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>();
            model.addFlashAttribute(ACTION_ERRORS, errors);
        }

        errors.add(anErrorMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errorsMap = (Map<String, List<String>>) model.asMap().get(FIELD_ERRORS);
        if (CollectionUtils.isEmpty(errorsMap)) {
            errorsMap = new LinkedHashMap<String, List<String>>();
            model.addAttribute(FIELD_ERRORS, errorsMap);
        }

        List<String> errors = errorsMap.get(fieldName);
        if (CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<String>();
            errorsMap.put(fieldName, errors);
        }

        errors.add(errorMessage);
    }

    @Override
    public boolean hasActionMessages(Model model) {
        return !getActionMessages(model).isEmpty();
    }

    @Override
    public boolean hasActionErrors(Model model) {
        return !getActionErrors(model).isEmpty();
    }

    @Override
    public boolean hasFieldErrors(Model model) {
        return !getFieldErrors(model).isEmpty();
    }

    @Override
    public boolean hasErrors(Model model) {
        return hasActionErrors(model) || hasFieldErrors(model);
    }

    /**
     * 从 Spring 的 <code>org.springframework.ui.Model</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     *
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Model model) {
        ResponseMessage message = new ResponseMessage();
        message.setActionMessages(getActionMessages(model));
        message.setActionErrors(getActionErrors(model));
        message.setFieldErrors(getFieldErrors(model));
        return message;
    }

    /**
     * 从 Spring 的 <code>org.springframework.validation.Errors</code> 对象中获取错误消息，封装成响应对象，用于页面错误提示。
     *
     * @return 响应消息，一般最终以 JSON 格式返回给客户端
     */
    public ResponseMessage getResponseMessage(Errors errors) {
        ResponseMessage message = new ResponseMessage();

        // 设置全局错误信息
        Collection<String> actionErrors = new ArrayList<String>();
        List<ObjectError> springActionErrors = errors.getGlobalErrors();
        for (ObjectError error : springActionErrors) {
            actionErrors.add(messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(),
                    null));
        }
        message.setActionErrors(actionErrors);

        // 设置字段错误信息
        Map<String, Collection<String>> fieldErrorsMap = new LinkedHashMap<String, Collection<String>>();
        List<FieldError> springFieldErrors = errors.getFieldErrors();
        for (FieldError error : springFieldErrors) {
            Collection<String> fieldErrors = fieldErrorsMap.get(error.getField());
            if (fieldErrors == null) {
                fieldErrors = new ArrayList<String>();
                fieldErrorsMap.put(error.getField(), fieldErrors);
            }
            String fieldError = messageSource.getMessage(error.getCode(), error.getArguments(),
                    error.getDefaultMessage(), null);
            fieldErrors.add(fieldError);
        }
        message.setFieldErrors(fieldErrorsMap);

        return message;
    }

    /**
     * 返回 spring mvc 重定向结果，例如：redirect:login。
     */
    protected String redirectTo(String url) {
        logger.debug("Redirect to {}", url);
        return "redirect:" + url;
    }

    /**
     * 判断浏览器是否是移动版本。
     */
    protected boolean isMobileAgent(WebRequest request) {
        String ua = request.getHeader("User-Agent").toLowerCase();
        if (StringUtils.isBlank(ua)) {
            return false;
        }

        return ua.matches(REGEX_MOBILE_AGENT1) || ua.substring(0, 4).matches(REGEX_MOBILE_AGENT2);
    }

}
