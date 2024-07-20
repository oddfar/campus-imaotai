package com.oddfar.campus.imt.http.entity.xmtravelreward;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import com.oddfar.campus.imt.http.entity.userisolation.UserIsolationResponse;
import jdk.nashorn.internal.codegen.DumpBytecode;
import lombok.Data;

@Data
public class XmTravelRewardResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private Double travelRewardXmy;
    }
}
