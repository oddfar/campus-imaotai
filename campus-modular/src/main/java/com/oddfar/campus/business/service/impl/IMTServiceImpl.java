package com.oddfar.campus.business.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IMTLogFactory;
import com.oddfar.campus.business.service.IMTService;
import com.oddfar.campus.business.service.IShopService;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.core.RedisCache;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IMTServiceImpl implements IMTService {

    private static final Logger logger = LoggerFactory.getLogger(IMTServiceImpl.class);

    @Autowired
    private IUserMapper iUserMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IShopService iShopService;

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
        String mtVersion = Convert.toStr(redisCache.getCacheObject("mt_version"));
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
        redisCache.setCacheObject("mt_version", mtVersion);

        return mtVersion;

    }

    @Override
    public void refreshMTVersion() {
        redisCache.deleteObject("mt_version");
        getMTVersion();
    }

    @Override
    public Boolean sendCode(String mobile, String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("mobile", mobile);
        final long curTime = System.currentTimeMillis();
        data.put("md5", signature(mobile, curTime));
        data.put("timestamp", String.valueOf(curTime));
//        data.put("MT-APP-Version", MT_VERSION);

        HttpRequest request = HttpUtil.createRequest(Method.POST,
                "https://app.moutai519.com.cn/xhr/front/user/register/vcode");


        request.header("MT-Device-ID", deviceId);
        request.header("MT-APP-Version", getMTVersion());
        request.header("User-Agent", "iOS;16.3;Apple;?unrecognized?");

        request.header("Content-Type", "application/json");

        HttpResponse execute = request.body(JSONObject.toJSONString(data)).execute();
        JSONObject jsonObject = JSONObject.parseObject(execute.body());
        //成功返回 {"code":2000}
        logger.info("「发送验证码返回」：" + jsonObject.toJSONString());
        if (jsonObject.getString("code").equals("2000")) {
            return Boolean.TRUE;
        } else {
            logger.error("「发送验证码-失败」：" + jsonObject.toJSONString());
            throw new ServiceException("发送验证码错误");
//            return false;
        }

    }

    @Override
    public boolean login(String mobile, String code, String deviceId) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("vCode", code);

        final long curTime = System.currentTimeMillis();
        map.put("md5", signature(mobile + code + "" + "", curTime));

        map.put("timestamp", String.valueOf(curTime));
        map.put("MT-APP-Version", getMTVersion());

        HttpRequest request = HttpUtil.createRequest(Method.POST,
                "https://app.moutai519.com.cn/xhr/front/user/register/login");
        IUser user = iUserMapper.selectById(mobile);
        if (user != null) {
            deviceId = user.getDeviceId();
        }
        request.header("MT-Device-ID", deviceId);
        request.header("MT-APP-Version", getMTVersion());
        request.header("User-Agent", "iOS;16.3;Apple;?unrecognized?");
        request.header("Content-Type", "application/json");

        HttpResponse execute = request.body(JSONObject.toJSONString(map)).execute();

        JSONObject body = JSONObject.parseObject(execute.body());

        if (body.getString("code").equals("2000")) {
//            logger.info("「登录请求-成功」" + body.toJSONString());
            iUserService.insertIUser(Long.parseLong(mobile), deviceId, body);
            return true;
        } else {
            logger.error("「登录请求-失败」" + body.toJSONString());
            throw new ServiceException("登录失败，本地错误日志已记录");
//            return false;
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
                JSONObject json = reservation(iUser, itemId, shopId);
                logContent += String.format("[预约项目]：%s\n[shopId]：%s\n[结果返回]：%s\n\n", itemId, shopId, json.toString());
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
    public void receiveReward(IUser iUser){
        String url = "https://h5.moutai519.com.cn/game/xmTravel/receiveReward";
        HttpRequest request = HttpUtil.createRequest(Method.POST, url);

        request.header("MT-Device-ID", iUser.getDeviceId())
                .header("MT-APP-Version", getMTVersion())
                .header("User-Agent", "iOS;16.3;Apple;?unrecognized?")
                .header("MT-Lat", iUser.getLat())
                .header("MT-Lng", iUser.getLng())
                .cookie("MT-Token-Wap=" + iUser.getCookie() + ";MT-Device-ID-Wap=" + iUser.getDeviceId() + ";");

        HttpResponse execute = request.execute();
        JSONObject body = JSONObject.parseObject(execute.body());

        if(body.getInteger("code") != 2000){
            String message = "领取小茅运失败";
            throw new ServiceException(message);
        }
    }

    //获取申购耐力值
    @Override
    public String getEnergyAward(IUser iUser) {
        String url = "https://h5.moutai519.com.cn/game/isolationPage/getUserEnergyAward";
        HttpRequest request = HttpUtil.createRequest(Method.POST, url);

        request.header("MT-Device-ID", iUser.getDeviceId())
                .header("MT-APP-Version", getMTVersion())
                .header("User-Agent", "iOS;16.3;Apple;?unrecognized?")
                .header("MT-Lat", iUser.getLat())
                .header("MT-Lng", iUser.getLng())
                .cookie("MT-Token-Wap=" + iUser.getCookie() + ";MT-Device-ID-Wap=" + iUser.getDeviceId() + ";");

        String body = request.execute().body();

        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getInteger("code") != 200) {
            String message = jsonObject.getString("message");
            throw new ServiceException(message);
        }

        return body;
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
        String url = "https://h5.moutai519.com.cn/game/xmTravel/startTravel";
        HttpRequest request = HttpUtil.createRequest(Method.POST, url);

        request.header("MT-Device-ID", iUser.getDeviceId())
                .header("MT-APP-Version", getMTVersion())
                .header("User-Agent", "iOS;16.3;Apple;?unrecognized?")
                .cookie("MT-Token-Wap=" + iUser.getCookie() + ";MT-Device-ID-Wap=" + iUser.getDeviceId() + ";");
        String body = request.execute().body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getInteger("code") != 2000) {
            String message = "开始旅行失败：" + jsonObject.getString("message");
            throw new ServiceException(message);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        //最后返回 {"startTravelTs":1690798861076}
        return jsonObject.toString();
    }


    //查询 可获取小茅运
    public Double getXmTravelReward(IUser iUser) {
        //查询旅行奖励:
        String url = "https://h5.moutai519.com.cn/game/xmTravel/getXmTravelReward";
        HttpRequest request = HttpUtil.createRequest(Method.GET, url);

        request.header("MT-Device-ID", iUser.getDeviceId())
                .header("MT-APP-Version", getMTVersion())
                .header("User-Agent", "iOS;16.3;Apple;?unrecognized?")
                .cookie("MT-Token-Wap=" + iUser.getCookie() + ";MT-Device-ID-Wap=" + iUser.getDeviceId() + ";");
        String body = request.execute().body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getInteger("code") != 2000) {
            String message = jsonObject.getString("message");
            throw new ServiceException(message);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        Double travelRewardXmy = data.getDouble("travelRewardXmy");
        //例如 1.95
        return travelRewardXmy;

    }

    //获取用户页面数据
    public Map<String, Integer> getUserIsolationPageData(IUser iUser) {
        //查询小茅运信息
        String url = "https://h5.moutai519.com.cn/game/isolationPage/getUserIsolationPageData";
        HttpRequest request = HttpUtil.createRequest(Method.GET, url);

        request.header("MT-Device-ID", iUser.getDeviceId())
                .header("MT-APP-Version", getMTVersion())
                .header("User-Agent", "iOS;16.3;Apple;?unrecognized?")
                .cookie("MT-Token-Wap=" + iUser.getCookie() + ";MT-Device-ID-Wap=" + iUser.getDeviceId() + ";");
        String body = request.form("__timestamp", DateUtil.currentSeconds()).execute().body();

        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getInteger("code") != 2000) {
            String message = jsonObject.getString("message");
            throw new ServiceException(message);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        //xmy: 小茅运值
        String xmy = data.getString("xmy");
        //energy: 耐力值
        int energy = data.getIntValue("energy");
        JSONObject xmTravel = data.getJSONObject("xmTravel");
        JSONObject energyReward = data.getJSONObject("energyReward");
        //status: 1. 未开始 2. 进行中 3. 已完成
        Integer status = xmTravel.getInteger("status");
        // travelEndTime: 旅行结束时间
        Long travelEndTime = xmTravel.getLong("travelEndTime");
        //remainChance 今日剩余旅行次数
        int remainChance = xmTravel.getIntValue("remainChance");

        //可领取申购耐力值奖励
        Integer energyValue = energyReward.getInteger("value");

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
        String url = "https://h5.moutai519.com.cn/game/synthesize/exchangeRateInfo";
        HttpRequest request = HttpUtil.createRequest(Method.GET, url);

        request.header("MT-Device-ID", iUser.getDeviceId())
                .header("MT-APP-Version", getMTVersion())
                .header("User-Agent", "iOS;16.3;Apple;?unrecognized?")
                .cookie("MT-Token-Wap=" + iUser.getCookie() + ";MT-Device-ID-Wap=" + iUser.getDeviceId() + ";");

        String body = request.form("__timestamp", DateUtil.currentSeconds()).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getInteger("code") != 2000) {
            String message = jsonObject.getString("message");
            throw new ServiceException(message);
        }
        //获取本月剩余奖励耐力值
        return jsonObject.getJSONObject("data").getIntValue("currentPeriodCanConvertXmyNum");

    }

    @Async
    @Override
    public void reservationBatch() {
        int minute = DateUtil.minute(new Date());
        List<IUser> iUsers = iUserService.selectReservationUserByMinute(minute);

        for (IUser iUser : iUsers) {
            logger.info("「开始预约用户」" + iUser.getMobile());
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
                logger.info("「开始获得旅行奖励」" + iUser.getMobile());
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
        logger.info("申购结果查询开始=========================");
        List<IUser> iUsers = iUserService.selectReservationUser();
        for (IUser iUser : iUsers) {
            try {
                String url = "https://app.moutai519.com.cn/xhr/front/mall/reservation/list/pageOne/query";
                String body = HttpUtil.createRequest(Method.GET, url)
                        .header("MT-Device-ID", iUser.getDeviceId())
                        .header("MT-APP-Version", getMTVersion())
                        .header("MT-Token", iUser.getToken())
                        .header("User-Agent", "iOS;16.3;Apple;?unrecognized?").execute().body();
                JSONObject jsonObject = JSONObject.parseObject(body);
                logger.info("查询申购结果回调: user->{},response->{}", iUser.getMobile(), body);
                if (jsonObject.getInteger("code") != 2000) {
                    String message = jsonObject.getString("message");
                    throw new ServiceException(message);
                }
                JSONArray itemVOs = jsonObject.getJSONObject("data").getJSONArray("reservationItemVOS");
                if(Objects.isNull(itemVOs) || itemVOs.isEmpty()){
                    logger.info("申购记录为空: user->{}", iUser.getMobile());
                    continue;
                }
                for (Object itemVO : itemVOs) {
                    JSONObject item = JSON.parseObject(itemVO.toString());
                    // 预约时间在24小时内的
                    if (item.getInteger("status") == 2 && DateUtil.between(item.getDate("reservationTime"), new Date(), DateUnit.HOUR) < 24) {
                        String logContent = DateUtil.formatDate(item.getDate("reservationTime")) + " 申购" + item.getString("itemName") + "成功";
                        IMTLogFactory.reservation(iUser, logContent);
                    }
                }
            } catch (Exception e) {
                logger.error("查询申购结果失败:失败原因->{}", e.getMessage(),e);
            }

        }
        logger.info("申购结果查询结束=========================");
    }

    public JSONObject reservation(IUser iUser, String itemId, String shopId) {
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

        map.put("actParam", AesEncrypt(JSON.toJSONString(map)));

        HttpRequest request = HttpUtil.createRequest(Method.POST,
                "https://app.moutai519.com.cn/xhr/front/mall/reservation/add");

        request.header("MT-Lat", iUser.getLat());
        request.header("MT-Lng", iUser.getLng());
        request.header("MT-Token", iUser.getToken());
        request.header("MT-Info", "028e7f96f6369cafe1d105579c5b9377");
        request.header("MT-Device-ID", iUser.getDeviceId());
        request.header("MT-APP-Version", getMTVersion());
        request.header("User-Agent", "iOS;16.3;Apple;?unrecognized?");
        request.header("Content-Type", "application/json");
        request.header("userId", iUser.getUserId().toString());

        HttpResponse execute = request.body(JSONObject.toJSONString(map)).execute();

        JSONObject body = JSONObject.parseObject(execute.body());
        //{"code":2000,"data":{"successDesc":"申购完成，请于7月6日18:00查看预约申购结果","reservationList":[{"reservationId":17053404357,"sessionId":678,"shopId":"233331084001","reservationTime":1688608601720,"itemId":"10214","count":1}],"reservationDetail":{"desc":"申购成功后将以短信形式通知您，请您在申购成功次日18:00前确认支付方式，并在7天内完成提货。","lotteryTime":1688637600000,"cacheValidTime":1688637600000}}}
        if (body.getInteger("code") != 2000) {
            String message = body.getString("message");
            throw new ServiceException(message);
        }
//        logger.info(body.toJSONString());
        return body;
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
