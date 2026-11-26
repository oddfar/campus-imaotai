package com.oddfar.campus.business.api;

import com.oddfar.campus.business.service.IMTLogFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 推送标题构建测试 (#333)
 */
public class PushPlusApiTest {

    @Test
    public void testBuildTitle() {
        //4种事件类型 x 成功/失败
        assertEquals("张三-i茅台-预约-成功", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_RESERVE, 0));
        assertEquals("张三-i茅台-预约-失败", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_RESERVE, 1));
        assertEquals("张三-i茅台-耐力值-成功", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_ENERGY, 0));
        assertEquals("张三-i茅台-耐力值-失败", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_ENERGY, 1));
        assertEquals("张三-i茅台-旅行-成功", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_TRAVEL, 0));
        assertEquals("张三-i茅台-旅行-失败", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_TRAVEL, 1));
        assertEquals("张三-i茅台-申购-成功", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_APPOINTMENT, 0));
        assertEquals("张三-i茅台-申购-失败", PushPlusApi.buildTitle("张三", IMTLogFactory.TYPE_APPOINTMENT, 1));
        //未知类型退化为旧格式
        assertEquals("张三-i茅台成功", PushPlusApi.buildTitle("张三", 99, 0));
        assertEquals("张三-i茅台失败", PushPlusApi.buildTitle("张三", 99, 1));
    }
}
