package com.oddfar.campus.imt.http.entity.appointment;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AppointmentResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private List<ReservationItemVO> reservationItemVOS;
        @Data
        public static class ReservationItemVO{
            private Integer status;
            private Date reservationTime;
            private String itemName;
        }
    }
}
