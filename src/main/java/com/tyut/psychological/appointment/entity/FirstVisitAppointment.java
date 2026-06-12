package com.tyut.psychological.appointment.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 初访预约实体类
 * 对应数据库表：first_visit_appointment
 */
public class FirstVisitAppointment {
    private Long id;
    private String appointmentNo;
    private Long studentId;
    private Long formId;
    private Long interviewerId;
    private String interviewerName;
    private Long dutyScheduleId;
    private LocalDate appointmentDate;
    private Long slotId;
    private String slotName;
    private java.time.LocalTime startTime;
    private java.time.LocalTime endTime;
    private Long roomId;
    private String roomName;
    private String appointmentStatus;
    private Integer priorityFlag;
    private Long auditAdminId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // getter和setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }
    
    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }
    
    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    
    public java.time.LocalTime getStartTime() { return startTime; }
    public void setStartTime(java.time.LocalTime startTime) { this.startTime = startTime; }
    
    public java.time.LocalTime getEndTime() { return endTime; }
    public void setEndTime(java.time.LocalTime endTime) { this.endTime = endTime; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public String getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    
    public Integer getPriorityFlag() { return priorityFlag; }
    public void setPriorityFlag(Integer priorityFlag) { this.priorityFlag = priorityFlag; }
    
    public Long getAuditAdminId() { return auditAdminId; }
    public void setAuditAdminId(Long auditAdminId) { this.auditAdminId = auditAdminId; }
    
    public LocalDateTime getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDateTime auditTime) { this.auditTime = auditTime; }
    
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
    
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}