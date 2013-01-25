/* 
 * @(#)LongIdEntity.java    Created on 2013-1-12
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.util.Date;

import com.guomi.meazza.support.IdEntity;

/**
 * ID 为数字类型的 Entity 基类。
 * 
 * @author akuma
 */
public abstract class LongIdEntity extends IdEntity<Long> {

    private static final long serialVersionUID = -8056033887637038214L;

    private Date modifyTime;
    private Date creationTime;

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
