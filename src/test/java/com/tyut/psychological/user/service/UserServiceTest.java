package com.tyut.psychological.user.service;

import com.tyut.psychological.user.dto.UserSaveRequest;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void updateUserShouldPersistUsernameAndRoles() {
        UserMapper userMapper = mock(UserMapper.class);
        UserService userService = new UserService(userMapper, "123456");

        SysUser existing = new SysUser();
        existing.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(existing);
        when(userMapper.selectByUsername("new-admin")).thenReturn(null);
        when(userMapper.selectRoleIdByCode("ADMIN")).thenReturn(1L);

        UserSaveRequest request = new UserSaveRequest();
        request.setUsername(" new-admin ");
        request.setRealName(" 新管理员 ");
        request.setPhone(" 13800000011 ");
        request.setEmail(" admin@example.com ");
        request.setRoleCodes(List.of("ADMIN"));
        request.setStatus(0);

        userService.updateUser(1L, request);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).update(userCaptor.capture());
        SysUser updated = userCaptor.getValue();
        assertEquals("new-admin", updated.getUsername());
        assertEquals("新管理员", updated.getRealName());
        assertEquals("13800000011", updated.getPhone());
        assertEquals("admin@example.com", updated.getEmail());
        assertEquals(0, updated.getStatus());
        verify(userMapper).deleteUserRoles(1L);
        verify(userMapper).insertUserRole(1L, 1L);
    }
}
