package com.oddfar.campus.business.controller;

import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IMTService;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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

    /**
     * 查询I茅台用户列表
     */
    @GetMapping("/list")
    public R list(IUser iUser) {
        PageResult<IUser> page = iUserService.page(iUser);

        return R.ok().put(page);
    }

    /**
     * 发送验证码
     */
    @GetMapping("/sendCode")
    public R sendCode(String mobile) {
        imtService.sendCode(mobile);

        return R.ok();
    }

    /**
     * 预约
     */
    @GetMapping("/reservation")
    public R reservation(String mobile) {
        IUser user = iUserMapper.selectById(mobile);
        if (user == null || StringUtils.isEmpty(user.getItemCode())) {
            return R.error("用户不存在或配置不对");
        } else {
            imtService.reservation(user);
            return R.ok();
        }

    }

    /**
     * 登录
     */
    @GetMapping("/login")
    public R login(String mobile, String code) {
        imtService.login(mobile, code);

        return R.ok();
    }


    /**
     * 获取I茅台用户详细信息
     */
    @GetMapping(value = "/{mobile}")
    public R getInfo(@PathVariable("mobile") Long mobile) {
        return R.ok(iUserMapper.selectById(mobile));
    }

    /**
     * 新增I茅台用户
     */
    @PreAuthorize("@ss.hasPermi('campus:user:add')")
    @PostMapping
    public R add(@RequestBody IUser iUser) {
        return R.ok(iUserService.insertIUser(iUser));
    }

    /**
     * 修改I茅台用户
     */
    @PreAuthorize("@ss.hasPermi('campus:user:edit')")
    @PutMapping
    public R edit(@RequestBody IUser iUser) {
        return R.ok(iUserService.updateIUser(iUser));
    }

    /**
     * 删除I茅台用户
     */
    @PreAuthorize("@ss.hasPermi('campus:user:remove')")
    @DeleteMapping("/{mobiles}")
    public R remove(@PathVariable Long[] mobiles) {
        return R.ok(iUserMapper.deleteBatchIds(Arrays.asList(mobiles)));
    }
}
