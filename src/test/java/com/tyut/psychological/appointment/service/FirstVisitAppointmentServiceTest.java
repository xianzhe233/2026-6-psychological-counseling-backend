package com.tyut.psychological.appointment.service;

import com.tyut.psychological.appointment.dto.ApproveRequest;
import com.tyut.psychological.appointment.dto.RejectRequest;
import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.appointment.vo.AppointmentAuditVO;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.schedule.entity.DutySchedule;
import com.tyut.psychological.schedule.service.DutyScheduleService;
import com.tyut.psychological.student.mapper.StudentFormMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirstVisitAppointmentServiceTest {

    @Test
    void pageAuditListShouldNormalizeInvalidPageParams() {
        TestContext context = new TestContext();
        when(context.appointmentMapper.pageAuditList(null, null, null, null, null, null)).thenReturn(List.of(new AppointmentAuditVO()));
        when(context.appointmentMapper.countAuditList(null, null, null, null, null, null)).thenReturn(1L);

        var result = context.service.pageAuditList(null, null, null, null, null, null, 0, -5);
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void approveAppointmentShouldAssignScheduleAndWriteLogs() {
        TestContext context = new TestContext();
        FirstVisitAppointment appointment = createAppointment("PENDING", null);
        when(context.appointmentMapper.selectById(1L)).thenReturn(appointment);

        DutySchedule schedule = createSchedule(8L, 6L, LocalDate.of(2026, 6, 12), 3L, 4L, 2, 0, 1, "INTERVIEWER");
        when(context.dutyScheduleService.getDutyScheduleById(8L)).thenReturn(schedule);

        AppointmentAuditVO detail = new AppointmentAuditVO();
        detail.setStudentName("张三");
        detail.setPhone("13800000000");
        detail.setAppointmentDate(schedule.getDutyDate());
        detail.setSlotName("周五上午");
        detail.setRoomName("A101");
        when(context.appointmentMapper.selectDetailById(1L)).thenReturn(detail);

        context.service.approveAppointment(1L, createApproveRequest(8L, schedule));

        verify(context.appointmentMapper).update(any(FirstVisitAppointment.class));
        verify(context.dutyScheduleService).incrementReservedCount(8L, 1);
        verify(context.notificationLogService).logAppointmentApproved(11L, "张三", "13800000000", 1L, "2026-06-12", "周五上午", "A101");
        verify(context.operationLogService).logSuccess(eq("初访预约"), eq("审核通过"), eq("预约ID: 1"));
    }

    @Test
    void rejectAppointmentShouldUpdateStatusAndReleaseReservedCount() {
        TestContext context = new TestContext();
        FirstVisitAppointment appointment = createAppointment("PENDING", 9L);
        when(context.appointmentMapper.selectById(1L)).thenReturn(appointment);

        RejectRequest request = new RejectRequest();
        request.setReason("信息不完整");
        context.service.rejectAppointment(1L, request);

        verify(context.dutyScheduleService).decrementReservedCount(9L, 1);
        verify(context.appointmentMapper).update(any(FirstVisitAppointment.class));
        verify(context.operationLogService).logSuccess(eq("初访预约"), eq("驳回预约"), eq("预约ID: 1"));
        verify(context.notificationLogService, never()).logAppointmentApproved(any(), any(), any(), any(), any(), any(), any());
    }

    private static FirstVisitAppointment createAppointment(String status, Long dutyScheduleId) {
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setId(1L);
        appointment.setStudentId(11L);
        appointment.setAppointmentStatus(status);
        appointment.setDutyScheduleId(dutyScheduleId);
        return appointment;
    }

    private static DutySchedule createSchedule(Long id, Long staffId, LocalDate dutyDate, Long slotId, Long roomId,
                                               int capacity, int reservedCount, int status, String staffType) {
        DutySchedule schedule = new DutySchedule();
        schedule.setId(id);
        schedule.setStaffId(staffId);
        schedule.setDutyDate(dutyDate);
        schedule.setSlotId(slotId);
        schedule.setRoomId(roomId);
        schedule.setCapacity(capacity);
        schedule.setReservedCount(reservedCount);
        schedule.setStatus(status);
        schedule.setStaffType(staffType);
        return schedule;
    }

    private static ApproveRequest createApproveRequest(Long dutyScheduleId, DutySchedule schedule) {
        ApproveRequest request = new ApproveRequest();
        request.setDutyScheduleId(dutyScheduleId);
        request.setInterviewerId(schedule.getStaffId());
        request.setAppointmentDate(schedule.getDutyDate());
        request.setSlotId(schedule.getSlotId());
        request.setRoomId(schedule.getRoomId());
        request.setAuditRemark("安排完成");
        return request;
    }

    private static final class TestContext {
        private final FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        private final DutyScheduleService dutyScheduleService = mock(DutyScheduleService.class);
        private final OperationLogService operationLogService = mock(OperationLogService.class);
        private final NotificationLogService notificationLogService = mock(NotificationLogService.class);
        private final StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        private final HttpServletRequest request = mock(HttpServletRequest.class);
        private final HttpSession session = mock(HttpSession.class);
        @SuppressWarnings("unused")
        private final StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        private final FirstVisitAppointmentService service;

        private TestContext() {
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("LOGIN_USER")).thenReturn(createAdminUser());
            service = new FirstVisitAppointmentService(
                appointmentMapper,
                dutyScheduleService,
                operationLogService,
                notificationLogService,
                studentFormMapper,
                request
            );
        }

        private CurrentUserVO createAdminUser() {
            CurrentUserVO user = new CurrentUserVO();
            user.setId(100L);
            user.setRealName("管理员");
            user.setRoles(List.of(RoleCode.ADMIN));
            user.setPrimaryRole(RoleCode.ADMIN);
            return user;
        }
    }
}
