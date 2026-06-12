package com.tyut.psychological.interviewer.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.interviewer.dto.InterviewResultRequest;
import com.tyut.psychological.interviewer.service.InterviewerService;
import com.tyut.psychological.interviewer.vo.InterviewTaskDetailVO;
import com.tyut.psychological.interviewer.vo.InterviewTaskVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 初访员控制器
 * 提供初访任务列表、详情、提交初访结果等接口
 */
@RestController
@RequestMapping("/api/interviewer")
public class InterviewerController {
    
    private final InterviewerService interviewerService;
    
    public InterviewerController(InterviewerService interviewerService) {
        this.interviewerService = interviewerService;
    }
    
    /**
     * 获取初访任务分页列表
     * 只能查看分配给自己的任务
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param request HTTP请求
     * @return 分页结果
     */
    @GetMapping("/tasks")
    public Result<PageResult<InterviewTaskVO>> pageInterviewTasks(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        // 校验当前用户必须是初访员角色
        Long interviewerId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.INTERVIEWER);
        return Result.success(interviewerService.pageInterviewTasks(interviewerId, startDate, endDate, status, riskLevel, pageNum, pageSize));
    }
    
    /**
     * 获取初访任务详情
     * 包含学生信息、登记表摘要、预约信息
     * @param appointmentId 预约ID
     * @param request HTTP请求
     * @return 任务详情
     */
    @GetMapping("/tasks/{appointmentId}")
    public Result<InterviewTaskDetailVO> getInterviewTaskDetail(
            @PathVariable Long appointmentId,
            HttpServletRequest request) {
        // 校验当前用户必须是初访员角色
        Long interviewerId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.INTERVIEWER);
        return Result.success(interviewerService.getInterviewTaskDetail(appointmentId, interviewerId));
    }
    
    /**
     * 提交初访结果
     * 校验预约状态必须为APPROVED，校验初访员归属
     * 写入first_visit_result，更新预约状态为COMPLETED
     * 若结论为ARRANGE_CONSULTATION，创建咨询队列
     * @param appointmentId 预约ID
     * @param request 初访结果请求
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PostMapping("/tasks/{appointmentId}/result")
    public Result<Void> submitInterviewResult(
            @PathVariable Long appointmentId,
            @Valid @RequestBody InterviewResultRequest request,
            HttpServletRequest httpRequest) {
        // 校验当前用户必须是初访员角色
        Long interviewerId = SessionUtils.getRequiredCurrentUser(httpRequest).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.INTERVIEWER);
        interviewerService.submitInterviewResult(appointmentId, interviewerId, request);
        return Result.success();
    }
}