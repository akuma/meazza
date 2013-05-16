/* 
 * @(#)ParseResult.java    Created on 2013-5-16
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.util.excel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析 XLS 文件所得的结果集类。
 * 
 * @param <T>
 *            结果集列表中的实体类类型
 * @author akuma
 */
public class ParseResult<T> {

    private Map<String, List<T>> recordListMap;
    private Map<String, List<Map<String, Object>>> recordMapListMap;

    private List<String> errorList;
    private List<String> messageList;

    public ParseResult() {
        recordListMap = new LinkedHashMap<String, List<T>>();
        recordMapListMap = new LinkedHashMap<String, List<Map<String, Object>>>();
        messageList = new ArrayList<String>();
        errorList = new ArrayList<String>();
    }

    public void addError(String error) {
        errorList.add(error);
    }

    public void addMessage(String message) {
        messageList.add(message);
    }

    /**
     * 添加工作表的记录值对象列表.
     * 
     * @param sheetName
     *            工作表名称
     * @param recordList
     *            记录值对象列表
     */
    public void addRecordList(String sheetName, List<T> recordList) {
        recordListMap.put(sheetName, recordList);
    }

    public void addRecordMapList(String sheetName, List<Map<String, Object>> recordMapList) {
        recordMapListMap.put(sheetName, recordMapList);
    }

    public String[] getAllErrors() {
        return errorList.toArray(new String[errorList.size()]);
    }

    public String[] getAllMessages() {
        return messageList.toArray(new String[messageList.size()]);
    }

    public List<T> getAllRecordList() {
        List<T> allRecordList = new ArrayList<T>();

        Collection<List<T>> c = recordListMap.values();
        for (Iterator<List<T>> iter = c.iterator(); iter.hasNext();) {
            List<T> recordList = iter.next();
            allRecordList.addAll(recordList);
        }

        return allRecordList;
    }

    public List<Map<String, Object>> getAllRecordMapList() {
        List<Map<String, Object>> allRecordMapList = new ArrayList<Map<String, Object>>();

        Collection<List<Map<String, Object>>> c = recordMapListMap.values();
        for (Iterator<List<Map<String, Object>>> iter = c.iterator(); iter.hasNext();) {
            List<Map<String, Object>> recordMapList = iter.next();
            allRecordMapList.addAll(recordMapList);
        }

        return allRecordMapList;
    }

    /**
     * 获取所有工作表名称。
     */
    public String[] getAllSheetNames() {
        String[] sheetNames = recordListMap.keySet().toArray(new String[recordListMap.size()]);
        if (sheetNames.length == 0) {
            sheetNames = recordMapListMap.keySet().toArray(new String[recordMapListMap.size()]);
        }

        return sheetNames;
    }

    /**
     * 获取工作表中的记录值对象列表。
     * 
     * @param sheetName
     *            工作表名称
     */
    public List<T> getRecordList(String sheetName) {
        return recordListMap.get(sheetName) == null ? new ArrayList<T>() : recordListMap.get(sheetName);
    }

    public List<Map<String, Object>> getRecordMapList(String sheetName) {
        return recordMapListMap.get(sheetName) == null ? new ArrayList<Map<String, Object>>() : recordMapListMap
                .get(sheetName);
    }

    public boolean hasErrors() {
        return !errorList.isEmpty();
    }

    public boolean hasMessages() {
        return !messageList.isEmpty();
    }

    public boolean hasRecordList() {
        return !recordListMap.isEmpty();
    }

    public boolean hasRecordMapList() {
        return !recordMapListMap.isEmpty();
    }

    public void removeError(String error) {
        errorList.remove(error);
    }

    public void removeMessage(String message) {
        messageList.remove(message);
    }

    public void removeRecordList(String sheetName) {
        recordListMap.remove(sheetName);
    }

    public void removeRecordMapList(String sheetName) {
        recordMapListMap.remove(sheetName);
    }

}
