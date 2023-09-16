package com.oddfar.campus.business.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oddfar.campus.business.domain.IMTItemInfo;
import com.oddfar.campus.business.domain.MapPoint;
import com.oddfar.campus.business.entity.IItem;
import com.oddfar.campus.business.entity.IShop;
import com.oddfar.campus.business.mapper.IItemMapper;
import com.oddfar.campus.business.mapper.IShopMapper;
import com.oddfar.campus.business.service.IShopService;
import com.oddfar.campus.common.core.RedisCache;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IShopServiceImpl extends ServiceImpl<IShopMapper, IShop> implements IShopService {

    private static final Logger logger = LoggerFactory.getLogger(IShopServiceImpl.class);

    @Autowired
    IShopMapper iShopMapper;
    @Autowired
    IItemMapper iItemMapper;

    @Autowired
    RedisCache redisCache;

    @Override
    public List<IShop> selectShopList() {

        List<IShop> shopList = redisCache.getCacheList("mt_shop_list");

        if (shopList != null && shopList.size() > 0) {
            return shopList;
        } else {
            refreshShop();
        }

        shopList = iShopMapper.selectList();


        return shopList;
    }

    //    @Async
    @Override
    public void refreshShop() {

        HttpRequest request = HttpUtil.createRequest(Method.GET,
                "https://static.moutai519.com.cn/mt-backend/xhr/front/mall/resource/get");

        JSONObject body = JSONObject.parseObject(request.execute().body());
        //获取shop的url
        String shopUrl = body.getJSONObject("data").getJSONObject("mtshops_pc").getString("url");
        //清空数据库
        iShopMapper.truncateShop();
        redisCache.deleteObject("mt_shop_list");

        String s = HttpUtil.get(shopUrl);

        JSONObject jsonObject = JSONObject.parseObject(s);
        Set<String> shopIdSet = jsonObject.keySet();
        List<IShop> list = new ArrayList<>();
        for (String iShopId : shopIdSet) {
            JSONObject shop = jsonObject.getJSONObject(iShopId);
            IShop iShop = new IShop(iShopId, shop);
//            iShopMapper.insert(iShop);
            list.add(iShop);
        }
        this.saveBatch(list);
        redisCache.setCacheList("mt_shop_list", list);
    }

    @Override
    public String getCurrentSessionId() {
        String mtSessionId = Convert.toStr(redisCache.getCacheObject("mt_session_id"));

        long dayTime = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if (StringUtils.isNotEmpty(mtSessionId)) {
            return mtSessionId;
        }

        String res = HttpUtil.get("https://static.moutai519.com.cn/mt-backend/xhr/front/mall/index/session/get/" + dayTime);
        //替换 current_session_id 673 ['data']['sessionId']
        JSONObject jsonObject = JSONObject.parseObject(res);

        if (jsonObject.getString("code").equals("2000")) {
            JSONObject data = jsonObject.getJSONObject("data");
            mtSessionId = data.getString("sessionId");
            redisCache.setCacheObject("mt_session_id", mtSessionId);

            iItemMapper.truncateItem();
            //item插入数据库
            JSONArray itemList = data.getJSONArray("itemList");
            for (Object obj : itemList) {
                JSONObject item = (JSONObject) obj;
                IItem iItem = new IItem(item);
                iItemMapper.insert(iItem);
            }

        }

        return mtSessionId;

    }

    @Override
    public void refreshItem() {
        redisCache.deleteObject("mt_session_id");
        getCurrentSessionId();
    }

    @Override
    public IShop selectByIShopId(String iShopId) {
        List<IShop> iShopList = iShopMapper.selectList("i_shop_id", iShopId);
        if (iShopList != null && iShopList.size() > 0) {
            return iShopList.get(0);
        } else {
            return null;
        }
//        return iShopMapper.selectOne(IShop::getIShopId, iShopId);
    }

    @Override
    public List<IMTItemInfo> getShopsByProvince(String province, String itemId) {
        String key = "mt_province:" + province + "." + getCurrentSessionId() + "." + itemId;
        List<IMTItemInfo> cacheList = redisCache.getCacheList(key);
        if (cacheList != null && cacheList.size() > 0) {
            return cacheList;
        } else {
            List<IMTItemInfo> imtItemInfoList = reGetShopsByProvince(province, itemId);
            redisCache.reSetCacheList(key, imtItemInfoList);
            redisCache.expire(key, 60, TimeUnit.MINUTES);
            return imtItemInfoList;
        }
    }

    public List<IMTItemInfo> reGetShopsByProvince(String province, String itemId) {

        long dayTime = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();

        String url = "https://static.moutai519.com.cn/mt-backend/xhr/front/mall/shop/list/slim/v3/" + getCurrentSessionId() + "/" + province + "/" + itemId + "/" + dayTime;

        String urlRes = HttpUtil.get(url);
        JSONObject res = null;
        try {
            res = JSONObject.parseObject(urlRes);
        } catch (JSONException jsonException) {
            String message = StringUtils.format("查询所在省市的投放产品和数量error: %s", url);
            logger.error(message);
            throw new ServiceException(message);
        }

//        JSONObject res = JSONObject.parseObject(HttpUtil.get(url));
        if (!res.containsKey("code") || !res.getString("code").equals("2000")) {
            String message = StringUtils.format("查询所在省市的投放产品和数量error: %s", url);
            logger.error(message);
            throw new ServiceException(message);
        }
        //组合信息
        List<IMTItemInfo> imtItemInfoList = new ArrayList<>();

        JSONObject data = res.getJSONObject("data");
        JSONArray shopList = data.getJSONArray("shops");

        for (Object obj : shopList) {
            JSONObject shops = (JSONObject) obj;
            JSONArray items = shops.getJSONArray("items");
            for (Object item : items) {
                JSONObject itemObj = (JSONObject) item;
                if (itemObj.getString("itemId").equals(itemId)) {
                    IMTItemInfo iItem = new IMTItemInfo(shops.getString("shopId"),
                            itemObj.getIntValue("count"), itemObj.getString("itemId"), itemObj.getIntValue("inventory"));
                    //添加
                    imtItemInfoList.add(iItem);
                }

            }


        }
        return imtItemInfoList;
    }

    @Override
    public String getShopId(int shopType, String itemId, String province, String city, String lat, String lng) {
        //查询所在省市的投放产品和数量
        List<IMTItemInfo> shopList = getShopsByProvince(province, itemId);
        //取id集合
        List<String> shopIdList = shopList.stream().map(IMTItemInfo::getShopId).collect(Collectors.toList());
        //获取门店列表
        List<IShop> iShops = selectShopList();
        //获取今日的门店信息列表
        List<IShop> list = iShops.stream().filter(i -> shopIdList.contains(i.getIShopId())).collect(Collectors.toList());

        String shopId = "";
        if (shopType == 1) {
            //预约本市出货量最大的门店
            shopId = getMaxInventoryShopId(shopList, list, city);
            if (StringUtils.isEmpty(shopId)) {
                //本市没有则预约本省最近的
                shopId = getMinDistanceShopId(list, province, lat, lng);
            }
        } else {
            //预约本省距离最近的门店
            shopId = getMinDistanceShopId(list, province, lat, lng);
        }

//        if (shopType == 2) {
//            // 预约本省距离最近的门店
//            shopId = getMinDistanceShopId(list, province, lat, lng);
//        }

        if (StringUtils.isEmpty(shopId)) {
            throw new ServiceException("申购时根据类型获取的门店商品id为空");
        }


        return shopId;
    }

    /**
     * 预约本市出货量最大的门店
     *
     * @param list1
     * @param list2
     * @param city
     * @return
     */
    public String getMaxInventoryShopId(List<IMTItemInfo> list1, List<IShop> list2, String city) {

        //本城市的shopId集合
        List<String> cityShopIdList = list2.stream().filter(iShop -> iShop.getCityName().contains(city))
                .map(IShop::getIShopId).collect(Collectors.toList());

        List<IMTItemInfo> collect = list1.stream().filter(i -> cityShopIdList.contains(i.getShopId())).sorted(Comparator.comparing(IMTItemInfo::getInventory).reversed()).collect(Collectors.toList());


        if (collect != null && collect.size() > 0) {
            return collect.get(0).getShopId();
        }

        return null;
    }

    /**
     * 预约本省距离最近的门店
     *
     * @param list2
     * @param province
     * @param lat
     * @param lng
     * @return
     */
    public String getMinDistanceShopId(List<IShop> list2, String province, String lat, String lng) {
        //本省的
        List<IShop> iShopList = list2.stream().filter(iShop -> iShop.getProvinceName().contains(province))
                .collect(Collectors.toList());

        MapPoint myPoint = new MapPoint(Double.parseDouble(lat), Double.parseDouble(lng));
        for (IShop iShop : iShopList) {
            MapPoint point = new MapPoint(Double.parseDouble(iShop.getLat()), Double.parseDouble(iShop.getLng()));
            Double disdance = getDisdance(myPoint, point);
            iShop.setDistance(disdance);
        }

        List<IShop> collect = iShopList.stream().sorted(Comparator.comparing(IShop::getDistance)).collect(Collectors.toList());

        return collect.get(0).getIShopId();

    }

    public static Double getDisdance(MapPoint point1, MapPoint point2) {
        double lat1 = (point1.getLatitude() * Math.PI) / 180; //将角度换算为弧度
        double lat2 = (point2.getLatitude() * Math.PI) / 180; //将角度换算为弧度
        double latDifference = lat1 - lat2;
        double lonDifference = (point1.getLongitude() * Math.PI) / 180 - (point2.getLongitude() * Math.PI) / 180;
        //计算两点之间距离   6378137.0 取自WGS84标准参考椭球中的地球长半径(单位:m)
        return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(latDifference / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(lonDifference / 2), 2))) * 6378137.0;
    }
}
