package com.tyut.psychological.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 学生预约创建请求DTO
 */
public class StudentAppointmentCreateRequest {
    @NotNull(message = "首访登记表ID不能为空")
    private Long formId;
    
    @NotNull(message = "值班安排ID不能为空")
    private Long dutyScheduleId;
    
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;
    
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;
    
    @NotNull(message = "初访员ID不能为空")
    private Long interviewerId;
    
    @NotNull(message = "咨询室ID不能为空")
    private Long roomId;

    // getter和setter方法
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}