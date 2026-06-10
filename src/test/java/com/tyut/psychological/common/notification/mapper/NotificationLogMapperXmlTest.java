package com.tyut.psychological.common.notification.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationLogMapperXmlTest {

    @Test
    void notificationLogMapperShouldUseRepositoryNotificationSchema() throws IOException {
        String xml = Files.readString(Path.of("src/main/resources/mapper/common/NotificationLogMapper.xml"));

        assertTrue(xml.contains("receiver_user_id"), "通知日志应使用 receiver_user_id 字段");
        assertTrue(xml.contains("send_status"), "通知日志应写入 send_status 字段");
        assertTrue(xml.contains("send_time"), "通知日志应写入 send_time 字段");
        assertTrue(xml.contains("related_id"), "通知日志应写入 related_id 字段");
        assertTrue(!xml.contains("INSERT INTO notification_log (\n            user_id"), "通知日志插入不应回退为旧 user_id 字段");
        assertTrue(!xml.contains("WHERE user_id = #{studentId}"), "通知日志查询不应回退为旧 user_id 字段");
    }
}
