/*
 * @(#)LoggerUtils.java    Created on 2017年1月7日
 * Copyright (c) 2017 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

/**
 * 用于获取 logback 日志文件和 logger 对象的工具类。
 *
 * @author akuma
 */
public abstract class LogbackUtils {

    static final Logger logger = LoggerFactory.getLogger(LogbackUtils.class);

    private static String logDir = null;
    private static ch.qos.logback.classic.Logger logbackLogger = null;

    static {
        if (logger instanceof ch.qos.logback.classic.Logger) {
            logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

            for (Iterator<Appender<ILoggingEvent>> iter = logbackLogger.iteratorForAppenders(); iter.hasNext();) {
                Appender<ILoggingEvent> appender = iter.next();
                if (appender instanceof FileAppender<?>) {
                    logDir = ((FileAppender<ILoggingEvent>) appender).getFile();
                    break;
                }
            }

            if (logDir != null) {
                logDir = logDir.substring(0, logDir.lastIndexOf('/'));
            }
            logger.debug("logDir: {}", logDir);
        }
    }

    /**
     * 获取系统中所有的 Logger 对象。
     *
     * @return logger对象列表
     */
    public static List<ch.qos.logback.classic.Logger> getAllLoggers() {
        if (logbackLogger == null) {
            return Collections.emptyList();
        }

        return logbackLogger.getLoggerContext().getLoggerList();
    }

    /**
     * 设置 logger 对象的日志级别。
     *
     * @param loggerName
     *            logger 名称
     * @param level
     *            日志级别
     */
    public static void setLoggerLevel(String loggerName, Level level) {
        if (StringUtils.isEmpty(loggerName)) {
            throw new IllegalArgumentException("loggerName can't be empty");
        }

        if (level == null) {
            throw new IllegalArgumentException("level can't be null");
        }

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName);
        logger.setLevel(level);
    }

    /**
     * 取得所有日志文件。
     *
     * @return 日志文件列表
     */
    public static List<File> getAllLogFiles() {
        if (logDir == null) {
            return Collections.emptyList();
        }

        File file = new File(logDir);
        if (!file.exists()) {
            return Collections.emptyList();
        }

        List<File> logs = new ArrayList<File>();
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                logs.add(files[i]);
            }
        }

        return logs;
    }

    /**
     * 根据名称找到日志文件。
     *
     * @param filename
     *            日志文件名称
     * @return 日志文件
     */
    public static File getLogFile(String filename) {
        if (filename.contains("..")) {
            throw new IllegalArgumentException("Illegal filename");
        }

        File log = new File(logDir + File.separator + filename);
        return log.exists() ? log : null;
    }

    /**
     * 取得日志文件根目录。
     *
     * @return 日志文件根目录
     */
    protected static String getLogDir() {
        return logDir;
    }

}
