package com.tyut.psychological.auth.service;

import com.tyut.psychological.auth.dto.LoginRequest;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.PasswordUtils;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    private static final Map<String, TemporaryUser> TEMP_USERS = Map.of(
            "admin", new TemporaryUser(1L, "admin", "中心管理员", "13800000000", List.of(RoleCode.ADMIN), RoleCode.ADMIN, PasswordUtils.hash("123456")),
            "20230001", new TemporaryUser(2L, "20230001", "学生示例", "13800000001", List.of(RoleCode.STUDENT), RoleCode.STUDENT, PasswordUtils.hash("123456")),
            "interviewer", new TemporaryUser(3L, "interviewer", "初访员示例", "13800000002", List.of(RoleCode.INTERVIEWER), RoleCode.INTERVIEWER, PasswordUtils.hash("123456")),
            "assistant", new TemporaryUser(4L, "assistant", "心理助理示例", "13800000003", List.of(RoleCode.ASSISTANT), RoleCode.ASSISTANT, PasswordUtils.hash("123456")),
            "counselor", new TemporaryUser(5L, "counselor", "咨询师示例", "13800000004", List.of(RoleCode.COUNSELOR), RoleCode.COUNSELOR, PasswordUtils.hash("123456"))
    );

    public CurrentUserVO login(LoginRequest request, HttpSession session) {
        TemporaryUser user = TEMP_USERS.get(request.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (!PasswordUtils.matches(request.getPassword(), user.passwordHash())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        CurrentUserVO currentUser = buildCurrentUser(user);
        session.setAttribute(SessionUtils.LOGIN_USER, currentUser);
        return currentUser;
    }

    public CurrentUserVO buildCurrentUser(TemporaryUser user) {
        CurrentUserVO vo = new CurrentUserVO();
        vo.setId(user.id());
        vo.setUsername(user.username());
        vo.setRealName(user.realName());
        vo.setPhone(user.phone());
        vo.setRoles(user.roles());
        vo.setPrimaryRole(user.primaryRole());
        return vo;
    }

    private record TemporaryUser(Long id, String username, String realName, String phone,
                                 List<RoleCode> roles, RoleCode primaryRole, String passwordHash) {
    }
}
