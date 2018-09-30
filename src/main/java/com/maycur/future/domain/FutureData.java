package com.maycur.future.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FutureData {
    private static final Logger logger = LoggerFactory.getLogger(FutureData.class);
    private boolean isCompleted = false;

    private String data;

    public synchronized String getData() throws InterruptedException {
        while (true) {
            if (isCompleted) {
                logger.info("Set data is completed, return data!");
                return data;
            } else {
                logger.info("Set data is not completed, wait!");
                wait();
            }

        }
    }

    public synchronized void setData(String data) throws InterruptedException {
        if (isCompleted) {
            return;
        }
        // 模拟20s的延迟
        TimeUnit.SECONDS.sleep(10);
        this.data = data;
        isCompleted = true;
        notifyAll();
    }

}
