package com.oddfar.campus.imt.http.entity;

import cn.hutool.http.Method;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class ImtBaseRequest <T extends ImtBaseResponse> {
    private HashMap<String,String> headMap;
    private Map<String,Object> formMap;
    private String cookie;
    public abstract String getPath();
    public abstract Method getMethod();
    @SuppressWarnings("unchecked")
    public Class<T> getResponseClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
