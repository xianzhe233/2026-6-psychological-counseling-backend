package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.consultation.dto.ArrangeConsultationRequest;
import com.tyut.psychological.consultation.entity.ConsultationQueue;
import com.tyut.psychological.consultation.mapper.ConsultationScheduleMapper;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.schedule.entity.CounselingRoom;
import com.tyut.psychological.schedule.entity.TimeSlot;
import com.tyut.psychological.schedule.mapper.CounselingRoomMapper;
import com.tyut.psychological.schedule.mapper.DutyScheduleMapper;
import com.tyut.psychological.schedule.mapper.TimeSlotMapper;
import com.tyut.psychological.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultationScheduleServiceTest {

    @Test
    void arrangeShouldRejectNonAssistantOperator() {
        ConsultationScheduleMapper consultationScheduleMapper = mock(ConsultationScheduleMapper.class);
        ConsultationQueueService consultationQueueService = mock(ConsultationQueueService.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        DutyScheduleMapper dutyScheduleMapper = mock(DutyScheduleMapper.class);
        CounselingRoomMapper counselingRoomMapper = mock(CounselingRoomMapper.class);
        TimeSlotMapper timeSlotMapper = mock(TimeSlotMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);
        OperationLogService operationLogService = mock(OperationLogService.class);

        ConsultationScheduleService service = new ConsultationScheduleService(
                consultationScheduleMapper,
                consultationQueueService,
                staffProfileMapper,
                dutyScheduleMapper,
                counselingRoomMapper,
                timeSlotMapper,
                userMapper,
                notificationLogService,
                operationLogService);

        ConsultationQueue queue = new ConsultationQueue();
        queue.setId(10L);
        queue.setQueueStatus("WAITING");
        queue.setStudentId(200L);
        when(consultationQueueService.getRequired(10L)).thenReturn(queue);

        StaffProfile counselor = new StaffProfile();
        counselor.setId(300L);
        counselor.setStaffType("COUNSELOR");
        counselor.setStatus(1);
        when(staffProfileMapper.selectById(300L)).thenReturn(counselor);

        StaffProfile nonAssistant = new StaffProfile();
        nonAssistant.setId(400L);
        nonAssistant.setStaffType("INTERVIEWER");
        nonAssistant.setStatus(1);
        when(staffProfileMapper.selectByUserId(900L)).thenReturn(nonAssistant);

        ArrangeConsultationRequest request = new ArrangeConsultationRequest();
        request.setQueueId(10L);
        request.setStudentId(200L);
        request.setCounselorId(300L);
        request.setConsultationDate(LocalDate.of(2026, 6, 10));
        request.setSlotId(1L);
        request.setRoomId(2L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.arrange(900L, request));
        assertEquals(403, exception.getCode());
        assertEquals("当前人员无权执行心理助理操作", exception.getMessage());
        verify(consultationScheduleMapper, never()).insert(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void arrangeShouldCheckRoomAndSlotAfterAssistantValidation() {
        ConsultationScheduleMapper consultationScheduleMapper = mock(ConsultationScheduleMapper.class);
        ConsultationQueueService consultationQueueService = mock(ConsultationQueueService.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        DutyScheduleMapper dutyScheduleMapper = mock(DutyScheduleMapper.class);
        CounselingRoomMapper counselingRoomMapper = mock(CounselingRoomMapper.class);
        TimeSlotMapper timeSlotMapper = mock(TimeSlotMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        NotificationLogService notificationLogService = mock(NotificationLogService.class);
        OperationLogService operationLogService = mock(OperationLogService.class);

        ConsultationScheduleService service = new ConsultationScheduleService(
                consultationScheduleMapper,
                consultationQueueService,
                staffProfileMapper,
                dutyScheduleMapper,
                counselingRoomMapper,
                timeSlotMapper,
                userMapper,
                notificationLogService,
                operationLogService);

        ConsultationQueue queue = new ConsultationQueue();
        queue.setId(10L);
        queue.setQueueStatus("WAITING");
        queue.setStudentId(200L);
        when(consultationQueueService.getRequired(10L)).thenReturn(queue);

        StaffProfile counselor = new StaffProfile();
        counselor.setId(300L);
        counselor.setStaffType("COUNSELOR");
        counselor.setStatus(1);
        when(staffProfileMapper.selectById(300L)).thenReturn(counselor);

        StaffProfile assistant = new StaffProfile();
        assistant.setId(401L);
        assistant.setStaffType("ASSISTANT");
        assistant.setStatus(1);
        when(staffProfileMapper.selectByUserId(900L)).thenReturn(assistant);

        CounselingRoom room = new CounselingRoom();
        room.setId(2L);
        room.setStatus(1);
        when(counselingRoomMapper.selectById(2L)).thenReturn(room);

        when(timeSlotMapper.selectById(1L)).thenReturn(null);

        ArrangeConsultationRequest request = new ArrangeConsultationRequest();
        request.setQueueId(10L);
        request.setStudentId(200L);
        request.setCounselorId(300L);
        request.setConsultationDate(LocalDate.of(2026, 6, 10));
        request.setSlotId(1L);
        request.setRoomId(2L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.arrange(900L, request));
        assertEquals(404, exception.getCode());
        assertEquals("时间段不存在或已停用", exception.getMessage());
    }
}
