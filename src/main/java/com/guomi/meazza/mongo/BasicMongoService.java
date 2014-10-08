/*
 * @(#)BasicMongoService.java    Created on 2014年2月27日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package com.guomi.meazza.mongo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.guomi.meazza.support.LongIdEntity;
import com.guomi.meazza.support.StringIdEntity;
import com.guomi.meazza.util.ObjectHelper;
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

    //    public abstract String getCollectionName();

    /**
     * 根据 ID 查询文档。
     */
    public <T> T findById(Object id, Class<T> entityClass) {
        return findById(id, entityClass, null);
    }

    /**
     * 根据 ID 查询文档。
     */
    public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
        if (id == null) {
            return null;
        }

        if (id instanceof CharSequence) {
            CharSequence cid = (CharSequence) id;
            if (StringUtils.isBlank(cid)) {
                return null;
            }
        }

        if (StringUtils.isBlank(collectionName)) {
            return mongoOps.findById(id, entityClass);
        }

        return mongoOps.findById(id, entityClass, collectionName);
    }

    /**
     * 根据 {@code Query} 条件查询满足条件的第一个文档。
     */
    public <T> T findOne(Query query, Class<T> entityClass) {
        return mongoOps.findOne(query, entityClass);
    }

    /**
     * 根据 {@code Query} 条件查询满足条件的第一个文档。
     */
    public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
        return mongoOps.findOne(query, entityClass, collectionName);
    }

    /**
     * 查找满足 {@code query} 条件的第一个文档并修改。
     */
    public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
        return mongoOps.findAndModify(query, update, entityClass);
    }

    /**
     * 查找满足 {@code query} 条件的第一个文档并修改。
     */
    public <T> T findAndModify(Query query, Update update, Class<T> entityClass, String collectionName) {
        return mongoOps.findAndModify(query, update, entityClass, collectionName);
    }

    /**
     * 查找满足 {@code query} 条件的第一个文档并删除。
     */
    public <T> T findAndRemove(Query query, Class<T> entityClass) {
        return mongoOps.findAndRemove(query, entityClass);
    }

    /**
     * 查找满足 {@code query} 条件的第一个文档并删除。
     */
    public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return mongoOps.findAndRemove(query, entityClass, collectionName);
    }

    /**
     * 根据 {@code Query} 条件获取文档列表。
     */
    public <T> List<T> find(Query query, Class<T> entityClass) {
        return mongoOps.find(query, entityClass);
    }

    /**
     * 根据 {@code Query} 条件获取文档列表。
     */
    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return mongoOps.find(query, entityClass, collectionName);
    }

    /**
     * 根据 {@code Query} 条件以分页方式获取文档列表。
     */
    public <T> List<T> find(Query query, Pagination page, Class<T> entityClass) {
        return find(query, page, entityClass, null);
    }

    /**
     * 根据 {@code Query} 条件以分页方式获取文档列表。
     */
    public <T> List<T> find(Query query, Pagination page, Class<T> entityClass, String collectionName) {
        Integer count = null;
        if (page.isPageCountEnable()) {
            count = (int) mongoOps.count(query, entityClass); // 先获取结果集数量
            page.setRowCount(count); // 计算分页
        }

        page.initialize();

        if (count == null) {
            logger.debug("Page query: skip={}, limit={}", page.getCurrentRowNum(), page.getPageSize());
        } else {
            logger.debug("Page query: count={}, skip={}, limit={}", count, page.getCurrentRowNum(), page.getPageSize());
        }

        // 只获取分页下的数据
        query.skip(page.getCurrentRowNum() - 1).limit(page.getPageSize());

        // 处理排序方式
        if (!StringUtils.isBlank(page.getOrderBy())) {
            query.with(new Sort(page.isDesc() ? Direction.DESC : Direction.ASC, page.getOrderBy()));
        }

        if (StringUtils.isBlank(collectionName)) {
            return mongoOps.find(query, entityClass);
        }

        return mongoOps.find(query, entityClass, collectionName);
    }

    public <T> long count(Query query, Class<T> entityClass) {
        return mongoOps.count(query, entityClass);
    }

    public long count(Query query, String collectionName) {
        return mongoOps.count(query, collectionName);
    }

    public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
        return mongoOps.group(inputCollectionName, groupBy, entityClass);
    }

    public <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy,
            Class<T> entityClass) {
        return mongoOps.group(criteria, inputCollectionName, groupBy, entityClass);
    }

    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
        return mongoOps.aggregate(aggregation, outputType);
    }

    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String collectionName,
            Class<O> outputType) {
        return mongoOps.aggregate(aggregation, collectionName, outputType);
    }

    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
        return mongoOps.aggregate(aggregation, inputType, outputType);
    }

    public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
        return mongoOps.aggregate(aggregation, collectionName, outputType);
    }

    /**
     * 保存对象到文档中。<br>
     * <b>注意：</b> 如果文档已经存在，则会以新对象替换掉整个文档。
     */
    public void save(Object object) {
        mongoOps.save(object);
    }

    /**
     * 保存对象到文档中。<br>
     * <b>注意：</b> 如果文档已经存在，则会以新对象替换掉整个文档。
     */
    public void save(Object object, String collectionName) {
        mongoOps.save(object, collectionName);
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
     * 将对象作为一个新文档添加到集合中。<br>
     * 如果对象是 {@code StringIdEntity} 或 {@code LongIdEntity} 的子类，则会自动设置 {@code creationTime} 属性为当前系统时间。
     */
    public void insert(Object object, String collectionName) {
        setCreationTimeIfPossible(object);
        mongoOps.insert(object, collectionName);
    }

    /**
     * 将多个对象作为新文档添加到集合中。<br>
     * 如果对象是 {@code StringIdEntity} 或 {@code LongIdEntity} 的子类，则会自动设置 {@code creationTime} 属性为当前系统时间。
     */
    public <T> void insert(T[] objects, Class<T> entityClass) {
        insert(Arrays.asList(objects), entityClass);
    }

    /**
     * 将多个对象作为新文档添加到集合中。<br>
     * 如果对象是 {@code StringIdEntity} 或 {@code LongIdEntity} 的子类，则会自动设置 {@code creationTime} 属性为当前系统时间。
     */
    public <T> void insert(T[] objects, String collectionName) {
        insert(Arrays.asList(objects), collectionName);
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
     * 将多个对象作为新文档添加到集合中。<br>
     * 如果对象是 {@code StringIdEntity} 或 {@code LongIdEntity} 的子类，则会自动设置 {@code creationTime} 属性为当前系统时间。
     */
    public <T> void insert(Collection<T> objects, String collectionName) {
        for (T object : objects) {
            setCreationTimeIfPossible(object);
        }
        mongoOps.insert(objects, collectionName);
    }

    /**
     * 更新指定 {@code id} 的文档。
     */
    public <T> WriteResult updateById(Object id, Update update, Class<T> entityClass) {
        setModifyTimeIfPossible(update);
        return mongoOps.updateFirst(getQueryById(id), update, entityClass);
    }

    /**
     * 更新指定 {@code id} 的文档。
     */
    public WriteResult updateById(Object id, Update update, String collectionName) {
        setModifyTimeIfPossible(update);
        return mongoOps.updateFirst(getQueryById(id), update, collectionName);
    }

    /**
     * 更新满足 {@code query} 条件的第一个文档。
     */
    public <T> WriteResult updateFirst(Query query, Update update, Class<T> entityClass) {
        setModifyTimeIfPossible(update);
        return mongoOps.updateFirst(query, update, entityClass);
    }

    /**
     * 更新满足 {@code query} 条件的第一个文档。
     */
    public WriteResult updateFirst(Query query, Update update, String collectionName) {
        setModifyTimeIfPossible(update);
        return mongoOps.updateFirst(query, update, collectionName);
    }

    /**
     * 更新满足 {@code query} 条件的所有文档。
     */
    public <T> WriteResult updateMulti(Query query, Update update, Class<T> entityClass) {
        setModifyTimeIfPossible(update);
        return mongoOps.updateMulti(query, update, entityClass);
    }

    /**
     * 更新满足 {@code query} 条件的所有文档。
     */
    public WriteResult updateMulti(Query query, Update update, String collectionName) {
        setModifyTimeIfPossible(update);
        return mongoOps.updateMulti(query, update, collectionName);
    }

    /**
     * 根据对象中的 {@code id} 来删除匹配的文档。
     */
    public void remove(Object object) {
        mongoOps.remove(object);
    }

    /**
     * 根据对象中的 {@code id} 来删除匹配的文档。
     */
    public void remove(Object object, String collectionName) {
        mongoOps.remove(object, collectionName);
    }

    /**
     * 删除所有满足 {@code query} 条件的文档。
     */
    public <T> void remove(Query query, Class<T> entityClass) {
        mongoOps.remove(query, entityClass);
    }

    /**
     * 删除所有满足 {@code query} 条件的文档。
     */
    public void remove(Query query, String collectionName) {
        mongoOps.remove(query, collectionName);
    }

    /**
     * 删除指定 {@code id} 的文档。
     */
    public <T> void removeById(Object id, Class<T> entityClass) {
        if (!isEmptyId(id)) {
            mongoOps.remove(getQueryById(id), entityClass);
        }
    }

    /**
     * 删除指定 {@code id} 的文档。
     */
    public void removeById(Object id, String collectionName) {
        if (!isEmptyId(id)) {
            mongoOps.remove(getQueryById(id), collectionName);
        }
    }

    /**
     * 批量删除指定 {@code id} 的文档。
     */
    public <T> void removeByIds(Collection<?> ids, Class<T> entityClass) {
        if (!CollectionUtils.isEmpty(ids)) {
            remove(getQueryByIds(ids), entityClass);
        }
    }

    /**
     * 批量删除指定 {@code id} 的文档。
     */
    public void removeByIds(Collection<?> ids, String collectionName) {
        if (!CollectionUtils.isEmpty(ids)) {
            remove(getQueryByIds(ids), collectionName);
        }
    }

    /**
     * 获取根据 ID 查询的 Query 对象。
     */
    protected Query getQueryById(Object id) {
        return Query.query(Criteria.where("id").is(id));
    }

    /**
     * 获取根据多个 ID 查询的 Query 对象。
     */
    protected Query getQueryByIds(Object... ids) {
        return Query.query(Criteria.where("id").in(ids));
    }

    /**
     * 获取根据多个 ID 查询的 Query 对象。
     */
    protected Query getQueryByIds(Collection<?> ids) {
        return Query.query(Criteria.where("id").in(ids));
    }

    /**
     * 根据 bean 中的属性值生成 eq query 对象。满足以下条件的属性会添加到 query 中：
     *
     * <ul>
     * <li>如果属性是 CharSequence 类型且内容不为 null、空串、空格，则生成一个 eq 条件。</li>
     * <li>对于其他类型的属性，如果内容不为 null，则生成一个 eq 条件。</li>
     * </ul>
     */
    protected Query getDynamicEqQuery(Object bean, String... properties) {
        Query query = new Query();

        if (ArrayUtils.isEmpty(properties)) {
            return query;
        }

        for (String name : properties) {
            Object value = ObjectHelper.getPropertyValueQuietly(bean, name);
            if (value instanceof CharSequence) {
                if (!StringUtils.isBlank((CharSequence) value)) {
                    query.addCriteria(Criteria.where(name).is(value));
                }
            } else {
                if (value != null) {
                    query.addCriteria(Criteria.where(name).is(value));
                }
            }
        }

        return query;
    }

    /**
     * 根据 bean 中的属性值生成 update 对象，不为 null 的属性才添加到 update 中。
     */
    protected Update getDynamicUpdate(Object bean, String... properties) {
        if (ArrayUtils.isEmpty(properties)) {
            return null;
        }

        boolean hasUpdate = false;
        Update update = new Update();
        for (String name : properties) {
            Object value = ObjectHelper.getPropertyValueQuietly(bean, name);
            if (value != null) {
                update.set(name, value);
                hasUpdate = true;
            }
        }
        return hasUpdate ? update : null;
    }

    private void setCreationTimeIfPossible(Object object) {
        if (object instanceof StringIdEntity) {
            StringIdEntity entity = (StringIdEntity) object;
            if (entity.getCreationTime() == null) {
                entity.setCreationTime(new Date());
            }

            if (entity.getModifyTime() == null) {
                entity.setModifyTime(new Date());
            }
        }

        if (object instanceof LongIdEntity) {
            LongIdEntity entity = (LongIdEntity) object;
            if (entity.getCreationTime() == null) {
                entity.setCreationTime(new Date());
            }

            if (entity.getModifyTime() == null) {
                entity.setModifyTime(new Date());
            }
        }
    }

    private void setModifyTimeIfPossible(Update update) {
        if (update != null && !update.modifies("modifyTime")) {
            update.set("modifyTime", new Date());
        }
    }

    private boolean isEmptyId(Object id) {
        if (id == null) {
            return true;
        }

        if ((id instanceof String) && StringUtils.isBlank((String) id)) {
            return true;
        }

        return false;
    }

}
