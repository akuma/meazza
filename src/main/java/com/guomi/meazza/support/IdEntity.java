/*
 * @(#)IdEntity.java    Created on 2012-8-2
 * Copyright (c) 2012 Guomi. All rights reserved.
 */
package com.guomi.meazza.support;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *  统一定义 id 的 entity 基类。
 *
 * @author akuma
 * @since 0.0.15
 */
public abstract class IdEntity implements Serializable {

    private static final long serialVersionUID = 7685930087139789958L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
