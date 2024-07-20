package com.oddfar.campus.imt.http.headenum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CookieEnum {
    COOKIE("cookie"),
    MT_TOKEN_WAP("MT-Token-Wap="),
    MT_DEVICE_ID_WAP(";MT-Device-ID-Wap=");

    private final String cookieName;
}
