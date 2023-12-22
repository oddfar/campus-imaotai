package com.oddfar.campus.common.domain;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oddfar.campus.common.enums.BizCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R setData(Object data) {
        put("data", data);
        return this;
    }

    //利用fastjson进行逆转
    public <T> T getData(String key, TypeReference<T> typeReference) {
        Object data = get(key);//默认是map
        String s = JSON.toJSONString(data);
        T t = JSON.parseObject(s, typeReference);
        return t;
    }

    //利用fastjson进行逆转
    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get("data");
        String s = JSON.toJSONString(data);
        T t = JSON.parseObject(s, typeReference);
        return t;
    }


    public R() {
        put("code", 200);
        put("msg", "success");
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R error(BizCodeEnum bizCodeEnum) {
        R r = new R();
        r.put("code", bizCodeEnum.getCode());
        r.put("msg", bizCodeEnum.getMsg());
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Object data) {
        R r = new R();
        r.put("data", data);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public R put(Object value) {
        super.put("data", value);
        return this;
    }

    public R put(PageResult pageResult) {
        super.put("rows", pageResult.getRows());
        super.put("total", pageResult.getTotal());
        return this;
    }

    public R put(IPage page) {
        super.put("rows", page.getRecords());
        super.put("total", page.getTotal());
        return this;
    }


    public Integer getCode() {
        return (Integer) this.get("code");
    }
}
