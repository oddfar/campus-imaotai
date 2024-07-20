package com.oddfar.campus.imt.http.entity.reservation;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import lombok.Data;

@Data
public class ReservationResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private Long userId;
        private String userName;
        private String token;
        private String cookie;
    }
}
