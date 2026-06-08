package com.tyut.psychological.consultation.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.consultation.dto.ArrangeConsultationRequest;
import com.tyut.psychological.consultation.dto.CancelScheduleRequest;
import com.tyut.psychological.consultation.dto.ConsultationQueueQuery;
import com.tyut.psychological.consultation.dto.ScheduleQuery;
import com.tyut.psychological.consultation.dto.SuspendQueueRequest;
import com.tyut.psychological.consultation.service.ConsultationQueueService;
import com.tyut.psychological.consultation.service.ConsultationScheduleService;
import com.tyut.psychological.consultation.vo.ArrangeResultVO;
import com.tyut.psychological.consultation.vo.AvailableSlotVO;
import com.tyut.psychological.consultation.vo.ConsultationQueueDetailVO;
import com.tyut.psychological.consultation.vo.ConsultationQueueVO;
import com.tyut.psychological.consultation.vo.ConsultationScheduleVO;
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
import java.util.List;

@RestController
@RequestMapping("/api/assistant")
public class AssistantConsultationController {
    private final ConsultationQueueService consultationQueueService;
    private final ConsultationScheduleService consultationScheduleService;

    public AssistantConsultationController(ConsultationQueueService consultationQueueService,
                                           ConsultationScheduleService consultationScheduleService) {
        this.consultationQueueService = consultationQueueService;
        this.consultationScheduleService = consultationScheduleService;
    }

    @GetMapping("/consultation/queue")
    public Result<PageResult<ConsultationQueueVO>> pageQueue(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String crisisLevel,
            @RequestParam(required = false) Long problemTypeId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        ConsultationQueueQuery query = new ConsultationQueueQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setKeyword(keyword);
        query.setCrisisLevel(crisisLevel);
        query.setProblemTypeId(problemTypeId);
        query.setStatus(status);
        return Result.success(consultationQueueService.pageQueue(query));
    }

    @GetMapping("/consultation/queue/{id}")
    public Result<ConsultationQueueDetailVO> getQueueDetail(@PathVariable Long id, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        return Result.success(consultationQueueService.getDetail(id));
    }

    @PostMapping("/consultation/queue/{id}/suspend")
    public Result<Void> suspendQueue(@PathVariable Long id,
                                     @RequestBody(required = false) SuspendQueueRequest suspendRequest,
                                     HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        String reason = suspendRequest != null ? suspendRequest.getReason() : null;
        consultationQueueService.suspend(id, reason);
        return Result.success();
    }

    @GetMapping("/counselors/available-slots")
    public Result<List<AvailableSlotVO>> getCounselorAvailableSlots(
            @RequestParam Long counselorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        return Result.success(consultationScheduleService.getCounselorAvailableSlots(counselorId, startDate));
    }

    @PostMapping("/consultation/schedules")
    public Result<ArrangeResultVO> arrange(@Valid @RequestBody ArrangeConsultationRequest arrangeRequest,
                                           HttpServletRequest request) {
        Long assistantUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        return Result.success(consultationScheduleService.arrange(assistantUserId, arrangeRequest));
    }

    @GetMapping("/consultation/schedules")
    public Result<PageResult<ConsultationScheduleVO>> pageSchedules(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String studentKeyword,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        ScheduleQuery query = new ScheduleQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setStudentKeyword(studentKeyword);
        query.setCounselorId(counselorId);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setStatus(status);
        return Result.success(consultationScheduleService.pageForAssistant(query));
    }

    @PostMapping("/consultation/schedules/{id}/cancel")
    public Result<Void> cancelSchedule(@PathVariable Long id,
                                       @Valid @RequestBody CancelScheduleRequest cancelRequest,
                                       HttpServletRequest request) {
        Long assistantUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ASSISTANT);
        consultationScheduleService.cancel(assistantUserId, id, cancelRequest.getReason());
        return Result.success();
    }
}
