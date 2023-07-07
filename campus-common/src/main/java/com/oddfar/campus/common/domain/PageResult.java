package com.oddfar.campus.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public final class PageResult<T> implements Serializable {

    private List<T> rows;

    private long total;

    public PageResult() {
    }

    public PageResult(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    public PageResult(int total) {
        this.rows = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0);
    }

    public static <T> PageResult<T> empty(int total) {
        return new PageResult<>(total);
    }


}
