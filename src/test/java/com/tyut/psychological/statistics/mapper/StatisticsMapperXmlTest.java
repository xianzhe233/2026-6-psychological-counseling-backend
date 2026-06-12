package com.tyut.psychological.statistics.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsMapperXmlTest {

    @Test
    void workloadQueryShouldJoinCounselorStaffProfileBeforeUser() throws IOException {
        String xml = Files.readString(Path.of("src/main/resources/mapper/statistics/StatisticsMapper.xml"));

        assertTrue(xml.contains("LEFT JOIN staff_profile sp ON cs.counselor_id = sp.id"), "咨询安排 counselor_id 是 staff_profile ID");
        assertTrue(xml.contains("LEFT JOIN sys_user su ON sp.user_id = su.id"), "咨询师姓名应通过 staff_profile.user_id 关联 sys_user");
        assertFalse(xml.contains("LEFT JOIN sys_user su ON cs.counselor_id = su.id"), "不应把 staff_profile ID 直接当作 sys_user ID");
    }
}
