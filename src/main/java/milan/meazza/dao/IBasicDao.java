/* 
 * @(#)IBasicDao.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.dao;

import java.io.Serializable;
import java.util.List;

/**
 * DAO 接口类，包含了对某个实体的数据库表进行操作的常用方法。
 * 
 * @author akuma
 * @param <T>
 *            模板参数
 */
public interface IBasicDao<T> {

    /**
     * 获取所有实体对象的列表。
     * 
     * @return 实体对象列表
     */
    List<T> findAll();

    /**
     * 获取某个实体对象。
     * 
     * @param entityId
     *            实体对应的 ID
     * @return 实体对象
     */
    T findById(Serializable entityId);

    /**
     * 添加一个实体信息。
     * 
     * @param entity
     *            实体对象
     * @return 添加成功后的实体对象
     */
    T insert(T entity);

    /**
     * 更新实体信息。
     * 
     * @param entity
     *            实体对象
     * @return 受影响的行数
     */
    int update(T entity);

    /**
     * 删除实体对象。
     * 
     * @param entityId
     *            实体对象的 ID
     * @return 受影响的行数
     */
    int delete(Serializable entityId);

}
