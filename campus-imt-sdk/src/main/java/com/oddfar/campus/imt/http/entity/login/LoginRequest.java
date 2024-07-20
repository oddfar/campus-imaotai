package com.oddfar.campus.imt.http.entity.login;

import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import lombok.Data;

import java.util.HashMap;
@Data
public class LoginRequest extends ImtBaseRequest<LoginResponse> {
    private String mobile;
    private String vCode;
    private String md5;
    private String timestamp;
    private String MTVersion;
    @Override
    public String getPath() {
        return "https://app.moutai519.com.cn/xhr/front/user/register/login";
    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }

}
