package com.oddfar.campus.business.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.oddfar.campus.business.domain.IMTCacheConstants;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IMTLogFactory;
import com.oddfar.campus.business.service.IMTService;
import com.oddfar.campus.business.service.IShopService;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.core.RedisCache;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.imt.http.domain.ImtHttpClient;
import com.oddfar.campus.imt.http.entity.appointment.AppointmentRequest;
import com.oddfar.campus.imt.http.entity.appointment.AppointmentResponse;
import com.oddfar.campus.imt.http.entity.appointment.AppointmentResponse.DataInfo.ReservationItemVO;
import com.oddfar.campus.imt.http.entity.energyaward.EnergYawardRequest;
import com.oddfar.campus.imt.http.entity.energyaward.EnergYawardResponse;
import com.oddfar.campus.imt.http.entity.exchangerate.ExchangeRateInfoRequest;
import com.oddfar.campus.imt.http.entity.exchangerate.ExchangeRateInfoResponse;
import com.oddfar.campus.imt.http.entity.login.LoginRequest;
import com.oddfar.campus.imt.http.entity.login.LoginResponse;
import com.oddfar.campus.imt.http.entity.receive.ReceiverRewardRequest;
import com.oddfar.campus.imt.http.entity.receive.ReceiverRewardResponse;
import com.oddfar.campus.imt.http.entity.reservation.ReservationRequest;
import com.oddfar.campus.imt.http.entity.reservation.ReservationResponse;
import com.oddfar.campus.imt.http.entity.sendcode.SendCodeRequest;
import com.oddfar.campus.imt.http.entity.sendcode.SendCodeResponse;
import com.oddfar.campus.imt.http.entity.sharereward.ShareRewardRequest;
import com.oddfar.campus.imt.http.entity.sharereward.ShareRewardResponse;
import com.oddfar.campus.imt.http.entity.starttravel.StartTravelRequest;
import com.oddfar.campus.imt.http.entity.starttravel.StartTravelResponse;
import com.oddfar.campus.imt.http.entity.userisolation.UserIsolationRequest;
import com.oddfar.campus.imt.http.entity.userisolation.UserIsolationResponse;
import com.oddfar.campus.imt.http.entity.xmtravelreward.XmTravelRewardRequest;
import com.oddfar.campus.imt.http.entity.xmtravelreward.XmTravelRewardResponse;
import com.oddfar.campus.imt.http.headenum.CookieEnum;
import com.oddfar.campus.imt.http.headenum.HeadEnum;
import com.oddfar.campus.imt.http.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * i茅台接口请求服务 实现类
 */
@Service
@Slf4j
public class IMTServiceImpl implements IMTService {

    @Resource
    private IUserMapper iUserMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IShopService iShopService;
    @Resource
    private ImtHttpClient imtHttpClient;

    private final static String SALT = "2af72f100c356273d46284f6fd1dfc08";

    private final static String AES_KEY = "qbhajinldepmucsonaaaccgypwuvcjaa";
    private final static String AES_IV = "2018534749963515";

