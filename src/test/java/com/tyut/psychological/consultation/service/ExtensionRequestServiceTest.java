package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.consultation.dto.ExtensionAdminQuery;
import com.tyut.psychological.consultation.mapper.ExtensionRequestMapper;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExtensionRequestServiceTest {

    @Test
    void rejectShouldRequireReason() {
        ExtensionRequestMapper extensionRequestMapper = mock(ExtensionRequestMapper.class);
        CounselorAccessService counselorAccessService = mock(CounselorAccessService.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        UserMapper userMapper = mock(UserMapper.class);

        ExtensionRequestService service = new ExtensionRequestService(
                extensionRequestMapper,
                counselorAccessService,
                notificationLogService,
                operationLogService,
                staffProfileMapper,
                userMapper);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.reject(1L, 2L, "   "));
        assertEquals(400, exception.getCode());
        assertEquals("驳回原因不能为空", exception.getMessage());
        verify(extensionRequestMapper, never()).selectById(any());
    }

    @Test
    void pageForAdminShouldNormalizeInvalidPageParams() {
        ExtensionRequestMapper extensionRequestMapper = mock(ExtensionRequestMapper.class);
        CounselorAccessService counselorAccessService = mock(CounselorAccessService.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        UserMapper userMapper = mock(UserMapper.class);

        ExtensionRequestService service = new ExtensionRequestService(
                extensionRequestMapper,
                counselorAccessService,
                notificationLogService,
                operationLogService,
                staffProfileMapper,
                userMapper);

        when(extensionRequestMapper.pageForAdmin(any())).thenReturn(List.of());
        when(extensionRequestMapper.countForAdmin(any())).thenReturn(0L);

        ExtensionAdminQuery query = new ExtensionAdminQuery();
        query.setPageNum(0);
        query.setPageSize(-3);

        var pageResult = service.pageForAdmin(query);
        assertEquals(1, pageResult.getPageNum());
        assertEquals(10, pageResult.getPageSize());
        assertEquals(0, pageResult.getTotal());
        assertEquals(0, query.getOffset());
    }
}
