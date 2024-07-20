package com.oddfar.campus.imt.http.entity.mtversion;

import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MTVersionResponse extends ImtBaseResponse {
    private String htmlContent;
}
