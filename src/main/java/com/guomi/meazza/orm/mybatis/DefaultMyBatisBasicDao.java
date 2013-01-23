/* 
 * @(#)DefaultMyBatisBasicDao.java    Created on 2012-8-1
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.orm.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.guomi.meazza.util.Pagination;

/**
 * @author akuma
 */
public class DefaultMyBatisBasicDao<T> extends SqlSessionDaoSupport implements MyBatisBasicDao<T> {

    @Override
    public <PK> T find(PK id) {
        return getSqlSession().selectOne("find", id);
    }

    @Override
    public List<T> findAll() {
        return getSqlSession().selectList("findAll");
    }

    @Override
    public List<T> findByEntity(T entity) {
        return getSqlSession().selectList("findByEntity", entity);
    }

    @Override
    public List<T> findByEntityWithPage(T entity, Pagination page) {
        Map<String, Object> params = new HashMap<>();
        params.put("param1", entity);
        params.put("param2", page);
        return getSqlSession().selectList("findByEntityWithPage", params);
    }

    @Override
    public List<T> findByParam(Object param) {
        return getSqlSession().selectList("findByParam", param);
    }

    @Override
    public List<T> findByParamWithPage(Object param, Pagination page) {
        Map<String, Object> params = new HashMap<>();
        params.put("param1", param);
        params.put("param2", page);
        return getSqlSession().selectList("findByParamWithPage", params);
    }

    @SuppressWarnings("unchecked")
    @Override
    @MapKey("id")
    public <PK> Map<PK, T> findMap(PK... ids) {
        return getSqlSession().selectMap("findMap", ids, "id");
    }

    @Override
    public void insert(T entity) {
        getSqlSession().insert("insert", entity);
    }

    @Override
    public void update(T entity) {
        getSqlSession().update("update", entity);
    }

    @Override
    public void updateIfPossible(T entity) {
        getSqlSession().update("updateIfPossible", entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <PK> void delete(PK... ids) {
        getSqlSession().delete("delete", ids);
    }

}
