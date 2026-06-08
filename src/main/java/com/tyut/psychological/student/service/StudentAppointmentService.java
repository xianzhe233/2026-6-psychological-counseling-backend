package com.tyut.psychological.student.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.student.dto.AppointmentCreateRequest;
import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.student.mapper.StudentAppointmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentAppointmentService {
    private final StudentAppointmentMapper appointmentMapper;
    private final AtomicLong appointmentNoSeed = new AtomicLong(1000);
    
    public StudentAppointmentService(StudentAppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }
    
    @Transactional
    public Map<String, Object> createAppointment(Long studentId, AppointmentCreateRequest request) {
        // 1. 检查学生是否有未完成的预约
        FirstVisitAppointment existing = appointmentMapper.selectActiveByStudentId(studentId);
        if (existing != null) {
            throw new BusinessException(409, "您已有未完成的预约，不能重复预约");
        }
        
        // 2. 创建预约记录
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setFormId(request.getFormId());
        appointment.setStudentId(studentId);
        appointment.setDutyScheduleId(request.getDutyScheduleId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setSlotId(request.getSlotId());
        
        // 从数据库查询时间段信息（这里简化处理，使用硬编码）
        appointment.setSlotName("上午第一时段");
        appointment.setStartTime(LocalTime.of(8, 30));
        appointment.setEndTime(LocalTime.of(10, 0));
        
        // 设置初访员和咨询室（从请求或值班安排获取）
        if (request.getInterviewerId() != null) {
            appointment.setInterviewerId(request.getInterviewerId());
            appointment.setInterviewerName("初访员张老师");
        }
        if (request.getRoomId() != null) {
            appointment.setRoomId(request.getRoomId());
            appointment.setRoomName("咨询室101");
        }
        
        appointment.setAppointmentStatus("PENDING");
        
        // 3. 插入数据库
        appointmentMapper.insert(appointment);
        
        // 4. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", appointment.getId());
        result.put("appointmentNo", appointment.getAppointmentNo());
        result.put("appointmentStatus", appointment.getAppointmentStatus());
        return result;
    }
    
    private String generateAppointmentNo() {
        long seq = appointmentNoSeed.incrementAndGet();
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("FV%s%04d", dateStr, seq);
    }
}