package com.tyut.psychological.report.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.DownloadUtils;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.report.dto.CaseReportAdminQuery;
import com.tyut.psychological.report.service.CaseReportExportService;
import com.tyut.psychological.report.service.CaseReportService;
import com.tyut.psychological.report.vo.CaseReportExportVO;
import com.tyut.psychological.report.vo.CaseReportVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/case-reports")
public class AdminCaseReportController {
    private final CaseReportService caseReportService;
    private final CaseReportExportService caseReportExportService;

    public AdminCaseReportController(CaseReportService caseReportService,
                                     CaseReportExportService caseReportExportService) {
        this.caseReportService = caseReportService;
        this.caseReportExportService = caseReportExportService;
    }

    @GetMapping
    public Result<PageResult<CaseReportVO>> pageReports(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String studentKeyword,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long problemTypeId,
            @RequestParam(required = false) String closeType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        CaseReportAdminQuery query = new CaseReportAdminQuery();
        query.setPageNum(pageNum); query.setPageSize(pageSize);
        query.setStudentKeyword(studentKeyword); query.setCounselorId(counselorId);
        query.setProblemTypeId(problemTypeId); query.setCloseType(closeType);
        query.setStartDate(startDate); query.setEndDate(endDate);
        return Result.success(caseReportService.pageForAdmin(query));
    }

    @GetMapping("/{id}")
    public Result<CaseReportVO> getReport(@PathVariable Long id, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(caseReportService.getDetailForAdmin(id));
    }

    @GetMapping("/{id}/export-word")
    public void exportWord(@PathVariable Long id,
                           HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        CaseReportExportVO report = caseReportExportService.fetchForAdmin(id);
        byte[] word = caseReportExportService.buildWord(report);
        String fileName = (report.getStudentName() != null ? report.getStudentName() : "学生") + "-结案报告.docx";
        DownloadUtils.writeAttachment(response, fileName, word);
        caseReportExportService.logExport("管理员", id, report.getStudentName());
    }
}