    /**
     * 项目启动时，初始化数据
     */
    @PostConstruct
    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshAll();
            }
        }).start();

    }


    @Override
    public String getMTVersion() {
        String mtVersion = Convert.toStr(redisCache.getCacheObject(IMTCacheConstants.MT_VERSION));
        if (StringUtils.isNotEmpty(mtVersion)) {
            return mtVersion;
        }
        String url = "https://apps.apple.com/cn/app/i%E8%8C%85%E5%8F%B0/id1600482450";
        String htmlContent = HttpUtil.get(url);
        Pattern pattern = Pattern.compile("new__latest__version\">(.*?)</p>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
            mtVersion = matcher.group(1);
            mtVersion = mtVersion.replace("版本 ", "");
        }
        redisCache.setCacheObject(IMTCacheConstants.MT_VERSION, mtVersion);

        return mtVersion;


    }

    @Override
    public void refreshMTVersion() {
        redisCache.deleteObject(IMTCacheConstants.MT_VERSION);
        getMTVersion();
    }

    @Override
    public Boolean sendCode(String mobile, String deviceId) {
        SendCodeRequest request = new SendCodeRequest();
        request.setMobile(mobile);
        long curTime = System.currentTimeMillis();
        request.setMd5(signature(mobile, curTime));
        request.setTimestamp(String.valueOf(curTime));

        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), deviceId);
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(HeadEnum.CONTENT_TYPE.getHeadeName(), HeadEnum.CONTENT_TYPE.getHeadValue());
        request.setHeadMap(headMap);
        SendCodeResponse response = imtHttpClient.execute(request);
        //成功返回 {"code":2000}
        log.info("「发送验证码返回」：" + response);
        if (String.valueOf(2000).equals(response.getCode())) {
            return Boolean.TRUE;
        } else {
            log.error("「发送验证码-失败」：" + response);
            throw new ServiceException("发送验证码错误");
        }

    }

    @Override
    public boolean login(String mobile, String code, String deviceId) {
        LoginRequest request = new LoginRequest();
        request.setMobile(mobile);
        request.setVCode(code);
        final long curTime = System.currentTimeMillis();
        request.setMd5(signature(mobile + code + "" + "", curTime));
        request.setTimestamp(String.valueOf(curTime));
        request.setMTVersion(getMTVersion());
        Map<String, Object> formData = GsonUtil.objectToMap(request);

        IUser iUser = iUserMapper.selectById(mobile);
        if (ObjectUtil.isNotNull(iUser)) {
            deviceId = iUser.getDeviceId();
        }

        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), deviceId);
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(HeadEnum.CONTENT_TYPE.getHeadeName(), HeadEnum.CONTENT_TYPE.getHeadValue());
        request.setHeadMap(headMap);
        request.setFormMap(formData);

        LoginResponse response = imtHttpClient.execute(request);
        if (String.valueOf(2000).equals(response.getCode())) {
//            log.info("「登录请求-成功」" + body.toJSONString());
            iUserService.insertIUser(Long.parseLong(mobile), deviceId, response);
            return true;
        } else {
            log.error("「登录请求-失败」" + response);
            throw new ServiceException("登录失败，本地错误日志已记录");
        }

    }


    @Override
    public void reservation(IUser iUser) {
        if (StringUtils.isEmpty(iUser.getItemCode())) {
            return;
        }
        String[] items = iUser.getItemCode().split("@");

        String logContent = "";
        for (String itemId : items) {
            try {
                String shopId = iShopService.getShopId(iUser.getShopType(), itemId,
                        iUser.getProvinceName(), iUser.getCityName(), iUser.getLat(), iUser.getLng());
                //预约
                String json = reservation(iUser, itemId, shopId);
                logContent += String.format("[预约项目]：%s\n[shopId]：%s\n[结果返回]：%s\n\n", itemId, shopId, json);

                //随机延迟3～5秒
                Random random = new Random();
                int sleepTime = random.nextInt(3) + 3;
                Thread.sleep(sleepTime * 1000);
            } catch (Exception e) {
                logContent += String.format("执行报错--[预约项目]：%s\n[结果返回]：%s\n\n", itemId, e.getMessage());
            }
        }

//        try {
//            //预约后领取耐力值
//            String energyAward = getEnergyAward(iUser);
//            logContent += "[申购耐力值]:" + energyAward;
//        } catch (Exception e) {
//            logContent += "执行报错--[申购耐力值]:" + e.getMessage();
//        }
        //日志记录
        IMTLogFactory.reservation(iUser, logContent);
        //预约后延迟领取耐力值
        getEnergyAwardDelay(iUser);
    }

    /**
     * 延迟执行：获取申购耐力值，并记录日志
     *
     * @param iUser
     */
    public void getEnergyAwardDelay(IUser iUser) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String logContent = "";
                //sleep 10秒
                try {
                    Thread.sleep(10000);
                    //预约后领取耐力值
                    String energyAward = getEnergyAward(iUser);
                    logContent += "[申购耐力值]:" + energyAward;
                } catch (Exception e) {
                    logContent += "执行报错--[申购耐力值]:" + e.getMessage();
                }
                //日志记录
                IMTLogFactory.reservation(iUser, logContent);
            }
        };
        new Thread(runnable).start();

    }

    // 领取小茅运
    public void receiveReward(IUser iUser) {
        ReceiverRewardRequest request = new ReceiverRewardRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(HeadEnum.MT_LAT.getHeadeName(), iUser.getLat());
        headMap.put(HeadEnum.MT_LNG.getHeadeName(), iUser.getLng());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");

        request.setHeadMap(headMap);
        ReceiverRewardResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = "领取小茅运失败";
            throw new ServiceException(message);
        }
    }

    public void shareReward(IUser iUser) {
        log.info("「领取每日首次分享获取耐力」：" + iUser.getMobile());
        ShareRewardRequest request = new ShareRewardRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(HeadEnum.MT_LAT.getHeadeName(), iUser.getLat());
        headMap.put(HeadEnum.MT_LNG.getHeadeName(), iUser.getLng());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");

        request.setHeadMap(headMap);
        ShareRewardResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = "领取每日首次分享获取耐力失败";
            throw new ServiceException(message);
        }
    }

    //获取申购耐力值
    @Override
    public String getEnergyAward(IUser iUser) {
        EnergYawardRequest request = new EnergYawardRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(HeadEnum.MT_LAT.getHeadeName(), iUser.getLat());
        headMap.put(HeadEnum.MT_LNG.getHeadeName(), iUser.getLng());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");

        request.setHeadMap(headMap);
        EnergYawardResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(200).equals(response.getCode())) {
            String message = response.getMessage();
            throw new ServiceException(message);
        }
        return response.toString();
    }

    @Override
    public void getTravelReward(IUser iUser) {
        String logContent = "";
        try {
            String s = travelReward(iUser);
            logContent += "[获得旅行奖励]:" + s;
        } catch (Exception e) {
//            e.printStackTrace();
            logContent += "执行报错--[获得旅行奖励]:" + e.getMessage();
        }
        //日志记录
        IMTLogFactory.reservation(iUser, logContent);
    }

    /**
     * 获得旅行奖励
     *
     * @param iUser
     * @return
     */
    public String travelReward(IUser iUser) {
        //9-20点才能领取旅行奖励
        int hour = DateUtil.hour(new Date(), true);
        if (!(9 <= hour && hour < 20)) {
            String message = "活动未开始，开始时间9点-20点";
            throw new ServiceException(message);
        }
        Map<String, Integer> pageData = getUserIsolationPageData(iUser);
        Integer status = pageData.get("status");
        if (status == 3) {
            Integer currentPeriodCanConvertXmyNum = pageData.get("currentPeriodCanConvertXmyNum");
            Double travelRewardXmy = getXmTravelReward(iUser);
            // 获取小茅运
            receiveReward(iUser);
            //首次分享获取耐力
            shareReward(iUser);
            //本次旅行奖励领取后, 当月实际剩余旅行奖励
            if (travelRewardXmy > currentPeriodCanConvertXmyNum) {
                String message = "当月无可领取奖励";
                throw new ServiceException(message);
            }
        }

        Integer remainChance = pageData.get("remainChance");
        if (remainChance < 1) {
            String message = "当日旅行次数已耗尽";
            throw new ServiceException(message);
        } else {
            //小茅运旅行活动
            return startTravel(iUser);
        }
    }

    //小茅运旅行活动
    public String startTravel(IUser iUser) {
        StartTravelRequest request = new StartTravelRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");

        request.setHeadMap(headMap);
        StartTravelResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = "开始旅行失败：" + response.getMessage();
            throw new ServiceException(message);
        }
        return response.toString();
    }


    //查询 可获取小茅运
    public Double getXmTravelReward(IUser iUser) {
        //查询旅行奖励:
        XmTravelRewardRequest request = new XmTravelRewardRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");

        request.setHeadMap(headMap);
        XmTravelRewardResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = response.getMessage();
            throw new ServiceException(message);
        }
        XmTravelRewardResponse.DataInfo data = response.getData();
        //例如 1.95
        return data.getTravelRewardXmy();

    }

    //获取用户页面数据
    public Map<String, Integer> getUserIsolationPageData(IUser iUser) {
        //查询小茅运信息
        UserIsolationRequest request = new UserIsolationRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");
        request.setHeadMap(headMap);

        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("__timestamp", DateUtil.currentSeconds());
        request.setFormMap(formMap);

        UserIsolationResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = response.getMessage();
            throw new ServiceException(message);
        }
        UserIsolationResponse.DataInfo data = response.getData();
        Integer energy = data.getEnergy();
        UserIsolationResponse.XmTravel xmTravel = data.getXmTravel();
        UserIsolationResponse.EnergyReward energyReward = data.getEnergyReward();
        Integer status = xmTravel.getStatus();
        Long travelEndTime = xmTravel.getTravelEndTime();
        Integer remainChance = xmTravel.getRemainChance();
        Integer energyValue = energyReward.getValue();
        if (energyValue > 0) {
            //获取申购耐力值
            getEnergyAward(iUser);
            energy += energyValue;
        }

