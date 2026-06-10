package com.tyut.psychological.common.log.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.log.dto.OperationLogQuery;
import com.tyut.psychological.common.log.mapper.OperationLogMapper;
import com.tyut.psychological.common.log.vo.OperationLogVO;
import com.tyut.psychological.common.notification.dto.NotificationLogQuery;
import com.tyut.psychological.common.notification.mapper.NotificationLogMapper;
import com.tyut.psychological.common.notification.vo.NotificationLogVO;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.ceil;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {
    private final NotificationLogMapper notificationLogMapper;
    private final OperationLogMapper operationLogMapper;

    public AdminLogController(NotificationLogMapper notificationLogMapper,
                              OperationLogMapper operationLogMapper) {
        this.notificationLogMapper = notificationLogMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @GetMapping("/notifications")
    public Result<PageResult<NotificationLogVO>> pageNotificationLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String notifyType,
            @RequestParam(required = false) String sendStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        NotificationLogQuery q = new NotificationLogQuery();
        q.setPageNum(pageNum); q.setPageSize(pageSize);
        q.setNotifyType(notifyType); q.setSendStatus(sendStatus);
        q.setKeyword(keyword); q.setStartTime(startTime); q.setEndTime(endTime);
        List<NotificationLogVO> list = notificationLogMapper.pageForAdmin(q);
        long total = notificationLogMapper.countForAdmin(q);
        long pages = pageSize == null || pageSize <= 0 ? 0 : (long) ceil((double) total / pageSize);
        return Result.success(new PageResult<>(list, total, pageNum, pageSize, pages));
    }

    @GetMapping("/operations")
    public Result<PageResult<OperationLogVO>> pageOperationLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String resultStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        OperationLogQuery q = new OperationLogQuery();
        q.setPageNum(pageNum); q.setPageSize(pageSize);
        q.setOperationType(operationType); q.setResultStatus(resultStatus);
        q.setKeyword(keyword); q.setStartTime(startTime); q.setEndTime(endTime);
        List<OperationLogVO> list = operationLogMapper.pageForAdmin(q);
        long total = operationLogMapper.countForAdmin(q);
        long pages = pageSize == null || pageSize <= 0 ? 0 : (long) ceil((double) total / pageSize);
        return Result.success(new PageResult<>(list, total, pageNum, pageSize, pages));
    }
}
