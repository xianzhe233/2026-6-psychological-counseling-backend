package com.tyut.psychological.schedule.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.schedule.dto.DutyScheduleSaveRequest;
import com.tyut.psychological.schedule.entity.DutySchedule;
import com.tyut.psychological.schedule.mapper.DutyScheduleMapper;
import com.tyut.psychological.schedule.vo.DutyScheduleVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DutyScheduleServiceTest {

    @Test
    void pageDutySchedulesShouldNormalizeInvalidPageParams() {
        DutyScheduleMapper dutyScheduleMapper = mock(DutyScheduleMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        DutyScheduleService service = new DutyScheduleService(dutyScheduleMapper, staffProfileMapper, operationLogService);

        when(dutyScheduleMapper.pageDutySchedules(null, null, null, null, null)).thenReturn(List.of(new DutyScheduleVO()));
        when(dutyScheduleMapper.countDutySchedules(null, null, null, null, null)).thenReturn(1L);

        var result = service.pageDutySchedules(null, null, null, null, null, 0, -5);
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void createDutyScheduleShouldRejectConflict() {
        DutyScheduleMapper dutyScheduleMapper = mock(DutyScheduleMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        DutyScheduleService service = new DutyScheduleService(dutyScheduleMapper, staffProfileMapper, operationLogService);

        DutyScheduleSaveRequest request = createRequest();
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStatus(1);
        when(staffProfileMapper.selectById(1L)).thenReturn(staffProfile);
        when(dutyScheduleMapper.checkConflict(1L, request.getDutyDate(), request.getSlotId(), null)).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.createDutySchedule(request));
        assertEquals(409, exception.getCode());
        assertEquals("该工作人员在该时间段已有值班安排", exception.getMessage());
        verify(dutyScheduleMapper, never()).insert(any());
    }

    @Test
    void updateDutyScheduleShouldRejectCapacityBelowReservedCount() {
        DutyScheduleMapper dutyScheduleMapper = mock(DutyScheduleMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        DutyScheduleService service = new DutyScheduleService(dutyScheduleMapper, staffProfileMapper, operationLogService);

        DutyScheduleSaveRequest request = createRequest();
        request.setCapacity(1);

        DutySchedule existing = new DutySchedule();
        existing.setId(10L);
        existing.setReservedCount(2);
        when(dutyScheduleMapper.selectById(10L)).thenReturn(existing);

        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStatus(1);
        when(staffProfileMapper.selectById(1L)).thenReturn(staffProfile);
        when(dutyScheduleMapper.checkConflict(1L, request.getDutyDate(), request.getSlotId(), 10L)).thenReturn(0L);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.updateDutySchedule(10L, request));
        assertEquals(400, exception.getCode());
        assertEquals("容量不能小于已预约数", exception.getMessage());
        verify(dutyScheduleMapper, never()).update(any());
    }

    @Test
    void incrementReservedCountShouldRejectWhenCapacityFull() {
        DutyScheduleMapper dutyScheduleMapper = mock(DutyScheduleMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        DutyScheduleService service = new DutyScheduleService(dutyScheduleMapper, staffProfileMapper, operationLogService);

        DutySchedule schedule = new DutySchedule();
        schedule.setId(5L);
        schedule.setCapacity(2);
        schedule.setReservedCount(2);
        when(dutyScheduleMapper.selectById(5L)).thenReturn(schedule);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.incrementReservedCount(5L, 1));
        assertEquals(400, exception.getCode());
        assertEquals("值班容量已满", exception.getMessage());
        verify(dutyScheduleMapper, never()).incrementReservedCount(any(), eq(1));
    }

    private DutyScheduleSaveRequest createRequest() {
        DutyScheduleSaveRequest request = new DutyScheduleSaveRequest();
        request.setStaffId(1L);
        request.setStaffType("INTERVIEWER");
        request.setDutyDate(LocalDate.of(2026, 6, 11));
        request.setSlotId(2L);
        request.setRoomId(3L);
        request.setCapacity(2);
        request.setStatus(1);
        return request;
    }
}
