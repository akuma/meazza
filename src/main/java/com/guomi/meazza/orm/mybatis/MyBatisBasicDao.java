/* 
 * @(#)MyBatisBasicMapper.java    Created on 2013-1-23
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.orm.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.guomi.meazza.util.Pagination;

/**
 * MyBatis 基础 DAO 接口。
 * 
 * @author akuma
 */
@MyBatisRepository
public interface MyBatisBasicDao<T> {

    <PK> T find(PK id);

    List<T> findAll();

    List<T> findByEntity(T entity);

    List<T> findByEntityWithPage(T entity, Pagination page);

    List<T> findByParam(Object param);

    List<T> findByParamWithPage(Object param, Pagination page);

    @SuppressWarnings("unchecked")
    @MapKey("id")
    <PK> Map<PK, T> findMap(PK... ids);

    void insert(T entity);

    void update(T entity);

    void updateIfPossible(T entity);

    @SuppressWarnings("unchecked")
    <PK> void delete(PK... ids);

}
