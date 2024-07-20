package com.oddfar.campus.imt.http.entity.reservation;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReservationRequest extends ImtBaseRequest<ReservationResponse> {
    private String sessionId;
    private String userId;
    private String shopId;
    private String actParam;
    private List<Map<String, Object>> itemInfoList;
    @Override
    public String getPath() {
        return "https://app.moutai519.com.cn/xhr/front/mall/reservation/add";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
