package com.tyut.psychological.appointment.controller;

import com.tyut.psychological.appointment.dto.ApproveRequest;
import com.tyut.psychological.appointment.dto.RejectRequest;
import com.tyut.psychological.appointment.service.FirstVisitAppointmentService;
import com.tyut.psychological.appointment.vo.AppointmentAuditVO;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 初访预约审核控制器
 * 提供预约审核列表、详情、通过、驳回、改约、优先标记等接口
 */
@RestController
@RequestMapping("/api/admin/first-visit/appointments")
public class FirstVisitAppointmentController {
    private final FirstVisitAppointmentService appointmentService;

    public FirstVisitAppointmentController(FirstVisitAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * 审核列表
     * @param keyword 关键词
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param priorityFlag 优先标记
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param request HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<AppointmentAuditVO>> pageAuditList(@RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) String status,
                                                               @RequestParam(required = false) String riskLevel,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                               @RequestParam(required = false) Integer priorityFlag,
                                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(appointmentService.pageAuditList(keyword, status, riskLevel, startDate, endDate, priorityFlag, pageNum, pageSize));
    }

    /**
     * 预约详情
     * @param id 预约ID
     * @param request HTTP请求
     * @return 预约详情
     */
    @GetMapping("/{id}")
    public Result<AppointmentAuditVO> getAppointmentDetail(@PathVariable Long id, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(appointmentService.getAppointmentDetail(id));
    }

    /**
     * 审核通过
     * @param id 预约ID
     * @param approveRequest 审核通过请求
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approveAppointment(@PathVariable Long id, 
                                          @Valid @RequestBody ApproveRequest approveRequest,
                                          HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        appointmentService.approveAppointment(id, approveRequest);
        return Result.success();
    }

    /**
     * 驳回预约
     * @param id 预约ID
     * @param rejectRequest 驳回请求
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/reject")
    public Result<Void> rejectAppointment(@PathVariable Long id,
                                         @Valid @RequestBody RejectRequest rejectRequest,
                                         HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        appointmentService.rejectAppointment(id, rejectRequest);
        return Result.success();
    }

    /**
     * 改约
     * @param id 预约ID
     * @param rescheduleRequest 改约请求（复用审核通过请求）
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/reschedule")
    public Result<Void> rescheduleAppointment(@PathVariable Long id,
                                             @Valid @RequestBody ApproveRequest rescheduleRequest,
                                             HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        appointmentService.rescheduleAppointment(id, rescheduleRequest);
        return Result.success();
    }

    /**
     * 标记优先
     * @param id 预约ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/priority")
    public Result<Void> markPriority(@PathVariable Long id, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        appointmentService.markPriority(id);
        return Result.success();
    }
}