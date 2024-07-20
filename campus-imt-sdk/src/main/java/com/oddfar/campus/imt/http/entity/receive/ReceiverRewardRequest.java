package com.oddfar.campus.imt.http.entity.receive;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class ReceiverRewardRequest extends ImtBaseRequest<ReceiverRewardResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/xmTravel/receiveReward";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
