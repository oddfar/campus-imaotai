package com.oddfar.campus.business.task;

import com.oddfar.campus.business.service.IMTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * i茅台定时任务
 */
@Configuration
@EnableScheduling
public class CampusIMTTask {
    private static final Logger logger = LoggerFactory.getLogger(CampusIMTTask.class);

    @Autowired
    private IMTService imtService;


    /**
     * 9：05执行
     */
    @Async
    @Scheduled(cron = "0 5 9 ? * * ")
    public void reservationBatchTask() {
        logger.info("「批量预约开始」 " + new Date());
        imtService.reservationBatch();

    }

    @Async
    @Scheduled(cron = "0 25,55 6,7,8 ? * * ")
    public void refresh() {
        logger.info("「刷新数据」开始刷新版本号，预约item，门店shop列表  " + new Date());
        imtService.refreshAll();

    }


}