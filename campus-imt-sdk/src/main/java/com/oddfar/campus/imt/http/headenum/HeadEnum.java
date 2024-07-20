package com.oddfar.campus.imt.http.headenum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum HeadEnum {
    USER_ID("userId",""),
    MT_DEVICE_ID("MT-Device-ID",""),
    MT_APP_VERSION("MT-APP-Version",""),
    MT_TOKEN("MT-Token",""),
    MT_INFO("MT-Info","028e7f96f6369cafe1d105579c5b9377"),
    USER_AGENT("User-Agent","iOS;16.3;Apple;?unrecognized?"),
    CONTENT_TYPE("Content-Type","application/json"),
    MT_LAT("MT-Lat",""),
    MT_LNG("MT-Lng","");

    private final String headeName;
    private final String headValue;
}
