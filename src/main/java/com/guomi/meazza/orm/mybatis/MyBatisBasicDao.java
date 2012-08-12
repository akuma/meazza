/* 
 * @(#)MyBatisBasicDao.java    Created on 2012-8-1
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.orm.mybatis;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.guomi.meazza.dao.CrudDao;

/**
 * @author akuma
 */
public class MyBatisBasicDao<E, PK> extends SqlSessionDaoSupport implements CrudDao<E, PK> {

    @Override
    public E find(PK id) {
        return getSqlSession().selectOne("find", id);
    }

    @Override
    public List<E> findAll() {
        return getSqlSession().selectList("findAll");
    }

    @Override
    public void insert(E entity) {
        getSqlSession().insert("insert", entity);
    }

    @Override
    public void update(E entity) {
        getSqlSession().update("update", entity);
    }

    @Override
    public int delete(PK id) {
        return getSqlSession().delete("delete", id);
    }

}
