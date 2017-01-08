/*
 * @(#)LogbackUtilsTest.java    Created on 2017年1月7日
 * Copyright (c) 2017 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * @author akuma
 */
public class LogbackUtilsTest {

    @Test
    public void testGetAllLoggers() {
        List<Logger> loggers = LogbackUtils.getAllLoggers();

        LogbackUtils.logger.debug("Log 1 : This log will be display");

        System.out.println("--- All loggers ---");
        for (Logger logger : loggers) {
            System.out.println(logger.getName() + ": " + logger.getEffectiveLevel());
        }

        for (Logger logger : loggers) {
            logger.setLevel(Level.ERROR);
            System.out.println(logger.getName() + "(New Level): " + logger.getEffectiveLevel());
        }

        LogbackUtils.logger.debug("Log 2: This log will not be display");

        for (Logger logger : loggers) {
            logger.setLevel(Level.DEBUG);
            System.out.println(logger.getName() + "(New Level): " + logger.getEffectiveLevel());
        }

        LogbackUtils.logger.debug("Log 3: This log will be display");
    }

    @Test
    public void testSetLoggerLevel() {
        LogbackUtils.setLoggerLevel("test", Level.WARN);

        Logger logger = (Logger) LoggerFactory.getLogger("test");
        assertTrue(logger.getLevel().equals(Level.WARN));

        LogbackUtils.setLoggerLevel("test", Level.DEBUG);
        assertTrue(logger.getLevel().equals(Level.DEBUG));
    }

    @Test
    public void testGetLogDir() {
        String logRoot = LogbackUtils.getLogDir();
        assertEquals("logs", logRoot);
    }

    @Test
    public void testGetAllLogFiles() {
        List<File> logFiles = LogbackUtils.getAllLogFiles();
        assertNotNull(logFiles);
    }

    @Test
    public void testGetLogFile() {
        File logFile = LogbackUtils.getLogFile("not-exist-file");
        assertNull(logFile);
    }

}
