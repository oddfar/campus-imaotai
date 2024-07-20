package com.oddfar.campus.imt.http.entity.energyaward;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

import java.util.HashMap;
@Data
public class EnergYawardRequest extends ImtBaseRequest<EnergYawardResponse> {
    @Override
    public String getPath() {
        return "https://h5.moutai519.com.cn/game/isolationPage/getUserEnergyAward";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
