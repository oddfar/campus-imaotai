package com.oddfar.campus.imt.http.entity.sendcode;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

@Data
public class SendCodeRequest extends ImtBaseRequest<SendCodeResponse> {
    private String mobile;
    private String md5;
    private String timestamp;
    @Override
    public String getPath() {
        return "https://app.moutai519.com.cn/xhr/front/user/register/vcode";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
