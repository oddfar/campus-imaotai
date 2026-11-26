package com.oddfar.campus.business.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 钉钉加签测试
 */
public class DingTalkApiTest {

    @Test
    public void testSign() {
        //已知向量：HmacSHA256(key=secret, msg="timestamp\nsecret") → Base64 → urlencode
        assertEquals("BYMqUCZnSqbfPf1GCfZftO7Rg2g6P%2BRp3%2F4%2BbLNtSGA%3D",
                DingTalkApi.sign(1700000000000L, "test-secret"));
    }
}
