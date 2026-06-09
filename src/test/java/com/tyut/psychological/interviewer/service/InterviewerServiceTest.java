package com.tyut.psychological.interviewer.service;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.mapper.ConsultationQueueMapper;
import com.tyut.psychological.interviewer.dto.InterviewResultRequest;
import com.tyut.psychological.interviewer.entity.FirstVisitResult;
import com.tyut.psychological.interviewer.mapper.FirstVisitResultMapper;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.student.entity.FirstVisitForm;
import com.tyut.psychological.student.mapper.StudentFormMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterviewerServiceTest {

    @Test
    void submitInterviewResultShouldCreateResultAndQueue() {
        // 准备mock对象
        FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        FirstVisitResultMapper firstVisitResultMapper = mock(FirstVisitResultMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        ConsultationQueueMapper consultationQueueMapper = mock(ConsultationQueueMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        
        // 创建service实例
        InterviewerService interviewerService = new InterviewerService(
            appointmentMapper, firstVisitResultMapper, staffProfileMapper,
            studentFormMapper, consultationQueueMapper, operationLogService
        );
        
        // 准备测试数据
        Long interviewerUserId = 1L;
        Long appointmentId = 1L;
        
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStaffType("INTERVIEWER");
        when(staffProfileMapper.selectByUserId(interviewerUserId)).thenReturn(staffProfile);
        
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setId(appointmentId);
        appointment.setStudentId(1L);
        appointment.setInterviewerId(1L);
        appointment.setAppointmentStatus("APPROVED");
        appointment.setPriorityFlag(0);
        when(appointmentMapper.selectById(appointmentId)).thenReturn(appointment);
        
        when(firstVisitResultMapper.selectByAppointmentId(appointmentId)).thenReturn(null);
        
        InterviewResultRequest request = new InterviewResultRequest();
        request.setCrisisLevel("MEDIUM");
        request.setProblemTypeId(1L);
        request.setInterviewTime(LocalDateTime.now());
        request.setConclusion("ARRANGE_CONSULTATION");
        request.setSummary("测试摘要");
        request.setNextAction("建议安排咨询");
        
        // 执行测试
        interviewerService.submitInterviewResult(appointmentId, interviewerUserId, request);
        
        // 验证结果
        ArgumentCaptor<FirstVisitResult> resultCaptor = ArgumentCaptor.forClass(FirstVisitResult.class);
        verify(firstVisitResultMapper).insert(resultCaptor.capture());
        FirstVisitResult savedResult = resultCaptor.getValue();
        assertEquals(appointmentId, savedResult.getAppointmentId());
        assertEquals(1L, savedResult.getInterviewerId());
        assertEquals("MEDIUM", savedResult.getCrisisLevel());
        assertEquals("ARRANGE_CONSULTATION", savedResult.getConclusion());
        
        // 验证预约状态更新
        ArgumentCaptor<FirstVisitAppointment> appointmentCaptor = ArgumentCaptor.forClass(FirstVisitAppointment.class);
        verify(appointmentMapper).update(appointmentCaptor.capture());
        assertEquals("COMPLETED", appointmentCaptor.getValue().getAppointmentStatus());
        
        // 验证咨询队列创建
        verify(consultationQueueMapper).insert(any());
        
        // 验证操作日志
        verify(operationLogService).logSuccess(eq("初访任务"), eq("提交初访结果"), anyString());
    }

    @Test
    void submitInterviewResultShouldRejectWhenStatusNotApproved() {
        // 准备mock对象
        FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        FirstVisitResultMapper firstVisitResultMapper = mock(FirstVisitResultMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        ConsultationQueueMapper consultationQueueMapper = mock(ConsultationQueueMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        
        // 创建service实例
        InterviewerService interviewerService = new InterviewerService(
            appointmentMapper, firstVisitResultMapper, staffProfileMapper,
            studentFormMapper, consultationQueueMapper, operationLogService
        );
        
        // 准备测试数据
        Long interviewerUserId = 1L;
        Long appointmentId = 1L;
        
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStaffType("INTERVIEWER");
        when(staffProfileMapper.selectByUserId(interviewerUserId)).thenReturn(staffProfile);
        
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setId(appointmentId);
        appointment.setStudentId(1L);
        appointment.setInterviewerId(1L);
        appointment.setAppointmentStatus("PENDING"); // 状态不是APPROVED
        when(appointmentMapper.selectById(appointmentId)).thenReturn(appointment);
        
        InterviewResultRequest request = new InterviewResultRequest();
        request.setCrisisLevel("MEDIUM");
        request.setProblemTypeId(1L);
        request.setInterviewTime(LocalDateTime.now());
        request.setConclusion("ARRANGE_CONSULTATION");
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            interviewerService.submitInterviewResult(appointmentId, interviewerUserId, request);
        });
        
        // 验证没有调用插入方法
        verify(firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenInterviewerMismatch() {
        // 准备mock对象
        FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        FirstVisitResultMapper firstVisitResultMapper = mock(FirstVisitResultMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        ConsultationQueueMapper consultationQueueMapper = mock(ConsultationQueueMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        
        // 创建service实例
        InterviewerService interviewerService = new InterviewerService(
            appointmentMapper, firstVisitResultMapper, staffProfileMapper,
            studentFormMapper, consultationQueueMapper, operationLogService
        );
        
        // 准备测试数据
        Long interviewerUserId = 1L;
        Long appointmentId = 1L;
        
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStaffType("INTERVIEWER");
        when(staffProfileMapper.selectByUserId(interviewerUserId)).thenReturn(staffProfile);
        
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setId(appointmentId);
        appointment.setStudentId(1L);
        appointment.setInterviewerId(2L); // 初访员ID不匹配
        appointment.setAppointmentStatus("APPROVED");
        when(appointmentMapper.selectById(appointmentId)).thenReturn(appointment);
        
        InterviewResultRequest request = new InterviewResultRequest();
        request.setCrisisLevel("MEDIUM");
        request.setProblemTypeId(1L);
        request.setInterviewTime(LocalDateTime.now());
        request.setConclusion("ARRANGE_CONSULTATION");
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            interviewerService.submitInterviewResult(appointmentId, interviewerUserId, request);
        });
        
        // 验证没有调用插入方法
        verify(firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenResultAlreadyExists() {
        // 准备mock对象
        FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        FirstVisitResultMapper firstVisitResultMapper = mock(FirstVisitResultMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        ConsultationQueueMapper consultationQueueMapper = mock(ConsultationQueueMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        
        // 创建service实例
        InterviewerService interviewerService = new InterviewerService(
            appointmentMapper, firstVisitResultMapper, staffProfileMapper,
            studentFormMapper, consultationQueueMapper, operationLogService
        );
        
        // 准备测试数据
        Long interviewerUserId = 1L;
        Long appointmentId = 1L;
        
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStaffType("INTERVIEWER");
        when(staffProfileMapper.selectByUserId(interviewerUserId)).thenReturn(staffProfile);
        
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setId(appointmentId);
        appointment.setStudentId(1L);
        appointment.setInterviewerId(1L);
        appointment.setAppointmentStatus("APPROVED");
        when(appointmentMapper.selectById(appointmentId)).thenReturn(appointment);
        
        // 模拟已存在初访结果
        FirstVisitResult existingResult = new FirstVisitResult();
        existingResult.setId(1L);
        when(firstVisitResultMapper.selectByAppointmentId(appointmentId)).thenReturn(existingResult);
        
        InterviewResultRequest request = new InterviewResultRequest();
        request.setCrisisLevel("MEDIUM");
        request.setProblemTypeId(1L);
        request.setInterviewTime(LocalDateTime.now());
        request.setConclusion("ARRANGE_CONSULTATION");
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            interviewerService.submitInterviewResult(appointmentId, interviewerUserId, request);
        });
        
        // 验证没有调用插入方法
        verify(firstVisitResultMapper, never()).insert(any());
    }

    @Test
    void submitInterviewResultShouldRejectWhenTransferWithoutNextAction() {
        // 准备mock对象
        FirstVisitAppointmentMapper appointmentMapper = mock(FirstVisitAppointmentMapper.class);
        FirstVisitResultMapper firstVisitResultMapper = mock(FirstVisitResultMapper.class);
        StaffProfileMapper staffProfileMapper = mock(StaffProfileMapper.class);
        StudentFormMapper studentFormMapper = mock(StudentFormMapper.class);
        ConsultationQueueMapper consultationQueueMapper = mock(ConsultationQueueMapper.class);
        OperationLogService operationLogService = mock(OperationLogService.class);
        
        // 创建service实例
        InterviewerService interviewerService = new InterviewerService(
            appointmentMapper, firstVisitResultMapper, staffProfileMapper,
            studentFormMapper, consultationQueueMapper, operationLogService
        );
        
        // 准备测试数据
        Long interviewerUserId = 1L;
        Long appointmentId = 1L;
        
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setId(1L);
        staffProfile.setStaffType("INTERVIEWER");
        when(staffProfileMapper.selectByUserId(interviewerUserId)).thenReturn(staffProfile);
        
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setId(appointmentId);
        appointment.setStudentId(1L);
        appointment.setInterviewerId(1L);
        appointment.setAppointmentStatus("APPROVED");
        when(appointmentMapper.selectById(appointmentId)).thenReturn(appointment);
        
        when(firstVisitResultMapper.selectByAppointmentId(appointmentId)).thenReturn(null);
        
        InterviewResultRequest request = new InterviewResultRequest();
        request.setCrisisLevel("MEDIUM");
        request.setProblemTypeId(1L);
        request.setInterviewTime(LocalDateTime.now());
        request.setConclusion("TRANSFER"); // 转介送诊
        request.setNextAction(null); // 没有填写后续建议
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            interviewerService.submitInterviewResult(appointmentId, interviewerUserId, request);
        });
        
        // 验证没有调用插入方法
        verify(firstVisitResultMapper, never()).insert(any());
    }
}