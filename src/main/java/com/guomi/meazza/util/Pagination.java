/*
 * @(#)Pagination.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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

    private List<Pair<String, Boolean>> sorts = new ArrayList<>(); // 排序方式
    private boolean pageCountEnable = true; // 是否启用分页的页数，不启用的情况下不需要显示总共有多少页

    @Deprecated
    private String orderBy;
    @Deprecated
    private boolean desc;

    /**
     * 构造方法。
     */
    public Pagination() {
    }

    /**
     * 构造方法。
     */
    public Pagination(Integer pageSize) {
        this(0, pageSize, true);
    }

    /**
     * 构造方法。
     *
     * @param pageSize
     *            每页多少行
     * @param pageCountEnable
     *            是否启用页数
     */
    public Pagination(Integer pageSize, boolean pageCountEnable) {
        this(0, pageSize, pageCountEnable);
    }

    /**
     * 构造方法。
     *
     * @param pageNum
     *            当前是第几页
     * @param pageSize
     *            每页多少行
     * @param pageCountEnable
     *            是否启用页数
     */
    public Pagination(Integer pageNum, Integer pageSize, boolean pageCountEnable) {
        this.pageNum = pageNum == null ? 0 : pageNum;
        this.pageSize = pageSize == null ? 10 : pageSize;
        this.pageCountEnable = pageCountEnable;
    }

    /**
     * 初始化，计算出一共有多少页和当前起始记录序号。
     */
    public void initialize() {
        if (pageCountEnable) {
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
        } else {
            currentRowNum = (pageNum - 1) * pageSize + 1;
        }

        if (logger.isInfoEnabled()) {
            logger.info("page: {}/{}, row: {}/{}, size: {}, pageCountEnable: {}", new Object[] { pageNum, pageCount,
                    currentRowNum, rowCount, pageSize, pageCountEnable });
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
     * @deprecated
     */
    @Deprecated
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置排序字段。
     *
     * @deprecated
     */
    @Deprecated
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 判断是否降序排列。
     *
     * @deprecated
     */
    @Deprecated
    public boolean isDesc() {
        return desc;
    }

    /**
     * 设置是否降序排列。
     *
     * @deprecated
     */
    @Deprecated
    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    /**
     * 获取排序方式，支持多个字段组合排序。
     */
    public List<Pair<String, Boolean>> getSorts() {
        return sorts;
    }

    /**
     * 设置排序方式，支持多个字段组合排序。
     */
    public void setSorts(List<Pair<String, Boolean>> sorts) {
        this.sorts = sorts;
    }

    /**
     * 添加排序方式。
     */
    public void addSort(String field, boolean isDesc) {
        sorts.add(Pair.of(field, isDesc));
    }

    /**
     * 获取第一个排序字段名称。
     */
    public String getSortField() {
        return sorts.isEmpty() ? null : sorts.get(0).getLeft();
    }

    /**
     * 获取第一个排序字段的排序方式。
     */
    public boolean isSortDesc() {
        return sorts.isEmpty() ? false : sorts.get(0).getRight();
    }

    /**
     * 获取第一个排序字段信息，例如：name:desc
     */
    public String getSort() {
        if (sorts.isEmpty()) {
            return StringUtils.EMPTY;
        }

        Pair<String, Boolean> sort = sorts.get(0);
        String field = sort.getLeft();
        String direction = sort.getRight() ? "desc" : "asc";
        return field + ":" + direction;
    }

    /**
     * 设置排序字段。格式：field:desc/asc，例如：name:desc、age:asc
     */
    public void setSort(String sort) {
        if (StringUtils.isBlank(sort)) {
            return;
        }

        if (!sort.contains(":")) {
            addSort(sort, true);
            return;
        }

        String[] strs = sort.split(":");
        if (strs.length != 2) {
            addSort(sort, true);
            return;
        }

        String field = strs[0];
        boolean isDesc = "desc".equalsIgnoreCase(strs[1]);
        addSort(field, isDesc);
    }

    /**
     * 判断是否启用页数。
     *
     * @return true/false
     */
    public boolean isPageCountEnable() {
        return pageCountEnable;
    }

    /**
     * 设置是否启用页数。
     *
     * @param pageCountEnable
     *            是否使用页数
     */
    public void setPageCountEnable(boolean pageCountEnable) {
        this.pageCountEnable = pageCountEnable;
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
        sb.append(", pageCountEnable: " + pageCountEnable + ")");
        return sb.toString();
    }

}
