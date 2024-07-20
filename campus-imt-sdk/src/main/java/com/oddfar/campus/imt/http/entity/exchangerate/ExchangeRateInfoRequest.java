package com.oddfar.campus.imt.http.entity.exchangerate;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class ExchangeRateInfoRequest extends ImtBaseRequest<ExchangeRateInfoResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/synthesize/exchangeRateInfo";
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

}
