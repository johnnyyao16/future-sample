package com.maycur.future.service;

import com.maycur.future.domain.FutureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FutureDataService {
    private static final Logger logger = LoggerFactory.getLogger(FutureDataService.class);

    public FutureData getFutureData() {
        FutureData futureData = new FutureData();
        new Thread(() -> {
            futureData.setData("Future data is ready!");
        }).start();
        return futureData;
    }

    public static void main(String args[]) throws InterruptedException {
        FutureDataService service = new FutureDataService();
        FutureData futureData1 = service.getFutureData();
        logger.info("Total 10s to get ready for future data");
        logger.info("We can do something here");
        TimeUnit.SECONDS.sleep(3);
        FutureData futureData2 = service.getFutureData();
        String result1 = futureData1.getData();
        logger.info("Future data 1 is ready, get result {}", result1);
        String result2 = futureData2.getData();
        logger.info("Future data 2 is ready, get result {}", result2);
    }
}
