/* 
 * @(#)ValidationSupport.java    Created on 2012-6-5
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.spring.mvc;

import java.util.Collection;
import java.util.Map;

import org.springframework.ui.Model;

/**
 * <code>ValidationSupport</code> 对错误信息和提示信息的显示提供了支持，支持全局和字段两种消息。
 * 
 * @author akuma
 */
public interface ValidationSupport {

    // /**
    // * Set the Collection of Action-level String messages (not errors).
    // *
    // * @param messages
    // * Collection of String messages (not errors).
    // */
    // void setActionMessages(Collection<String> messages);

    /**
     * Get the Collection of Action-level messages for this action. Messages should not be added directly here, as
     * implementations are free to return a new Collection or an Unmodifiable Collection.
     * 
     * @param model
     *            Spring Model Object
     * @return Collection of String messages
     */
    Collection<String> getActionMessages(Model model);

    // /**
    // * Set the Collection of Action-level String error messages.
    // *
    // * @param errorMessages
    // * Collection of String error messages
    // */
    // void setActionErrors(Collection<String> errorMessages);

    /**
     * Get the Collection of Action-level error messages for this action. Error messages should not be added directly
     * here, as implementations are free to return a new Collection or an Unmodifiable Collection.
     * 
     * @param model
     *            Spring Model Object
     * @return Collection of String error messages
     */
    Collection<String> getActionErrors(Model model);

    // /**
    // * Set the field error map of fieldname (String) to Collection of String error messages.
    // *
    // * @param errorMap
    // * field error map
    // */
    // void setFieldErrors(Map<String, List<String>> errorMap);

    /**
     * Get the field specific errors associated with this action. Error messages should not be added directly here, as
     * implementations are free to return a new Collection or an Unmodifiable Collection.
     * 
     * @param model
     *            Spring Model Object
     * @return Map with errors mapped from fieldname (String) to Collection of String error messages
     */
    Map<String, Collection<String>> getFieldErrors(Model model);

    /**
     * Add an Action-level message to this Action.
     * 
     * @param aMessage
     *            the message
     * @param model
     *            Spring Model Object
     */
    void addActionMessage(String aMessage, Model model);

    /**
     * Add an Action-level error message to this Action.
     * 
     * @param anErrorMessage
     *            the error message
     * @param model
     *            Spring Model Object
     */
    void addActionError(String anErrorMessage, Model model);

    /**
     * Add an error message for a given field.
     * 
     * @param fieldName
     *            name of field
     * @param errorMessage
     *            the error message
     * @param model
     *            Spring Model Object
     */
    void addFieldError(String fieldName, String errorMessage, Model model);

    /**
     * Checks whether there are any Action-level messages.
     * 
     * @param model
     *            Spring Model Object
     * @return true if any Action-level messages have been registered
     */
    boolean hasActionMessages(Model model);

    /**
     * Check whether there are any Action-level error messages.
     * 
     * @param model
     *            Spring Model Object
     * @return true if any Action-level error messages have been registered
     */
    boolean hasActionErrors(Model model);

    /**
     * Check whether there are any field errors associated with this action.
     * 
     * @param model
     *            Spring Model Object
     * @return whether there are any field errors
     */
    boolean hasFieldErrors(Model model);

    /**
     * Checks whether there are any action errors or field errors.
     * 
     * @param model
     *            Spring Model Object
     * @return <code>(hasActionErrors(Model) || hasFieldErrors(Model))</code>
     */
    boolean hasErrors(Model model);

}
