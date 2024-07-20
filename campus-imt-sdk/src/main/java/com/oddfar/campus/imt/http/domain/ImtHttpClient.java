package com.oddfar.campus.imt.http.domain;

import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import com.oddfar.campus.imt.http.entity.ImtBaseResponse;

public interface ImtHttpClient {
    <T extends ImtBaseResponse> T execute(ImtBaseRequest<T> request);
}
