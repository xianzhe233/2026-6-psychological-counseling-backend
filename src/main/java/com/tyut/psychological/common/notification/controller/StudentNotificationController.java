package com.tyut.psychological.common.notification.controller;

import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.notification.service.StudentNotificationService;
import com.tyut.psychological.common.notification.vo.StudentNotificationVO;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 学生通知控制器
 * 提供学生通知列表接口
 */
@RestController
@RequestMapping("/api/student/notifications")
public class StudentNotificationController {
    private final StudentNotificationService studentNotificationService;

    public StudentNotificationController(StudentNotificationService studentNotificationService) {
        this.studentNotificationService = studentNotificationService;
    }

    /**
     * 学生通知列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param httpRequest HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<StudentNotificationVO>> pageNotifications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        SessionUtils.requireAnyRole(currentUser, RoleCode.STUDENT);
        return Result.success(studentNotificationService.pageStudentNotifications(currentUser.getId(), pageNum, pageSize));
    }
}