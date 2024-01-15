package com.oddfar.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * GitHub: https://github.com/oddfar/campus-imaotai
 * @author oddfar
 */
@SpringBootApplication
public class CampusApplication {

    public static void main(String[] args) {
        //设置+8时区，避免因为时区问题导致预约时间不正确
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.of("+8")));
        SpringApplication.run(CampusApplication.class, args);
    }

}
