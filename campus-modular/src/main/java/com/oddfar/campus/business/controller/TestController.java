package com.oddfar.campus.business.controller;

import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IMTService;
import com.oddfar.campus.business.service.IShopService;
import com.oddfar.campus.common.annotation.Anonymous;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.annotation.Log;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@ApiResource(name = "测试", appCode = "test", resBizType = ResBizTypeEnum.BUSINESS)
@Log(openLog = false)
public class TestController {

    @Autowired
    private IUserMapper iUserMapper;
    @Autowired
    private IMTService imtService;
    @Autowired
    private IShopService iShopService;

    /**
     * 需要接口权限
     */
    @PreAuthorize("@ss.resourceAuth()")
    @GetMapping(value = "/1", name = "测试1的接口")
    public R test1() {

        return R.ok();
    }

    /**
     * 需要 'campus:test' 权限字符串
     */
    @PreAuthorize("@ss.hasPermi('campus:test')")
    @GetMapping(value = "/2", name = "测试2的接口")
    public R test2() {

        return R.ok();
    }

    /**
     * 匿名接口，不需要认证，所有人都可访问
     */
    @Anonymous
    @GetMapping(value = "/3", name = "测试3的接口")
    public R test3() {
        iShopService.selectShopList();
        return R.ok();
    }

}
