package com.oddfar.campus.common.enums;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
public enum NoticeEnum {
    PUSH_PLUS, WEB_HOOk
    ;

    public static NoticeEnum getNoticeType(int type) {
        for (NoticeEnum value : NoticeEnum.values()) {
            if (value.ordinal() == type) {
                return value;
            }
        }
        return WEB_HOOk;
    }
}
