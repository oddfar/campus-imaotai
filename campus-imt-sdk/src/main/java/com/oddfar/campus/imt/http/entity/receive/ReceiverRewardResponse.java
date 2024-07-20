package com.oddfar.campus.imt.http.entity.receive;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import lombok.Data;

@Data
public class ReceiverRewardResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private Long userId;
        private String userName;
        private String token;
        private String cookie;
    }
}
