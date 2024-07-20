package com.oddfar.campus.imt.http.entity.userisolation;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import lombok.Data;


@Data
public class UserIsolationResponse extends ImtBaseResponse {
    private DataInfo data;
    @Data
    public static class DataInfo{
        private String xmy;
        private Integer energy;
        private XmTravel xmTravel;
        private EnergyReward energyReward;
    }
    @Data
    public static class XmTravel{
        private Integer status;
        private Integer remainChance;
        private Long travelEndTime;
    }
    @Data
    public static class EnergyReward{
        private Integer value;
    }
}
