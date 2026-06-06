package com.tyut.psychological.auth.controller;

import com.tyut.psychological.auth.dto.LoginRequest;
import com.tyut.psychological.auth.service.AuthService;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<CurrentUserVO> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        return Result.success(authService.login(request, session));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @GetMapping("/current")
    public Result<CurrentUserVO> current(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(401, "请先登录");
        }
        CurrentUserVO user = SessionUtils.getCurrentUser(session);
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(user);
    }
}
