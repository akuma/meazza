/*
 * @(#)BasicMongoService.java    Created on 2014年2月27日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package com.guomi.meazza.mongo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.guomi.meazza.support.LongIdEntity;
import com.guomi.meazza.support.StringIdEntity;
import com.guomi.meazza.util.Pagination;
import com.mongodb.WriteResult;

/**
 * Mongo Service 基类。
 * 
 * @author akuma
 */
public abstract class BasicMongoService {

    protected Logger logger = LoggerFactory.getLogger(BasicMongoService.class);

    @Resource
    protected MongoOperations mongoOps;

    public <T> T findById(Object id, Class<T> entityClass) {
        if (id == null) {
            return null;
        }

        if (id instanceof CharSequence) {
            CharSequence cid = (CharSequence) id;
            if (StringUtils.isBlank(cid)) {
                return null;
            }
        }

        return mongoOps.findById(id, entityClass);
    }

    /**
     * 以分页方式获取查询结果。
     */
    public <T> List<T> find(Query query, Pagination page, Class<T> entityClass) {
        // 先获取结果集数量
        int count = (int) mongoOps.count(query, entityClass);

        // 计算分页
        page.setRowCount(count);
        page.initialize();
        logger.debug("Page query: count={}, skip={}, limit={}", count, page.getCurrentRowNum(), page.getPageSize());

        // 只获取分页下的数据
        query.skip(page.getCurrentRowNum() - 1).limit(page.getPageSize());

        return mongoOps.find(query, entityClass);
    }

    public void save(Object object) {
        mongoOps.save(object);
    }

    public void insert(Object object) {
        setCreationTimeIfPossible(object);
        mongoOps.insert(object);
    }

    public <T> void insert(Collection<T> objects, Class<T> entityClass) {
        for (T object : objects) {
            setCreationTimeIfPossible(object);
        }
        mongoOps.insert(objects, entityClass);
    }

    public <T> WriteResult updateFirst(Query query, Update update, Class<T> entityClass) {
        return mongoOps.updateFirst(query, update, entityClass);
    }

    public <T> WriteResult updateMulti(Query query, Update update, Class<T> entityClass) {
        return mongoOps.updateMulti(query, update, entityClass);
    }

    public void remove(Object object) {
        mongoOps.remove(object);
    }

    public <T> void remove(Query query, Class<T> entityClass) {
        mongoOps.remove(query, entityClass);
    }

    private void setCreationTimeIfPossible(Object object) {
        if (object instanceof StringIdEntity) {
            StringIdEntity entity = (StringIdEntity) object;
            if (entity.getCreationTime() == null) {
                entity.setCreationTime(new Date());
            }
        }

        if (object instanceof LongIdEntity) {
            LongIdEntity entity = (LongIdEntity) object;
            if (entity.getCreationTime() == null) {
                entity.setCreationTime(new Date());
            }
        }
    }

}
