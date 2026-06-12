package com.tyut.psychological.user.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.user.dto.UserQuery;
import com.tyut.psychological.user.dto.UserSaveRequest;
import com.tyut.psychological.user.service.UserService;
import com.tyut.psychological.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 用户分页查询，仅管理员可访问
    @GetMapping
    public Result<PageResult<UserVO>> page(UserQuery query, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(userService.pageUsers(query));
    }

    // 新增用户
    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserSaveRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        return Result.success(userService.createUser(request));
    }

    // 修改用户
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserSaveRequest request,
                               HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        userService.updateUser(id, request);
        return Result.success();
    }

    // 启用用户
    @PostMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        userService.enableUser(id);
        return Result.success();
    }

    // 禁用用户
    @PostMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id, HttpServletRequest request) {
        var currentUser = SessionUtils.getRequiredCurrentUser(request);
        SessionUtils.requireAnyRole(currentUser, RoleCode.ADMIN);
        userService.disableUser(id, currentUser.getId());
        return Result.success();
    }

    // 重置密码
    @PostMapping("/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(userService.resetPassword(id));
    }
}
