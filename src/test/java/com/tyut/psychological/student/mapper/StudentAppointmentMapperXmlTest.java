package com.tyut.psychological.student.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentAppointmentMapperXmlTest {

    @Test
    void studentNotificationsQueryShouldStayConsistentWithRepositorySchema() throws IOException {
        String xml = Files.readString(Path.of("src/main/resources/mapper/student/StudentAppointmentMapper.xml"));

        assertTrue(xml.contains("send_status"), "通知查询应包含 send_status 字段");
        assertTrue(xml.contains("send_time"), "通知查询应包含 send_time 字段");
        assertTrue(xml.contains("related_id"), "通知查询应包含 related_id 字段");
        assertTrue(xml.contains("receiver_user_id = #{studentId}"), "通知查询应按 receiver_user_id 过滤");
        assertTrue(!xml.contains("WHERE user_id = #{studentId}"), "通知查询不应回退为不存在的 user_id 字段");
    }
}
