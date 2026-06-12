package com.tyut.psychological.schedule.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.schedule.dto.BatchScheduleRequest;
import com.tyut.psychological.schedule.dto.BatchScheduleResponse;
import com.tyut.psychological.schedule.dto.DutyScheduleSaveRequest;
import com.tyut.psychological.schedule.service.DutyScheduleService;
import com.tyut.psychological.schedule.vo.DutyScheduleVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 值班安排控制器
 * 提供值班安排的增删改查接口
 */
@RestController
@RequestMapping("/api/admin/duty-schedules")
public class DutyScheduleController {
    private final DutyScheduleService dutyScheduleService;

    public DutyScheduleController(DutyScheduleService dutyScheduleService) {
        this.dutyScheduleService = dutyScheduleService;
    }

    /**
     * 值班分页查询
     * @param staffType 工作人员类型
     * @param staffId 工作人员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param request HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<DutyScheduleVO>> page(@RequestParam(required = false) String staffType,
                                                  @RequestParam(required = false) Long staffId,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                  @RequestParam(required = false) Integer status,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(dutyScheduleService.pageDutySchedules(staffType, staffId, startDate, endDate, status, pageNum, pageSize));
    }

    /**
     * 新增值班安排
     * @param request 值班安排请求
     * @param httpRequest HTTP请求
     * @return 值班安排ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody DutyScheduleSaveRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        return Result.success(dutyScheduleService.createDutySchedule(request));
    }

    /**
     * 修改值班安排
     * @param id 值班安排ID
     * @param request 值班安排请求
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DutyScheduleSaveRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        dutyScheduleService.updateDutySchedule(id, request);
        return Result.success();
    }

    /**
     * 批量排班
     * @param request 批量排班请求
     * @param httpRequest HTTP请求
     * @return 批量排班结果
     */
    @PostMapping("/batch")
    public Result<BatchScheduleResponse> batchCreate(@Valid @RequestBody BatchScheduleRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        return Result.success(dutyScheduleService.batchCreateDutySchedules(request));
    }
}