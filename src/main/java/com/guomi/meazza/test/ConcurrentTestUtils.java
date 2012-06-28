/* 
 * @(#)ConcurrentTestUtils.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于并发测试的工具类。
 * 
 * @author akuma
 */
public abstract class ConcurrentTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentTestUtils.class);

    /**
     * 以 <code>concurrentNum</code> 的并发数去同时执行 <code>task</code> 任务。
     * 
     * @param concurrentNum
     *            并发执行任务的线程数
     * @param task
     *            任务对象
     */
    public static void executeTask(int concurrentNum, Task task) {
        if (task == null) {
            return;
        }

        final Task aTask = task;
        final CountDownLatch startSignal = new CountDownLatch(1); // 任务开始信号
        final CountDownLatch doneSignal = new CountDownLatch(concurrentNum); // 任务结束信号

        ExecutorService executor = Executors.newCachedThreadPool(); // 线程池
        for (int i = 0; i < concurrentNum; i++) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        startSignal.await();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }

                    long start = System.currentTimeMillis();
                    try {
                        aTask.execute();
                    } finally {
                        doneSignal.countDown();
                        long elapsed = System.currentTimeMillis() - start;
                        logger.info("- Task[" + aTask.getName() + "] executed in " + elapsed + " ms.");
                    }
                }
            });
        }

        // 统计整个任务并发执行完成的时间
        long start = System.currentTimeMillis();

        startSignal.countDown();

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        long elapsed = System.currentTimeMillis() - start;
        logger.info("Task[" + task.getName() + "] elapsed " + elapsed + " ms.");

        executor.shutdown();
    }

    /**
     * 任务执行接口。
     */
    public interface Task {
        /**
         * 获取任务名称。
         * 
         * @return 任务名称
         */
        String getName();

        /**
         * 执行任务。
         */
        void execute();
    }

}
