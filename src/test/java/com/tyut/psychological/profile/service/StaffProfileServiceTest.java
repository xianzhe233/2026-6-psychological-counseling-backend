package com.tyut.psychological.profile.service;

import com.tyut.psychological.profile.dto.StaffSaveRequest;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StaffProfileServiceTest {

    @Test
    void updateStaffShouldSyncLinkedUserAndRole() {
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        StaffProfileService staffProfileService = new StaffProfileService(staffProfileMapper, userMapper, "123456");

        StaffProfile existing = new StaffProfile();
        existing.setId(2L);
        existing.setUserId(8L);
        existing.setStaffType("INTERVIEWER");
        when(staffProfileMapper.selectById(2L)).thenReturn(existing);
        when(userMapper.selectRoleCodesByUserId(8L)).thenReturn(List.of());
        when(userMapper.selectRoleIdByCode("COUNSELOR")).thenReturn(5L);

        StaffSaveRequest request = new StaffSaveRequest();
        request.setRealName(" 李老师 ");
        request.setPhone(" 13800001111 ");
        request.setStaffNo(" C009 ");
        request.setStaffType("COUNSELOR");
        request.setTitle(" 讲师 ");
        request.setSpecialty(" 压力管理 ");
        request.setIntroduction(" 简介 ");
        request.setMaxDailyAppointments(6);
        request.setStatus(0);

        staffProfileService.updateStaff(2L, request);

        ArgumentCaptor<StaffProfile> profileCaptor = ArgumentCaptor.forClass(StaffProfile.class);
        verify(staffProfileMapper).update(profileCaptor.capture());
        StaffProfile updatedProfile = profileCaptor.getValue();
        assertEquals("C009", updatedProfile.getStaffNo());
        assertEquals("COUNSELOR", updatedProfile.getStaffType());
        assertEquals("讲师", updatedProfile.getTitle());
        assertEquals(0, updatedProfile.getStatus());

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).update(userCaptor.capture());
        SysUser updatedUser = userCaptor.getValue();
        assertEquals(8L, updatedUser.getId());
        assertEquals("李老师", updatedUser.getRealName());
        assertEquals("13800001111", updatedUser.getPhone());
        assertEquals(0, updatedUser.getStatus());

        verify(userMapper).deleteUserRoleByRoleCode(8L, "INTERVIEWER");
        verify(userMapper).insertUserRole(8L, 5L);
    }
}
