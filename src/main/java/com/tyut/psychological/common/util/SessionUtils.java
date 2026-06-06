package com.tyut.psychological.common.util;

import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;

public final class SessionUtils {
    public static final String LOGIN_USER = "LOGIN_USER";

    private SessionUtils() {
    }

    public static CurrentUserVO getCurrentUser(HttpSession session) {
        Object value = session.getAttribute(LOGIN_USER);
        if (value instanceof CurrentUserVO user) {
            return user;
        }
        return null;
    }

    public static CurrentUserVO getRequiredCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(401, "请先登录");
        }
        CurrentUserVO user = getCurrentUser(session);
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }
        return user;
    }

    public static void requireAnyRole(CurrentUserVO user, RoleCode... roles) {
        boolean matched = Arrays.stream(roles).anyMatch(role -> user.getRoles().contains(role));
        if (!matched) {
            throw new BusinessException(403, "当前角色无权访问");
        }
    }
}
