package com.oddfar.campus.imt.http.rest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.oddfar.campus.imt.http.domain.ImtHttpClient;
import com.oddfar.campus.imt.http.entity.ImtBaseRequest;
import com.oddfar.campus.imt.http.entity.ImtBaseResponse;
import com.oddfar.campus.imt.http.headenum.HeadEnum;
import com.oddfar.campus.imt.http.util.GsonUtil;

import java.util.HashMap;
import java.util.Map;

public class ImtHttpClientImpl implements ImtHttpClient {
    @Override
    public <T extends ImtBaseResponse> T execute(ImtBaseRequest<T> request) {
        String url = request.getPath();
        Method method = request.getMethod();
//        String cookie = request.getCookie();
        HashMap<String, String> headMap = request.getHeadMap();
        Map<String, Object> formMap = request.getFormMap();

        HttpRequest httpRequest = HttpUtil.createRequest(method, url);
        if (CollectionUtil.isNotEmpty(formMap)) {
            httpRequest.form(formMap);
        }
        //如果是json
        if (HeadEnum.CONTENT_TYPE.getHeadValue().equals(headMap.get(HeadEnum.CONTENT_TYPE.getHeadeName()))) {
            httpRequest.body(GsonUtil.toJson(request));
        }
        httpRequest.headerMap(headMap, true);
        String body = httpRequest.execute().body();
        return GsonUtil.fromJson(body, request.getResponseClass());
    }
}
