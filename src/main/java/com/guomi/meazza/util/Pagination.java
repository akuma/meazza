/* 
 * @(#)Pagination.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于处理数据分页的工具类。
 * 
 * @author akuma
 */
public class Pagination {

    private static final Logger logger = LoggerFactory.getLogger(Pagination.class);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private String id = StringUtils.EMPTY; // 分页信息的标识，会作为参数的后缀，在多个pagination共存的时候有用

    private Integer pageNum = 0; // 当前是第几页
    private Integer pageCount = 0; // 一共有多少页
    private Integer rowCount = 0; // 一共有多少行
    private Integer pageSize = DEFAULT_PAGE_SIZE; // 每页有多少行，默认10行
    private Integer currentRowNum = 0; // 当前起始记录序号

    private String orderBy;
    private boolean desc;
    private boolean useCursor;

    /**
     * 构造方法。
     */
    public Pagination() {
    }

    /**
     * 构造方法。
     * 
     * @param pageSize
     *            每页多少行
     * @param useCursor
     *            是否使用游标
     */
    public Pagination(Integer pageSize, boolean useCursor) {
        this(0, pageSize, useCursor);
    }

    /**
     * 构造方法。
     * 
     * @param pageNum
     *            当前是第几页
     * @param pageSize
     *            每页多少行
     * @param useCursor
     *            是否使用游标
     */
    public Pagination(Integer pageNum, Integer pageSize, boolean useCursor) {
        this.pageNum = pageNum == null ? 0 : pageNum;
        this.pageSize = pageSize == null ? 10 : pageSize;
        this.useCursor = useCursor;
    }

    /**
     * 初始化，计算出一共有多少页和当前起始记录序号。
     */
    public void initialize() {
        // 得到总共页数
        if (rowCount % pageSize == 0) {
            pageCount = rowCount / pageSize;
        } else {
            pageCount = rowCount / pageSize + 1;
        }

        // 校验当前页参数
        if (pageNum > pageCount) {
            pageNum = pageCount;
        } else if (pageNum < 1) {
            pageNum = 1;
        }

        // 得到当前起始记录序号
        if (rowCount == 0) {
            currentRowNum = 0;
            pageNum = 0;
        } else {
            currentRowNum = (pageNum - 1) * pageSize + 1;
        }

        if (logger.isInfoEnabled()) {
            logger.info("page: {}/{}, row: {}/{}, size: {}, cursor: {}", new Object[] { pageNum, pageCount,
                    currentRowNum, rowCount, pageSize, useCursor });
        }
    }

    /**
     * 取得分页对象的id。
     * 
     * @return 分页对象的id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置分页对象的id。
     * 
     * @param id
     *            分页对象的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 取得当前是第几页。
     * 
     * @return 当前是第几页
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * 设置当前是第几页。
     * 
     * @param pageNum
     *            当前是第几页
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum == null ? 0 : pageNum;
    }

    /**
     * 取得每页有多少行。
     * 
     * @return 每页有多少行
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页多少行。
     * 
     * @param pageSize
     *            每页多少行
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }

    /**
     * 取得一共有多少页。
     * 
     * @return 一共有多少页
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * 取得当前起始记录序号。
     * 
     * @return 当前起始记录序号
     */
    public Integer getCurrentRowNum() {
        return currentRowNum;
    }

    /**
     * 取得一共有多少行。
     * 
     * @return 一共有多少行
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 设置一共有多少行。
     * 
     * @param rowCount
     *            一共有多少行
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount == null ? 0 : rowCount;
    }

    /**
     * 取得排序字段。
     * 
     * @return 排序字段
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置排序字段。
     * 
     * @param orderBy
     *            排序字段
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 判断是否降序排列。
     * 
     * @return 是true，否则false
     */
    public boolean isDesc() {
        return desc;
    }

    /**
     * 设置是否降序排列。
     * 
     * @param desc
     *            是否降序排列
     */
    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    /**
     * 判断是否使用游标。
     * 
     * @return 是true，否则false
     */
    public boolean isUseCursor() {
        return useCursor;
    }

    /**
     * 设置是否使用游标。
     * 
     * @param useCursor
     *            是否使用游标
     */
    public void setUseCursor(boolean useCursor) {
        this.useCursor = useCursor;
    }

    /**
     * 判断当前页是否是第一页。
     * 
     * @return true/false
     */
    public boolean isFirstPage() {
        return pageNum == 1;
    }

    /**
     * 判断当前页是否是最后一页。
     * 
     * @return true/false
     */
    public boolean isLastPage() {
        return pageNum == pageCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pagination(");
        sb.append("page: " + pageNum + "/" + pageCount);
        sb.append(", row: " + currentRowNum + "/" + rowCount);
        sb.append(", size: " + pageSize);
        sb.append(", cursor: " + useCursor + ")");
        return sb.toString();
    }

}
