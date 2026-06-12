package com.tyut.psychological.report.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.DownloadUtils;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.report.dto.CaseReportQuery;
import com.tyut.psychological.report.dto.CaseReportRequest;
import com.tyut.psychological.report.service.CaseReportExportService;
import com.tyut.psychological.report.service.CaseReportService;
import com.tyut.psychological.report.vo.CaseReportExportVO;
import com.tyut.psychological.report.vo.CaseReportVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/counselor/case-reports")
public class CounselorCaseReportController {
    private final CaseReportService caseReportService;
    private final CaseReportExportService caseReportExportService;

    public CounselorCaseReportController(CaseReportService caseReportService,
                                         CaseReportExportService caseReportExportService) {
        this.caseReportService = caseReportService;
        this.caseReportExportService = caseReportExportService;
    }

    @GetMapping
    public Result<PageResult<CaseReportVO>> pageReports(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String studentKeyword,
            HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        CaseReportQuery query = new CaseReportQuery();
        query.setPageNum(pageNum); query.setPageSize(pageSize);
        query.setStatus(status); query.setStudentKeyword(studentKeyword);
        return Result.success(caseReportService.pageForCounselor(counselorUserId, query));
    }

    @GetMapping("/{id}")
    public Result<CaseReportVO> getReport(@PathVariable Long id, HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(caseReportService.getDetailForCounselor(counselorUserId, id));
    }

    @PostMapping
    public Result<CaseReportVO> saveReport(@Valid @RequestBody CaseReportRequest saveRequest,
                                           HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(caseReportService.save(counselorUserId, saveRequest));
    }

    @PutMapping("/{id}")
    public Result<CaseReportVO> updateReport(@PathVariable Long id,
                                             @Valid @RequestBody CaseReportRequest updateRequest,
                                             HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(caseReportService.update(counselorUserId, id, updateRequest));
    }

    @PostMapping("/{id}/submit")
    public Result<CaseReportVO> submitReport(@PathVariable Long id, HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(caseReportService.submit(counselorUserId, id));
    }

    @GetMapping("/{id}/export-word")
    public void exportWord(@PathVariable Long id,
                           HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        CaseReportExportVO report = caseReportExportService.fetchForCounselor(counselorUserId, id);
        byte[] word = caseReportExportService.buildWord(report);
        String fileName = (report.getStudentName() != null ? report.getStudentName() : "学生") + "-结案报告.docx";
        DownloadUtils.writeAttachment(response, fileName, word);
        caseReportExportService.logExport("咨询师", id, report.getStudentName());
    }
}
