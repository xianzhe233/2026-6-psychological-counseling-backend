package com.tyut.psychological.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 审核通过请求DTO
 */
public class ApproveRequest {
    @NotNull(message = "值班安排ID不能为空")
    private Long dutyScheduleId;
    
    @NotNull(message = "初访员ID不能为空")
    private Long interviewerId;
    
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;
    
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;
    
    private Long roomId;
    
    private String auditRemark;

    // getter和setter方法
    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    
    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }
    
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
}