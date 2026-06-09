package com.tyut.psychological.interviewer.service;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.common.entity.ProblemType;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.common.mapper.ProblemTypeMapper;
import com.tyut.psychological.consultation.mapper.ConsultationQueueMapper;
import com.tyut.psychological.interviewer.dto.InterviewResultRequest;
import com.tyut.psychological.interviewer.entity.FirstVisitResult;
import com.tyut.psychological.interviewer.mapper.FirstVisitResultMapper;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.student.mapper.StudentFormMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterviewerServiceTest {

    @Test
    void submitInterviewResultShouldCreateResultAndQueue() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        context.mockAppointment(1L, 1L, "APPROVED", 0);
        context.mockProblemType(1L, 1);
        when(context.firstVisitResultMapper.selectByAppointmentId(1L)).thenReturn(null);

        InterviewResultRequest request = createRequest("MEDIUM", 1L, "ARRANGE_CONSULTATION", "测试摘要", "建议安排咨询");

        context.service.submitInterviewResult(1L, 1L, request);

        ArgumentCaptor<FirstVisitResult> resultCaptor = ArgumentCaptor.forClass(FirstVisitResult.class);
        verify(context.firstVisitResultMapper).insert(resultCaptor.capture());
        FirstVisitResult savedResult = resultCaptor.getValue();
        assertEquals(1L, savedResult.getAppointmentId());
        assertEquals(1L, savedResult.getInterviewerId());
        assertEquals("MEDIUM", savedResult.getCrisisLevel());
        assertEquals("ARRANGE_CONSULTATION", savedResult.getConclusion());

        ArgumentCaptor<FirstVisitAppointment> appointmentCaptor = ArgumentCaptor.forClass(FirstVisitAppointment.class);
        verify(context.appointmentMapper).update(appointmentCaptor.capture());
        assertEquals("COMPLETED", appointmentCaptor.getValue().getAppointmentStatus());

        verify(context.consultationQueueMapper).insert(any());
        verify(context.operationLogService).logSuccess(eq("初访任务"), eq("提交初访结果"), any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenStatusNotApproved() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        context.mockAppointment(1L, 1L, "PENDING", 0);

        InterviewResultRequest request = createRequest("MEDIUM", 1L, "ARRANGE_CONSULTATION", null, null);

        assertThrows(BusinessException.class, () -> context.service.submitInterviewResult(1L, 1L, request));
        verify(context.firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenInterviewerMismatch() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        context.mockAppointment(1L, 2L, "APPROVED", 0);

        InterviewResultRequest request = createRequest("MEDIUM", 1L, "ARRANGE_CONSULTATION", null, null);

        assertThrows(BusinessException.class, () -> context.service.submitInterviewResult(1L, 1L, request));
        verify(context.firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenResultAlreadyExists() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        context.mockAppointment(1L, 1L, "APPROVED", 0);
        FirstVisitResult existingResult = new FirstVisitResult();
        existingResult.setId(1L);
        when(context.firstVisitResultMapper.selectByAppointmentId(1L)).thenReturn(existingResult);

        InterviewResultRequest request = createRequest("MEDIUM", 1L, "ARRANGE_CONSULTATION", null, null);

        assertThrows(BusinessException.class, () -> context.service.submitInterviewResult(1L, 1L, request));
        verify(context.firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenTransferWithoutNextAction() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        context.mockAppointment(1L, 1L, "APPROVED", 0);
        context.mockProblemType(1L, 1);
        when(context.firstVisitResultMapper.selectByAppointmentId(1L)).thenReturn(null);

        InterviewResultRequest request = createRequest("MEDIUM", 1L, "TRANSFER", null, null);

        assertThrows(BusinessException.class, () -> context.service.submitInterviewResult(1L, 1L, request));
        verify(context.firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenProblemTypeInvalid() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        context.mockAppointment(1L, 1L, "APPROVED", 0);
        when(context.firstVisitResultMapper.selectByAppointmentId(1L)).thenReturn(null);
        when(context.problemTypeMapper.selectById(99L)).thenReturn(null);

        InterviewResultRequest request = createRequest("MEDIUM", 99L, "NO_NEED", null, null);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> context.service.submitInterviewResult(1L, 1L, request));
        assertEquals(400, exception.getCode());
        assertEquals("问题类型不存在或已停用", exception.getMessage());
        verify(context.firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void pageInterviewTasksShouldRejectDisabledInterviewer() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 0);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> context.service.pageInterviewTasks(1L, null, null, null, null, -1, 0));
        assertEquals(403, exception.getCode());
        assertEquals("当前初访员已停用", exception.getMessage());
        verify(context.appointmentMapper, never()).pageInterviewTasks(any(), any(), any(), any(), any());
    }

    @Test
    void pageInterviewTasksShouldNormalizeInvalidPageParams() {
        TestContext context = new TestContext();
        context.mockInterviewer(1L, 1);
        when(context.appointmentMapper.pageInterviewTasks(1L, null, null, null, null)).thenReturn(List.of());
        when(context.appointmentMapper.countInterviewTasks(1L, null, null, null, null)).thenReturn(0L);

        var result = context.service.pageInterviewTasks(1L, null, null, null, null, 0, -5);
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(0, result.getTotal());
    }

    private static InterviewResultRequest createRequest(String crisisLevel, Long problemTypeId, String conclusion, String summary, String nextAction) {
        InterviewResultRequest request = new InterviewResultRequest();
        request.setCrisisLevel(crisisLevel);
        request.setProblemTypeId(problemTypeId);
        request.setInterviewTime(LocalDateTime.now());
        request.setConclusion(conclusion);
        request.setSummary(summary);
        request.setNextAction(nextAction);
        return request;
    }

    private static final class TestContext {
        private final FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        private final FirstVisitResultMapper firstVisitResultMapper = mock(FirstVisitResultMapper.class);
        private final StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        private final StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        private final ConsultationQueueMapper consultationQueueMapper = mock(ConsultationQueueMapper.class);
        private final OperationLogService operationLogService = mock(OperationLogService.class);
        private final ProblemTypeMapper problemTypeMapper = mock(ProblemTypeMapper.class);
        private final InterviewerService service = new InterviewerService(
            appointmentMapper,
            firstVisitResultMapper,
            staffProfileMapper,
            studentFormMapper,
            consultationQueueMapper,
            operationLogService,
            problemTypeMapper
        );

        private void mockInterviewer(Long userId, Integer status) {
            StaffProfile staffProfile = new StaffProfile();
            staffProfile.setId(1L);
            staffProfile.setStaffType("INTERVIEWER");
            staffProfile.setStatus(status);
            when(staffProfileMapper.selectByUserId(userId)).thenReturn(staffProfile);
        }

        private void mockAppointment(Long appointmentId, Long interviewerId, String status, Integer priorityFlag) {
            FirstVisitAppointment appointment = new FirstVisitAppointment();
            appointment.setId(appointmentId);
            appointment.setStudentId(1L);
            appointment.setInterviewerId(interviewerId);
            appointment.setAppointmentStatus(status);
            appointment.setPriorityFlag(priorityFlag);
            when(appointmentMapper.selectById(appointmentId)).thenReturn(appointment);
        }

        private void mockProblemType(Long problemTypeId, Integer status) {
            ProblemType problemType = new ProblemType();
            problemType.setId(problemTypeId);
            problemType.setStatus(status);
            when(problemTypeMapper.selectById(problemTypeId)).thenReturn(problemType);
        }
    }
}
