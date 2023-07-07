package com.oddfar.campus.business.task;

import com.oddfar.campus.business.service.IMTService;
import com.oddfar.campus.business.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * i茅台定时任务
 */
@Configuration
@EnableScheduling
public class CampusIMTTask {

    @Autowired
    private IMTService imtService;
    @Autowired
    private IShopService iShopService;

    /**
     * 9：05执行
     */
    @Async
    @Scheduled(cron = "0 5 9 ? * * ")
    public void reservationBatchTask() {

        imtService.reservationBatch();

    }

    @Async
    @Scheduled(cron = "0 25,55 6,7,8 ? * * ")
    public void refresh() {

        imtService.refreshMTVersion();
        iShopService.refreshShop();
        iShopService.refreshItem();

    }


}