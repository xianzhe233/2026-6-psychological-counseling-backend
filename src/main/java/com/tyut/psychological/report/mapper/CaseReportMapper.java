package com.tyut.psychological.report.mapper;

import com.tyut.psychological.report.dto.CaseReportAdminQuery;
import com.tyut.psychological.report.dto.CaseReportQuery;
import com.tyut.psychological.report.entity.CaseReport;
import com.tyut.psychological.report.vo.CaseReportExportVO;
import com.tyut.psychological.report.vo.CaseReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CaseReportMapper {

    CaseReport selectById(@Param("id") Long id);

    int insert(CaseReport report);

    int update(CaseReport report);

    int submit(@Param("id") Long id);

    List<CaseReportVO> pageForCounselor(@Param("query") CaseReportQuery query);

    long countForCounselor(@Param("query") CaseReportQuery query);

    CaseReportVO selectDetailForCounselor(@Param("id") Long id, @Param("counselorId") Long counselorId);

    List<CaseReportVO> pageForAdmin(@Param("query") CaseReportAdminQuery query);

    long countForAdmin(@Param("query") CaseReportAdminQuery query);

    CaseReportVO selectDetailForAdmin(@Param("id") Long id);

    long countCounselorStudentRelation(@Param("counselorId") Long counselorId, @Param("studentId") Long studentId);

    CaseReportExportVO selectExportForCounselor(@Param("id") Long id, @Param("counselorId") Long counselorId);

    CaseReportExportVO selectExportForAdmin(@Param("id") Long id);
}
