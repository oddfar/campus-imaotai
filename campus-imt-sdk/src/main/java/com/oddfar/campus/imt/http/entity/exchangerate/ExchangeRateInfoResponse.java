package com.oddfar.campus.imt.http.entity.exchangerate;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import com.oddfar.campus.imt.http.entity.xmtravelreward.XmTravelRewardResponse;
import lombok.Data;

@Data
public class ExchangeRateInfoResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private Integer currentPeriodCanConvertXmyNum;
    }
}
