package com.tyut.psychological.auth.service;

import com.tyut.psychological.auth.dto.LoginRequest;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.PasswordUtils;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserMapper userMapper;

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 登录：从数据库查询用户，校验密码和状态
    public CurrentUserVO login(LoginRequest request, HttpSession session) {
        SysUser user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (!PasswordUtils.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }
        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());
        // 构建当前用户信息
        CurrentUserVO currentUser = buildCurrentUser(user);
        session.setAttribute(SessionUtils.LOGIN_USER, currentUser);
        return currentUser;
    }

    // 从数据库用户构建 CurrentUserVO
    public CurrentUserVO buildCurrentUser(SysUser user) {
        CurrentUserVO vo = new CurrentUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        List<String> roleCodes = userMapper.selectRoleCodesByUserId(user.getId());
        List<RoleCode> roles = roleCodes.stream()
                .map(RoleCode::valueOf)
                .collect(Collectors.toList());
        vo.setRoles(roles);
        if (!roles.isEmpty()) {
            vo.setPrimaryRole(roles.get(0));
        }
        return vo;
    }
}
