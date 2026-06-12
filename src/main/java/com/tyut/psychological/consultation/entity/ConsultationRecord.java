package com.tyut.psychological.consultation.entity;

import java.time.LocalDateTime;

public class ConsultationRecord {
    private Long id;
    private Long scheduleId;
    private Long studentId;
    private Long counselorId;
    private Integer sessionNo;
    private LocalDateTime consultationTime;
    private String recordStatus;
    private String contentSummary;
    private String nextPlan;
    private Integer needClose;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public Integer getSessionNo() { return sessionNo; }
    public void setSessionNo(Integer sessionNo) { this.sessionNo = sessionNo; }
    public LocalDateTime getConsultationTime() { return consultationTime; }
    public void setConsultationTime(LocalDateTime consultationTime) { this.consultationTime = consultationTime; }
    public String getRecordStatus() { return recordStatus; }
    public void setRecordStatus(String recordStatus) { this.recordStatus = recordStatus; }
    public String getContentSummary() { return contentSummary; }
    public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
    public String getNextPlan() { return nextPlan; }
    public void setNextPlan(String nextPlan) { this.nextPlan = nextPlan; }
    public Integer getNeedClose() { return needClose; }
    public void setNeedClose(Integer needClose) { this.needClose = needClose; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
