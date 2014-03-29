/*
 * @(#)StringIdEntity.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.util.Date;

/**
 * ID 为字符串类型的 Entity 基类。
 * 
 * @author akuma
 */
public abstract class StringIdEntity extends IdEntity {

    private static final long serialVersionUID = -8056033887637038214L;

    private String id;
    private Date modifyTime;
    private Date creationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