//        本月剩余旅行奖励
        int exchangeRateInfo = getExchangeRateInfo(iUser);
        if (exchangeRateInfo <= 0) {
            String message = "当月无可领取奖励";
            throw new ServiceException(message);
        }

        Long endTime = travelEndTime * 1000;
        // 未开始
        if (status == 1) {
            if (energy < 100) {
                String message = String.format("耐力不足100, 当前耐力值:%s", energy);
                throw new ServiceException(message);
            }
        }
        // 进行中
        if (status == 2) {
            Date date = new Date(endTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(date);
            String message = String.format("旅行暂未结束,本次旅行结束时间:%s ", formattedDate);
            throw new ServiceException(message);
        }

        Map<String, Integer> map = new HashMap<>();

        map.put("remainChance", remainChance);
        map.put("status", status);
        map.put("currentPeriodCanConvertXmyNum", getExchangeRateInfo(iUser));
        return map;
    }

    // 获取本月剩余奖励耐力值
    public int getExchangeRateInfo(IUser iUser) {
        ExchangeRateInfoRequest request = new ExchangeRateInfoRequest();
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        headMap.put(CookieEnum.COOKIE.getCookieName(), CookieEnum.MT_TOKEN_WAP.getCookieName() + iUser.getCookie() + CookieEnum.MT_DEVICE_ID_WAP.getCookieName() + iUser.getDeviceId() + ";");
        request.setHeadMap(headMap);

        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("__timestamp", DateUtil.currentSeconds());
        request.setFormMap(formMap);

        ExchangeRateInfoResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = response.getMessage();
            throw new ServiceException(message);
        }
        //获取本月剩余奖励耐力值
        return response.getData().getCurrentPeriodCanConvertXmyNum();

    }

    @Async
    @Override
    public void reservationBatch() {
        int minute = DateUtil.minute(new Date());
        List<IUser> iUsers = iUserService.selectReservationUserByMinute(minute);

        for (IUser iUser : iUsers) {
            log.info("「开始预约用户」" + iUser.getMobile());
            //预约
            reservation(iUser);
            //延时3秒
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Async
    @Override
    public void getTravelRewardBatch() {
        try {
            int minute = DateUtil.minute(new Date());
            List<IUser> iUsers = iUserService.selectReservationUserByMinute(minute);
//        List<IUser> iUsers = iUserService.selectReservationUser();

            for (IUser iUser : iUsers) {
                log.info("「开始获得旅行奖励」" + iUser.getMobile());
                getTravelReward(iUser);
                //延时3秒
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshAll() {
        refreshMTVersion();
        iShopService.refreshShop();
        iShopService.refreshItem();
    }

    @Override
    public void appointmentResults() {
        log.info("申购结果查询开始=========================");
        List<IUser> iUsers = iUserService.selectReservationUser();
        for (IUser iUser : iUsers) {
            try {
                AppointmentRequest request = new AppointmentRequest();
                // 添加请求头到Map
                HashMap<String, String> headMap = new HashMap<>();
                headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
                headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
                headMap.put(HeadEnum.MT_TOKEN.getHeadeName(), iUser.getToken());
                headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());

                request.setHeadMap(headMap);
                AppointmentResponse response = imtHttpClient.execute(request);
                log.info("查询申购结果回调: user->{},response->{}", iUser.getMobile(), response);
                if (!"2000".equals(response.getCode())) {
                    String message = response.getMessage();
                    throw new ServiceException(message);
                }
                List<ReservationItemVO> itemVOS = response.getData().getReservationItemVOS();

                if (Objects.isNull(itemVOS) || itemVOS.isEmpty()) {
                    log.info("申购记录为空: user->{}", iUser.getMobile());
                    continue;
                }
                for (ReservationItemVO itemVO : itemVOS) {
                    if (Integer.valueOf(2).equals(itemVO.getStatus()) && DateUtil.between(itemVO.getReservationTime(), new Date(), DateUnit.HOUR) < 24) {
                        String logContent = DateUtil.formatDate(itemVO.getReservationTime()) + " 申购" + itemVO.getItemName() + "成功";
                        IMTLogFactory.reservation(iUser, logContent);
                    }
                }
            } catch (Exception e) {
                log.error("查询申购结果失败:失败原因->{}", e.getMessage(), e);
            }

        }
        log.info("申购结果查询结束=========================");
    }

    public String reservation(IUser iUser, String itemId, String shopId) {
        Map<String, Object> map = new HashMap<>();
        JSONArray itemArray = new JSONArray();
        Map<String, Object> info = new HashMap<>();
        info.put("count", 1);
        info.put("itemId", itemId);

        itemArray.add(info);

        map.put("itemInfoList", itemArray);

        map.put("sessionId", iShopService.getCurrentSessionId());
        map.put("userId", iUser.getUserId().toString());
        map.put("shopId", shopId);

        ReservationRequest request = new ReservationRequest();
        request.setSessionId(iShopService.getCurrentSessionId());
        request.setUserId(iUser.getUserId().toString());
        request.setShopId(shopId);
        request.setActParam(AesEncrypt(JSON.toJSONString(map)));

        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(HeadEnum.MT_DEVICE_ID.getHeadeName(), iUser.getDeviceId());
        headMap.put(HeadEnum.MT_APP_VERSION.getHeadeName(), getMTVersion());
        headMap.put(HeadEnum.MT_TOKEN.getHeadeName(), iUser.getToken());
        headMap.put(HeadEnum.MT_INFO.getHeadeName(), HeadEnum.MT_INFO.getHeadValue());
        headMap.put(HeadEnum.MT_LNG.getHeadeName(), iUser.getLng());
        headMap.put(HeadEnum.MT_LAT.getHeadeName(), iUser.getLat());
        headMap.put(HeadEnum.CONTENT_TYPE.getHeadeName(), HeadEnum.CONTENT_TYPE.getHeadValue());
        headMap.put(HeadEnum.USER_ID.getHeadeName(), iUser.getUserId().toString());
        headMap.put(HeadEnum.USER_AGENT.getHeadeName(), HeadEnum.USER_AGENT.getHeadValue());
        request.setHeadMap(headMap);

        ReservationResponse response = imtHttpClient.execute(request);
        if (!String.valueOf(2000).equals(response.getCode())) {
            String message = response.getMessage();
            throw new ServiceException(message);
        }
        return response.toString();
    }

    /**
     * 加密
     *
     * @param params
     * @return
     */
    public static String AesEncrypt(String params) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, AES_KEY.getBytes(), AES_IV.getBytes());
        return aes.encryptBase64(params);
    }

    /**
     * 解密
     *
     * @param params
     * @return
     */
    public static String AesDecrypt(String params) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, AES_KEY.getBytes(), AES_IV.getBytes());
        return aes.decryptStr(params);
    }

    /**
     * 获取验证码的md5签名，密钥+手机号+时间
     * 登录的md5签名：密钥+mobile+vCode+ydLogId+ydToken
     *
     * @param content
     * @return
     */
    private static String signature(String content, long time) {

        String text = SALT + content + time;
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            md5 = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }


}
