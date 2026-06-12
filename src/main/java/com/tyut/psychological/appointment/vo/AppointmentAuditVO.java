package com.tyut.psychological.appointment.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约审核VO
 * 包含审核列表和详情所需的所有字段
 */
public class AppointmentAuditVO {
    private Long id;
    private String appointmentNo;
    
    // 学生信息
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String phone;
    
    // 首访登记信息
    private Long formId;
    private String mainProblem;
    private String problemDescription;
    private String expectedHelp;
    private Integer moodScore;
    private Integer sleepScore;
    private Integer stressScore;
    private Integer selfHarmFlag;
    private Integer emergencyFlag;
    private Integer riskScore;
    private String riskLevel;
    
    // 预约信息
    private LocalDate appointmentDate;
    private Long slotId;
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long roomId;
    private String roomName;
    
    // 初访员信息
    private Long interviewerId;
    private String interviewerName;
    
    // 值班安排信息
    private Long dutyScheduleId;
    
    // 预约状态和审核信息
    private String appointmentStatus;
    private Integer priorityFlag;
    private Long auditAdminId;
    private String auditAdminName;
    private LocalDateTime auditTime;
    private String auditRemark;
    private String cancelReason;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // getter和setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
    public String getMainProblem() { return mainProblem; }
    public void setMainProblem(String mainProblem) { this.mainProblem = mainProblem; }
    
    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }
    
    public String getExpectedHelp() { return expectedHelp; }
    public void setExpectedHelp(String expectedHelp) { this.expectedHelp = expectedHelp; }
    
    public Integer getMoodScore() { return moodScore; }
    public void setMoodScore(Integer moodScore) { this.moodScore = moodScore; }
    
    public Integer getSleepScore() { return sleepScore; }
    public void setSleepScore(Integer sleepScore) { this.sleepScore = sleepScore; }
    
    public Integer getStressScore() { return stressScore; }
    public void setStressScore(Integer stressScore) { this.stressScore = stressScore; }
    
    public Integer getSelfHarmFlag() { return selfHarmFlag; }
    public void setSelfHarmFlag(Integer selfHarmFlag) { this.selfHarmFlag = selfHarmFlag; }
    
    public Integer getEmergencyFlag() { return emergencyFlag; }
    public void setEmergencyFlag(Integer emergencyFlag) { this.emergencyFlag = emergencyFlag; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }
    
    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }
    
    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    
    public String getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    
    public Integer getPriorityFlag() { return priorityFlag; }
    public void setPriorityFlag(Integer priorityFlag) { this.priorityFlag = priorityFlag; }
    
    public Long getAuditAdminId() { return auditAdminId; }
    public void setAuditAdminId(Long auditAdminId) { this.auditAdminId = auditAdminId; }
    
    public String getAuditAdminName() { return auditAdminName; }
    public void setAuditAdminName(String auditAdminName) { this.auditAdminName = auditAdminName; }
    
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