package com.oddfar.campus.imt.http.entity.login;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import com.oddfar.campus.imt.http.entity.appointment.AppointmentResponse;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LoginResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private Long userId;
        private String userName;
        private String token;
        private String cookie;
    }
}
