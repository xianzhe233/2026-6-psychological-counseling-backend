package com.tyut.psychological.student.service;

import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.schedule.mapper.TimeSlotMapper;
import com.tyut.psychological.schedule.service.DutyScheduleService;
import com.tyut.psychological.student.mapper.StudentAppointmentMapper;
import com.tyut.psychological.student.vo.MyAppointmentVO;
import com.tyut.psychological.student.vo.MyNotificationVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentAppointmentServiceTest {

    @Test
    void getMyAppointmentsShouldNormalizeInvalidPageParams() {
        StudentAppointmentMapper studentAppointmentMapper = mock(StudentAppointmentMapper.class);
        FirstVisitAppointmentMapper firstVisitAppointmentMapper = mock(FirstVisitAppointmentMapper.class);
        DutyScheduleService dutyScheduleService = mock(DutyScheduleService.class);
        TimeSlotMapper timeSlotMapper = mock(TimeSlotMapper.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);

        StudentAppointmentService service = new StudentAppointmentService(
                studentAppointmentMapper,
                firstVisitAppointmentMapper,
                dutyScheduleService,
                timeSlotMapper,
                notificationLogService);

        when(studentAppointmentMapper.selectStudentAppointments(2L, null, null, null)).thenReturn(List.of(new MyAppointmentVO()));
        when(studentAppointmentMapper.countStudentAppointments(2L, null, null, null)).thenReturn(1L);

        var result = service.getMyAppointments(createStudentUser(2L), null, null, null, 0, -5);
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void getMyAppointmentsShouldRejectInvalidDateRange() {
        StudentAppointmentMapper studentAppointmentMapper = mock(StudentAppointmentMapper.class);
        FirstVisitAppointmentMapper firstVisitAppointmentMapper = mock(FirstVisitAppointmentMapper.class);
        DutyScheduleService dutyScheduleService = mock(DutyScheduleService.class);
        TimeSlotMapper timeSlotMapper = mock(TimeSlotMapper.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);

        StudentAppointmentService service = new StudentAppointmentService(
                studentAppointmentMapper,
                firstVisitAppointmentMapper,
                dutyScheduleService,
                timeSlotMapper,
                notificationLogService);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.getMyAppointments(
                        createStudentUser(2L),
                        null,
                        LocalDate.of(2026, 6, 10),
                        LocalDate.of(2026, 6, 1),
                        1,
                        10));

        assertEquals(400, exception.getCode());
        assertEquals("开始日期不能晚于结束日期", exception.getMessage());
    }

    @Test
    void getMyNotificationsShouldNormalizeInvalidPageParams() {
        StudentAppointmentMapper studentAppointmentMapper = mock(StudentAppointmentMapper.class);
        FirstVisitAppointmentMapper firstVisitAppointmentMapper = mock(FirstVisitAppointmentMapper.class);
        DutyScheduleService dutyScheduleService = mock(DutyScheduleService.class);
        TimeSlotMapper timeSlotMapper = mock(TimeSlotMapper.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);

        StudentAppointmentService service = new StudentAppointmentService(
                studentAppointmentMapper,
                firstVisitAppointmentMapper,
                dutyScheduleService,
                timeSlotMapper,
                notificationLogService);

        when(studentAppointmentMapper.selectStudentNotifications(2L, null)).thenReturn(List.of(new MyNotificationVO()));
        when(studentAppointmentMapper.countStudentNotifications(2L, null)).thenReturn(1L);

        var result = service.getMyNotifications(createStudentUser(2L), null, -1, 0);
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    private CurrentUserVO createStudentUser(Long userId) {
        CurrentUserVO user = new CurrentUserVO();
        user.setId(userId);
        user.setRoles(List.of(RoleCode.STUDENT));
        user.setPrimaryRole(RoleCode.STUDENT);
        return user;
    }
}
