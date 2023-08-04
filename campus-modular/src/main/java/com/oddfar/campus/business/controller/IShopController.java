package com.oddfar.campus.business.controller;

import com.oddfar.campus.business.entity.IShop;
import com.oddfar.campus.business.mapper.IShopMapper;
import com.oddfar.campus.business.service.IShopService;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * i茅台商品Controller
 *
 * @author oddfar
 * @date 2023-07-05
 */
@RestController
@RequestMapping("/imt/shop")
@ApiResource(name = "i茅台商品Controller")
public class IShopController {
    @Autowired
    private IShopService iShopService;
    @Autowired
    private IShopMapper iShopMapper;

    /**
     * 查询i茅台商品列表
     */
    @GetMapping("/list")
    public R list(IShop iShop) {
        PageResult<IShop> page = iShopMapper.selectPage(iShop);

        return R.ok().put(page);
    }


    /**
     * 刷新i茅台商品列表
     */
    @GetMapping(value = "/refresh", name = "刷新i茅台商品列表")
    @PreAuthorize("@ss.resourceAuth()")
    public R refreshShop() {
        iShopService.refreshShop();
        return R.ok();
    }

}
