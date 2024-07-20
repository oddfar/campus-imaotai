package com.oddfar.campus.imt.http.entity.appointment;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class AppointmentRequest extends ImtBaseRequest<AppointmentResponse> {
    @Override
    public String getPath() {
        return "https://app.moutai519.com.cn/xhr/front/mall/reservation/list/pageOne/query";
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

}
