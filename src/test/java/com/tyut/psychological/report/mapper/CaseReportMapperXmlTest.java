package com.tyut.psychological.report.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CaseReportMapperXmlTest {

    @Test
    void adminCaseReportQueryShouldResolveCounselorNameViaStaffProfile() throws IOException {
        String xml = Files.readString(Path.of("src/main/resources/mapper/report/CaseReportMapper.xml"));

        assertTrue(xml.contains("LEFT JOIN staff_profile counselor_sp ON cr.counselor_id = counselor_sp.id"), "结案报告 counselor_id 是 staff_profile ID");
        assertTrue(xml.contains("LEFT JOIN sys_user counselor_su ON counselor_sp.user_id = counselor_su.id"), "结案报告应通过 staff_profile.user_id 关联咨询师姓名");
        assertTrue(xml.contains("counselor_su.real_name AS counselorName"), "管理员结案报告查询应返回 counselorName");
    }
}
