package com.tyut.psychological.user.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.PasswordUtils;
import com.tyut.psychological.user.dto.UserQuery;
import com.tyut.psychological.user.dto.UserSaveRequest;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import com.tyut.psychological.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final String defaultPassword;

    public UserService(UserMapper userMapper, @Value("${app.default-password:123456}") String defaultPassword) {
        this.userMapper = userMapper;
        this.defaultPassword = defaultPassword;
    }

    // 分页查询用户，支持关键词、角色、状态筛选
    public PageResult<UserVO> pageUsers(UserQuery query) {
        List<UserVO> records = userMapper.pageUsers(query);
        long total = userMapper.countUsers(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        // 为每个用户填充角色列表
        for (UserVO vo : records) {
            vo.setRoles(userMapper.selectRoleCodesByUserId(vo.getId()));
        }
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    // 新增用户，同时分配角色
    @Transactional
    public Long createUser(UserSaveRequest request) {
        // 检查用户名唯一
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            SysUser existing = userMapper.selectByUsername(request.getUsername());
            if (existing != null) {
                throw new BusinessException(400, "用户名已存在");
            }
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPasswordHash(PasswordUtils.hash(
                request.getPassword() != null && !request.getPassword().isEmpty()
                        ? request.getPassword() : defaultPassword));
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        userMapper.insert(user);
        // 分配角色
        if (request.getRoleCodes() != null) {
            for (String roleCode : request.getRoleCodes()) {
                Long roleId = userMapper.selectRoleIdByCode(roleCode);
                if (roleId != null) {
                    userMapper.insertUserRole(user.getId(), roleId);
                }
            }
        }
        return user.getId();
    }

    // 修改用户信息，重新分配角色
    @Transactional
    public void updateUser(Long id, UserSaveRequest request) {
        SysUser existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 检查用户名唯一（排除自身）
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            SysUser duplicate = userMapper.selectByUsername(request.getUsername());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new BusinessException(400, "用户名已存在");
            }
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(PasswordUtils.hash(request.getPassword()));
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        userMapper.update(user);
        // 重新分配角色
        if (request.getRoleCodes() != null) {
            userMapper.deleteUserRoles(id);
            for (String roleCode : request.getRoleCodes()) {
                Long roleId = userMapper.selectRoleIdByCode(roleCode);
                if (roleId != null) {
                    userMapper.insertUserRole(id, roleId);
                }
            }
        }
    }

    // 启用用户
    public void enableUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(1);
        userMapper.update(update);
    }

    // 禁用用户，不能禁用自己
    public void disableUser(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new BusinessException(400, "不能禁用当前登录用户自己");
        }
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(0);
        userMapper.update(update);
    }

    // 重置密码为系统默认密码
    public String resetPassword(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setPasswordHash(PasswordUtils.hash(defaultPassword));
        userMapper.update(update);
        return defaultPassword;
    }
}
