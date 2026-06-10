package com.tyut.psychological.consultation.entity;

import java.time.LocalDateTime;

public class ExtensionRequest {
    private Long id;
    private Long studentId;
    private Long counselorId;
    private Integer requestSessions;
    private String reason;
    private String requestStatus;
    private Long auditAdminId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public Integer getRequestSessions() { return requestSessions; }
    public void setRequestSessions(Integer requestSessions) { this.requestSessions = requestSessions; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }
    public Long getAuditAdminId() { return auditAdminId; }
    public void setAuditAdminId(Long auditAdminId) { this.auditAdminId = auditAdminId; }
    public LocalDateTime getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDateTime auditTime) { this.auditTime = auditTime; }
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
