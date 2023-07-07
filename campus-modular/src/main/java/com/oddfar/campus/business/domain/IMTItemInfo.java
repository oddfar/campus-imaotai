package com.oddfar.campus.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * i茅台预约商品信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IMTItemInfo {

    private String shopId;

    private int count;

    private String itemId;

    /**
     * 库存
     */
    private int inventory;


}
