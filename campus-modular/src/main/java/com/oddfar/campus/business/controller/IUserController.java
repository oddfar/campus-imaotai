package com.oddfar.campus.business.controller;

import com.oddfar.campus.business.entity.IShop;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IMTService;
import com.oddfar.campus.business.service.IShopService;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * I茅台用户Controller
 *
 * @author oddfar
 * @date 2023-07-06
 */
@RestController
@RequestMapping("/imt/user")
@ApiResource(name = "I茅台用户Controller")
public class IUserController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IUserMapper iUserMapper;
    @Autowired
    private IMTService imtService;
    @Autowired
    private IShopService iShopService;

    /**
     * 查询I茅台用户列表
     */
    @GetMapping(value = "/list", name = "查询I茅台用户列表")
    @PreAuthorize("@ss.resourceAuth()")
    public R list(IUser iUser) {
        PageResult<IUser> page = iUserService.page(iUser);

        return R.ok().put(page);
    }

    /**
     * 发送验证码
     */
    @GetMapping(value = "/sendCode", name = "发送验证码")
    @PreAuthorize("@ss.resourceAuth()")
    public R sendCode(String mobile, String deviceId) {
        imtService.sendCode(mobile, deviceId);

        return R.ok();
    }

    /**
     * 预约
     */
    @GetMapping(value = "/reservation", name = "预约")
    @PreAuthorize("@ss.resourceAuth()")
    public R reservation(String mobile) {
        IUser user = iUserMapper.selectById(mobile);
        if (user == null) {
            return R.error("用户不存在");
        }
        if (StringUtils.isEmpty(user.getItemCode())) {
            return R.error("商品预约code为空");
        }

        imtService.reservation(user);
        return R.ok();
    }

    /**
     * 小茅运旅行活动
     */
    @GetMapping(value = "/travelReward", name = "旅行")
    @PreAuthorize("@ss.resourceAuth()")
    public R travelReward(String mobile) {
        IUser user = iUserMapper.selectById(mobile);
        if (user == null) {
            return R.error("用户不存在");
        } else {
            imtService.getTravelReward(user);
            return R.ok();
        }
    }

    /**
     * 登录
     */
    @GetMapping(value = "/login", name = "登录")
    @PreAuthorize("@ss.resourceAuth()")
    public R login(String mobile, String code, String deviceId) {
        imtService.login(mobile, code, deviceId);

        return R.ok();
    }


    /**
     * 获取I茅台用户详细信息
     */
    @PreAuthorize("@ss.resourceAuth()")
    @GetMapping(value = "/{mobile}", name = "获取I茅台用户详细信息")
    public R getInfo(@PathVariable("mobile") Long mobile) {
        return R.ok(iUserMapper.selectById(mobile));
    }

    /**
     * 新增I茅台用户
     */
    @PreAuthorize("@ss.resourceAuth()")
    @PostMapping(name = "新增I茅台用户")
    public R add(@RequestBody IUser iUser) {

        IShop iShop = iShopService.selectByIShopId(iUser.getIshopId());
        if (iShop == null) {
            throw new ServiceException("门店商品id不存在");
        }
        iUser.setLng(iShop.getLng());
        iUser.setLat(iShop.getLat());
        iUser.setProvinceName(iShop.getProvinceName());
        iUser.setCityName(iShop.getCityName());

        return R.ok(iUserService.insertIUser(iUser));
    }

    /**
     * 修改I茅台用户
     */
    @PreAuthorize("@ss.resourceAuth()")
    @PutMapping(name = "修改I茅台用户")
    public R edit(@RequestBody IUser iUser) {
        IShop iShop = iShopService.selectByIShopId(iUser.getIshopId());
        if (iShop == null) {
            throw new ServiceException("门店商品id不存在");
        }
        iUser.setLng(iShop.getLng());
        iUser.setLat(iShop.getLat());
        iUser.setProvinceName(iShop.getProvinceName());
        iUser.setCityName(iShop.getCityName());
        return R.ok(iUserService.updateIUser(iUser));
    }

    /**
     * 删除I茅台用户
     */
    @PreAuthorize("@ss.resourceAuth()")
    @DeleteMapping(value = "/{mobiles}", name = "删除I茅台用户")
    public R remove(@PathVariable Long[] mobiles) {
        return R.ok(iUserMapper.deleteIUser(mobiles));
    }
}
