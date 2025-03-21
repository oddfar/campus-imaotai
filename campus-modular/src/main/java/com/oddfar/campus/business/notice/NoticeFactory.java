package com.oddfar.campus.business.notice;

import com.oddfar.campus.common.enums.NoticeEnum;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.Service;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
public class NoticeFactory {

    private static final Map<NoticeEnum, NoticeHandler> SERVICE_MAP = Collections.synchronizedMap(new HashMap<>());

    public static void addService(NoticeEnum noticeEnum, NoticeHandler service) {
        if (SERVICE_MAP.containsKey(noticeEnum)) {
            return;
        }
        SERVICE_MAP.put(noticeEnum, service);
    }

    public static NoticeHandler getService(NoticeEnum noticeEnum) {
        NoticeHandler handler = SERVICE_MAP.get(noticeEnum);
        if (handler == null) {
            throw new RuntimeException("服务未找到");
        }
        return handler;
    }

}
