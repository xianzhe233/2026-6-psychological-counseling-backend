package com.tyut.psychological.student.dto;

import jakarta.validation.constraints.NotNull;

public class AppointmentCreateRequest {
    @NotNull(message = "表单ID不能为空")
    private Long formId;
    
    @NotNull(message = "值班安排ID不能为空")
    private Long dutyScheduleId;
    
    @NotNull(message = "预约日期不能为空")
    private String appointmentDate;
    
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;
    
    @NotNull(message = "初访员ID不能为空")
    private Long interviewerId;
    
    @NotNull(message = "咨询室ID不能为空")
    private Long roomId;

    // Getters and Setters
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}