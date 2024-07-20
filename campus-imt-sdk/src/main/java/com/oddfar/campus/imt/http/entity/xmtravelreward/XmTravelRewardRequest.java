package com.oddfar.campus.imt.http.entity.xmtravelreward;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class XmTravelRewardRequest extends ImtBaseRequest<XmTravelRewardResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/xmTravel/getXmTravelReward";
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

}
