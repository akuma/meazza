/*
 * @(#)BasicMongoService.java    Created on 2014年2月27日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package com.guomi.meazza.mongo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.guomi.meazza.support.LongIdEntity;
import com.guomi.meazza.support.StringIdEntity;
import com.guomi.meazza.util.Pagination;
import com.guomi.meazza.util.StringUtils;
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

    /**
     * 根据 ID 查询文档。
     */
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
     * 根据 {@code Query} 条件查询满足条件的第一个文档。
     */
    public <T> T findOne(Query query, Class<T> entityClass) {
        return mongoOps.findOne(query, entityClass);
    }

    /**
     * 根据 {@code Query} 条件以分页方式获取文档列表。
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

        // 处理排序方式
        if (!StringUtils.isBlank(page.getOrderBy())) {
            query.with(new Sort(page.isDesc() ? Direction.DESC : Direction.ASC, page.getOrderBy()));
        }

        return mongoOps.find(query, entityClass);
    }

    /**
     * 保存对象到文档中。<br>
     * <b>注意：</b> 如果文档已经存在，则会以新对象替换掉整个文档。
     */
    public void save(Object object) {
        mongoOps.save(object);
    }

    /**
     * 将对象作为一个新文档添加到集合中。<br>
     * 如果对象是 {@code StringIdEntity} 或 {@code LongIdEntity} 的子类，则会自动设置 {@code creationTime} 属性为当前系统时间。
     */
    public void insert(Object object) {
        setCreationTimeIfPossible(object);
        mongoOps.insert(object);
    }

    /**
     * 将多个对象作为新文档添加到集合中。<br>
     * 如果对象是 {@code StringIdEntity} 或 {@code LongIdEntity} 的子类，则会自动设置 {@code creationTime} 属性为当前系统时间。
     */
    public <T> void insert(Collection<T> objects, Class<T> entityClass) {
        for (T object : objects) {
            setCreationTimeIfPossible(object);
        }
        mongoOps.insert(objects, entityClass);
    }

    /**
     * 更新满足 {@code query} 条件的第一个文档。
     */
    public <T> WriteResult updateFirst(Query query, Update update, Class<T> entityClass) {
        return mongoOps.updateFirst(query, update, entityClass);
    }

    /**
     * 更新满足 {@code query} 条件的所有文档。
     */
    public <T> WriteResult updateMulti(Query query, Update update, Class<T> entityClass) {
        return mongoOps.updateMulti(query, update, entityClass);
    }

    /**
     * 根据对象中的 {@code id} 来删除匹配的文档。
     */
    public void remove(Object object) {
        mongoOps.remove(object);
    }

    /**
     * 删除所有满足 {@code query} 条件的文档。
     */
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
