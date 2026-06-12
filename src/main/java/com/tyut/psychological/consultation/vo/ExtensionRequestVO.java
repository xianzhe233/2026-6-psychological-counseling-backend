package com.tyut.psychological.consultation.vo;

import java.time.LocalDateTime;

public class ExtensionRequestVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private Integer requestSessions;
    private String reason;
    private String status;
    private String auditRemark;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public Integer getRequestSessions() { return requestSessions; }
    public void setRequestSessions(Integer requestSessions) { this.requestSessions = requestSessions; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
