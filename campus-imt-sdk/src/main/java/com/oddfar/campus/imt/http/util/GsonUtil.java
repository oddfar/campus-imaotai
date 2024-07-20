package com.oddfar.campus.imt.http.util;
import com.google.gson.*;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GsonUtil {
    private static final Gson GSON = new GsonBuilder().create();
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return GSON.fromJson(jsonStr, clazz);
    }
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
    @SneakyThrows
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // 确保可以访问私有属性
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }
}
