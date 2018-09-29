package com.maycur.future.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FutureData {
    private static final Logger logger = LoggerFactory.getLogger(FutureData.class);
    private volatile boolean isCompleted = false;
    private Object lock = new Object();

    private String data;

    public String getData() throws InterruptedException {
        synchronized (lock) {
            while (true) {
                if (isCompleted) {
                    logger.info("Set data is completed, return data!");
                    return data;
                } else {
                    logger.info("Set data is not completed, wait!");
                    lock.wait();
                }
            }
        }
    }

    public void setData(String data) {
        synchronized (lock) {
            // 模拟20s的延迟
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.data = data;
            isCompleted = true;
            lock.notify();
        }
    }
}
