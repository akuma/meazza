/* 
 * @(#)StringIdEntity.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.util.Date;

import com.guomi.meazza.support.IdEntity;

/**
 * ID 为字符串类型的 Entity 基类。
 * 
 * @author akuma
 */
public abstract class StringIdEntity extends IdEntity<String> {

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
