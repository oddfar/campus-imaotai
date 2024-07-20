package com.oddfar.campus.imt.http.entity.userisolation;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class UserIsolationRequest extends ImtBaseRequest<UserIsolationResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/isolationPage/getUserIsolationPageData";
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

}
