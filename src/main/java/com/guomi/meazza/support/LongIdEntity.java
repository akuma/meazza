/* 
 * @(#)LongIdEntity.java    Created on 2013-1-12
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.util.Date;

/**
 * ID 为数字类型的 Entity 基类。
 * 
 * @author akuma
 */
public abstract class LongIdEntity extends IdEntity<Long> {

    private static final long serialVersionUID = -8056033887637038214L;

    // 对于数据库中是 int 类型的 ID 字段，如果在实体类中定义为 Long，而在 mybatis 配置中又不将 id column 设置为 long，
    // 则获取到的 ID 类型将是 Integer 类型，所以在这个子类中需要重新定义 id
    private Long id;
    private Date modifyTime;
    private Date creationTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
