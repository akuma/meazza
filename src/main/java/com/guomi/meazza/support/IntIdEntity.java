/*
 * @(#)IntIdEntity.java    Created on 2015年3月19日
 * Copyright (c) 2015 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.util.Date;

/**
 * ID 为 Integer 类型的 Entity 基类。
 *
 * @author akuma
 */
public class IntIdEntity extends IdEntity {

    private static final long serialVersionUID = -8056033887637038214L;

    private Integer id;
    private Date modifyTime;
    private Date creationTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
