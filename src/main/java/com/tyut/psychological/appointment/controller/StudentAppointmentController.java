package com.tyut.psychological.appointment.controller;

import com.tyut.psychological.appointment.dto.StudentAppointmentCreateRequest;
import com.tyut.psychological.appointment.dto.StudentAppointmentCancelRequest;
import com.tyut.psychological.appointment.service.FirstVisitAppointmentService;
import com.tyut.psychological.appointment.vo.AvailableSlotVO;
import com.tyut.psychological.appointment.vo.StudentAppointmentVO;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.schedule.service.DutyScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 学生预约控制器
 * 提供学生预约提交、列表、详情、取消等接口
 */
@RestController
@RequestMapping("/api/student/appointments")
public class StudentAppointmentController {
    private final FirstVisitAppointmentService appointmentService;
    private final DutyScheduleService dutyScheduleService;

    public StudentAppointmentController(FirstVisitAppointmentService appointmentService,
                                       DutyScheduleService dutyScheduleService) {
        this.appointmentService = appointmentService;
        this.dutyScheduleService = dutyScheduleService;
    }

    /**
     * 查询可预约时间段
     * @param date 日期
     * @param interviewerId 初访员ID（可选）
     * @param httpRequest HTTP请求
     * @return 可预约时间段列表
     */
    @GetMapping("/available-slots")
    public Result<List<AvailableSlotVO>> getAvailableSlots(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) Long interviewerId,
            HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        SessionUtils.requireAnyRole(currentUser, RoleCode.STUDENT);
        return Result.success(dutyScheduleService.getAvailableSlots(date, interviewerId));
    }

    /**
     * 学生提交预约
     * @param request 预约创建请求
     * @param httpRequest HTTP请求
     * @return 预约信息
     */
    @PostMapping
    public Result<StudentAppointmentVO> createAppointment(@Valid @RequestBody StudentAppointmentCreateRequest request,
                                                         HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        SessionUtils.requireAnyRole(currentUser, RoleCode.STUDENT);
        return Result.success(appointmentService.createStudentAppointment(currentUser.getId(), request));
    }

    /**
     * 学生预约列表
     * @param status 预约状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param httpRequest HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<StudentAppointmentVO>> pageAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        SessionUtils.requireAnyRole(currentUser, RoleCode.STUDENT);
        return Result.success(appointmentService.pageStudentAppointments(currentUser.getId(), status, pageNum, pageSize));
    }

    /**
     * 学生预约详情
     * @param id 预约ID
     * @param httpRequest HTTP请求
     * @return 预约详情
     */
    @GetMapping("/{id}")
    public Result<StudentAppointmentVO> getAppointmentDetail(@PathVariable Long id,
                                                            HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        SessionUtils.requireAnyRole(currentUser, RoleCode.STUDENT);
        return Result.success(appointmentService.getStudentAppointmentDetail(id, currentUser.getId()));
    }

    /**
     * 学生取消预约
     * @param id 预约ID
     * @param request 取消请求
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelAppointment(@PathVariable Long id,
                                         @Valid @RequestBody StudentAppointmentCancelRequest request,
                                         HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        SessionUtils.requireAnyRole(currentUser, RoleCode.STUDENT);
        appointmentService.cancelStudentAppointment(id, currentUser.getId(), request);
        return Result.success();
    }
}