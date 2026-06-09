package com.tyut.psychological.consultation.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.consultation.dto.CounselorScheduleQuery;
import com.tyut.psychological.consultation.dto.ExtensionCreateRequest;
import com.tyut.psychological.consultation.dto.ExtensionQuery;
import com.tyut.psychological.consultation.dto.SaveConsultationRecordRequest;
import com.tyut.psychological.consultation.service.ConsultationRecordService;
import com.tyut.psychological.consultation.service.ConsultationScheduleService;
import com.tyut.psychological.consultation.service.ExtensionRequestService;
import com.tyut.psychological.consultation.vo.ConsultationRecordVO;
import com.tyut.psychological.consultation.vo.CounselorScheduleVO;
import com.tyut.psychological.consultation.vo.ExtensionRequestVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/counselor")
public class CounselorConsultationController {
    private final ConsultationScheduleService consultationScheduleService;
    private final ConsultationRecordService consultationRecordService;
    private final ExtensionRequestService extensionRequestService;

    public CounselorConsultationController(ConsultationScheduleService consultationScheduleService,
                                           ConsultationRecordService consultationRecordService,
                                           ExtensionRequestService extensionRequestService) {
        this.consultationScheduleService = consultationScheduleService;
        this.consultationRecordService = consultationRecordService;
        this.extensionRequestService = extensionRequestService;
    }

    @GetMapping("/schedules")
    public Result<PageResult<CounselorScheduleVO>> pageSchedules(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String studentKeyword,
            HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        CounselorScheduleQuery query = new CounselorScheduleQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setStatus(status);
        query.setStudentKeyword(studentKeyword);
        return Result.success(consultationScheduleService.pageForCounselor(counselorUserId, query));
    }

    @GetMapping("/schedules/{id}")
    public Result<CounselorScheduleVO> getScheduleDetail(@PathVariable Long id, HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(consultationScheduleService.getCounselorScheduleDetail(counselorUserId, id));
    }

    @GetMapping("/schedules/{scheduleId}/record")
    public Result<ConsultationRecordVO> getRecord(@PathVariable Long scheduleId, HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(consultationRecordService.getBySchedule(counselorUserId, scheduleId));
    }

    @PostMapping("/schedules/{scheduleId}/record")
    public Result<ConsultationRecordVO> saveRecord(@PathVariable Long scheduleId,
                                                   @Valid @RequestBody SaveConsultationRecordRequest saveRequest,
                                                   HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(consultationRecordService.save(counselorUserId, scheduleId, saveRequest));
    }

    @GetMapping("/extension-requests")
    public Result<PageResult<ExtensionRequestVO>> pageExtensionRequests(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        ExtensionQuery query = new ExtensionQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setStatus(status);
        return Result.success(extensionRequestService.pageForCounselor(counselorUserId, query));
    }

    @PostMapping("/extension-requests")
    public Result<ExtensionRequestVO> createExtensionRequest(
            @Valid @RequestBody ExtensionCreateRequest createRequest,
            HttpServletRequest request) {
        Long counselorUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.COUNSELOR);
        return Result.success(extensionRequestService.create(counselorUserId, createRequest));
    }
}
