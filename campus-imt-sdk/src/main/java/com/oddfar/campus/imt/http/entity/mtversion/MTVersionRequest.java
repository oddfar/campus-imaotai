package com.oddfar.campus.imt.http.entity.mtversion;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class MTVersionRequest extends ImtBaseRequest<MTVersionResponse> {
    @Override
    public String getPath() {
        return "https://apps.apple.com/cn/app/i%E8%8C%85%E5%8F%B0/id1600482450";
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

}
