package com.oddfar.campus.imt.http.entity;

import lombok.Data;

@Data
public abstract class ImtBaseResponse {
    private String code;
    private String message;

    public boolean isSuccess() {
        return "2000".equals(code);
    }

}
