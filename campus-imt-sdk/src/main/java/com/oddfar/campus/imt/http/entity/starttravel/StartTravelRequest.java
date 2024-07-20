package com.oddfar.campus.imt.http.entity.starttravel;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class StartTravelRequest extends ImtBaseRequest<StartTravelResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/xmTravel/startTravel";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
