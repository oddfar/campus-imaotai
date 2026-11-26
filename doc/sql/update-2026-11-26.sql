-- 通知渠道扩展：钉钉 + 邮件 (#380)
-- 在 campus_imaotai-1.0.5.sql 基础上执行

ALTER TABLE `i_user`
    ADD COLUMN `ding_webhook` varchar(500) DEFAULT NULL COMMENT '钉钉机器人webhook',
    ADD COLUMN `ding_secret` varchar(128) DEFAULT NULL COMMENT '钉钉加签密钥',
    ADD COLUMN `mail_addr` varchar(128) DEFAULT NULL COMMENT '通知收件邮箱';
