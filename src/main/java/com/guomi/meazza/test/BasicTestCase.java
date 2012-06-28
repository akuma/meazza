/* 
 * @(#)BasicTestCase.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.test;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * 所有测试类的基类。
 * 
 * @author akuma
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
public abstract class BasicTestCase extends AbstractTransactionalJUnit4SpringContextTests {
}
