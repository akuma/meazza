/* 
 * @(#)CrudDao.java    Created on 2012-8-1
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.dao;

import java.util.List;

/**
 * CRUD 操作的 DAO 接口。
 * 
 * @author akuma
 */
public interface CrudDao<Entity, PK> {

    Entity find(PK id);

    List<Entity> findAll();

    void insert(Entity entity);

    void update(Entity entity);

    int delete(PK id);

}
