package com.tyut.psychological.schedule.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.common.vo.OptionVO;
import com.tyut.psychological.schedule.dto.TimeSlotSaveRequest;
import com.tyut.psychological.schedule.service.TimeSlotService;
import com.tyut.psychological.schedule.vo.TimeSlotVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/time-slots")
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    // 时间段分页查询
    @GetMapping
    public Result<PageResult<TimeSlotVO>> page(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(timeSlotService.pageTimeSlots(keyword, status, pageNum, pageSize));
    }

    // 新增时间段
    @PostMapping
    public Result<Long> create(@Valid @RequestBody TimeSlotSaveRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        return Result.success(timeSlotService.createTimeSlot(request));
    }

    // 修改时间段
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TimeSlotSaveRequest request,
                               HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        timeSlotService.updateTimeSlot(id, request);
        return Result.success();
    }

    // 时间段下拉选项（已登录用户均可访问）
    @GetMapping("/options")
    public Result<List<OptionVO>> options(HttpServletRequest request) {
        SessionUtils.getRequiredCurrentUser(request);
        return Result.success(timeSlotService.getTimeSlotOptions());
    }
}
