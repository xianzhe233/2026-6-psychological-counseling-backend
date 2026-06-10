package com.tyut.psychological.common.log.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.log.dto.OperationLogQuery;
import com.tyut.psychological.common.log.service.AdminLogService;
import com.tyut.psychological.common.log.vo.OperationLogVO;
import com.tyut.psychological.common.notification.dto.NotificationLogQuery;
import com.tyut.psychological.common.notification.vo.NotificationLogVO;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {
    private final AdminLogService adminLogService;

    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }

    @GetMapping("/notifications")
    public Result<PageResult<NotificationLogVO>> pageNotificationLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String notifyType,
            @RequestParam(required = false) String sendStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        NotificationLogQuery query = new NotificationLogQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setKeyword(keyword);
        query.setNotifyType(notifyType);
        query.setSendStatus(sendStatus);
        query.setStartTime(toStartDateTime(startTime));
        query.setEndTime(toEndDateTime(endTime));
        return Result.success(adminLogService.pageNotificationLogs(query));
    }

    @GetMapping("/operations")
    public Result<PageResult<OperationLogVO>> pageOperationLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String resultStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        OperationLogQuery query = new OperationLogQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setOperatorName(operatorName);
        query.setModuleName(moduleName);
        query.setOperationType(operationType);
        query.setResultStatus(resultStatus);
        query.setStartTime(toStartDateTime(startTime));
        query.setEndTime(toEndDateTime(endTime));
        return Result.success(adminLogService.pageOperationLogs(query));
    }

    private LocalDateTime toStartDateTime(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime toEndDateTime(LocalDate date) {
        return date == null ? null : date.atTime(LocalTime.MAX);
    }
}
