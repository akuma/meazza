/* 
 * @(#)MyBatisRepository.java    Created on 2013-1-22
 * Copyright (c) 2013 Guomi. All rights reserved.
 */
package com.guomi.meazza.orm.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识 MyBatis 的 DAO，方便 {@link org.mybatis.spring.mapper.MapperScannerConfigurer} 的扫描。
 * 
 * @author akuma
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyBatisRepository {
}
