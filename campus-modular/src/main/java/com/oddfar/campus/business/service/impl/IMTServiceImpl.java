package com.oddfar.campus.business.service.impl;

import cn.hutool.core.convert.Convert;
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
import com.oddfar.campus.business.mapper.IShopMapper;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IMTServiceImpl implements IMTService {

    private static final Logger logger = LoggerFactory.getLogger(IMTServiceImpl.class);

    @Autowired
    IShopMapper iShopMapper;

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
    public boolean sendCode(String mobile) {
        Map<String, Object> data = new HashMap<>();
        data.put("mobile", mobile);
        data.put("md5", signature(mobile));
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
//        data.put("MT-APP-Version", MT_VERSION);

        HttpRequest request = HttpUtil.createRequest(Method.POST,
                "https://app.moutai519.com.cn/xhr/front/user/register/vcode");


        request.header("MT-Lat", "28.499562");
        request.header("MT-K", "1675213490331");
        request.header("MT-Lng", "102.182324");
        request.header("Host", "app.moutai519.com.cn");
        request.header("MT-User-Tag", "0");
        request.header("Accept", "*/*");
        request.header("MT-Network-Type", "WIFI");
//        request.header("MT-Token", "2");
        request.header("MT-Team-ID", "");
        request.header("MT-Info", "028e7f96f6369cafe1d105579c5b9377");
        request.header("MT-Device-ID", "2F2075D0-B66C-4287-A903-DBFF6358342A");
        request.header("MT-Bundle-ID", "com.moutai.mall");
        request.header("Accept-Language", "en-CN;q=1, zh-Hans-CN;q=0.9");
        request.header("MT-Request-ID", "167560018873318465");
        request.header("MT-APP-Version", getMTVersion());
        request.header("User-Agent", "iOS;16.3;Apple;?unrecognized?");
        request.header("MT-R", "clips_OlU6TmFRag5rCXwbNAQ/Tz1SKlN8THcecBp/HGhHdw==");
        request.header("Content-Length", "93");
        request.header("Accept-Encoding", "gzip, deflate, br");
        request.header("Connection", "keep-alive");
        request.header("Content-Type", "application/json");
//        request.header("userId", "1");

        HttpResponse execute = request.body(JSONObject.toJSONString(data)).execute();
        JSONObject jsonObject = JSONObject.parseObject(execute.body());
        //成功返回 {"code":2000}
        logger.info("「发送验证码返回」：" + jsonObject.toJSONString());
        if (jsonObject.getString("code").equals("2000")) {
            return true;
        } else {
            logger.error("「发送验证码-失败」：" + jsonObject.toJSONString());
            throw new ServiceException("发送验证码错误");
//            return false;
        }

    }

    @Override
    public boolean login(String mobile, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("vCode", code);
        map.put("ydToken", "");
        map.put("ydLogId", "");

        map.put("md5", signature(mobile + code + "" + ""));

        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("MT-APP-Version", getMTVersion());

        HttpRequest request = HttpUtil.createRequest(Method.POST,
                "https://app.moutai519.com.cn/xhr/front/user/register/login");


        request.header("MT-Lat", "28.499562");
        request.header("MT-K", "1675213490331");
        request.header("MT-Lng", "102.182324");
        request.header("Host", "app.moutai519.com.cn");
        request.header("MT-User-Tag", "0");
        request.header("Accept", "*/*");
        request.header("MT-Network-Type", "WIFI");
//        request.header("MT-Token", "2");
        request.header("MT-Team-ID", "");
        request.header("MT-Info", "028e7f96f6369cafe1d105579c5b9377");
        request.header("MT-Device-ID", "2F2075D0-B66C-4287-A903-DBFF6358342A");
        request.header("MT-Bundle-ID", "com.moutai.mall");
        request.header("Accept-Language", "en-CN;q=1, zh-Hans-CN;q=0.9");
        request.header("MT-Request-ID", "167560018873318465");
        request.header("MT-APP-Version", getMTVersion());
        request.header("User-Agent", "iOS;16.3;Apple;?unrecognized?");
        request.header("MT-R", "clips_OlU6TmFRag5rCXwbNAQ/Tz1SKlN8THcecBp/HGhHdw==");
        request.header("Content-Length", "93");
        request.header("Accept-Encoding", "gzip, deflate, br");
        request.header("Connection", "keep-alive");
        request.header("Content-Type", "application/json");
//        request.header("userId", "1");

        HttpResponse execute = request.body(JSONObject.toJSONString(map)).execute();

        JSONObject body = JSONObject.parseObject(execute.body());


        if (body.getString("code").equals("2000")) {
            logger.info("「登录请求-成功」" + body.toJSONString());
            iUserService.insertIUser(Long.parseLong(mobile), body);
            return true;
        } else {
            logger.error("「登录请求-失败」" + body.toJSONString());
            throw new ServiceException("登录失败，日志已记录");
//            return false;
        }

    }


    @Override
    public void reservation(IUser iUser) {
        if (StringUtils.isEmpty(iUser.getItemCode())) {
            return;
        }
        String[] items = iUser.getItemCode().split("@");

        for (String itemId : items) {
            String shopId = iShopService.getShopId(iUser.getShopType(), itemId,
                    iUser.getProvinceName(), iUser.getCityName(), iUser.getLat(), iUser.getLng());
            if (StringUtils.isNotEmpty(shopId)) {
                //预约
                JSONObject json = reservation(iUser, itemId, shopId);
                //日志记录
                IMTLogFactory.reservation(iUser, shopId, json);
            } else {
                IMTLogFactory.reservation(iUser, shopId);
            }

        }

    }


    @Override
    public void reservationBatch() {
        List<IUser> iUsers = iUserService.selectReservationUser();

        for (IUser iUser : iUsers) {
            try {
                logger.info("「开始预约用户」" + iUser.getMobile());
                //预约
                reservation(iUser);
                //延时一秒
//                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                IMTLogFactory.reservation(iUser, e);
                e.printStackTrace();
            }

        }
    }

    @Override
    public void refreshAll() {
        refreshMTVersion();
        iShopService.refreshShop();
        iShopService.refreshItem();
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
        request.header("MT-K", "1675213490331");
        request.header("MT-Lng", iUser.getLng());
        request.header("Host", "app.moutai519.com.cn");
        request.header("MT-User-Tag", "0");
        request.header("Accept", "*/*");
        request.header("MT-Network-Type", "WIFI");
        request.header("MT-Token", iUser.getToken());
        request.header("MT-Team-ID", "");
        request.header("MT-Info", "028e7f96f6369cafe1d105579c5b9377");
        request.header("MT-Device-ID", "2F2075D0-B66C-4287-A903-DBFF6358342A");
        request.header("MT-Bundle-ID", "com.moutai.mall");
        request.header("Accept-Language", "en-CN;q=1, zh-Hans-CN;q=0.9");
        request.header("MT-Request-ID", "167560018873318465");
        request.header("MT-APP-Version", getMTVersion());
        request.header("User-Agent", "iOS;16.3;Apple;?unrecognized?");
        request.header("MT-R", "clips_OlU6TmFRag5rCXwbNAQ/Tz1SKlN8THcecBp/HGhHdw==");
        request.header("Content-Length", "93");
        request.header("Accept-Encoding", "gzip, deflate, br");
        request.header("Connection", "keep-alive");
        request.header("Content-Type", "application/json");
        request.header("userId", iUser.getUserId().toString());

        HttpResponse execute = request.body(JSONObject.toJSONString(map)).execute();

        JSONObject body = JSONObject.parseObject(execute.body());

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
    private static String signature(String content) {

        String text = SALT + content + System.currentTimeMillis();
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
