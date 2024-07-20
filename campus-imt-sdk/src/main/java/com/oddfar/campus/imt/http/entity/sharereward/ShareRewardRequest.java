package com.oddfar.campus.imt.http.entity.sharereward;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class ShareRewardRequest extends ImtBaseRequest<ShareRewardResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/xmTravel/shareReward";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
