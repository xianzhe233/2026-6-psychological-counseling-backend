package com.tyut.psychological.report.vo;

import java.time.LocalDateTime;

public class CaseReportVO {
    private Long id;
    private String reportNo;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String phone;
    private Long problemTypeId;
    private String problemTypeLabel;
    private Integer totalSessions;
    private String effectSelfRating;
    private String caseSummary;
    private String counselingEffect;
    private String suggestion;
    private String closeType;
    private String reportStatus;
    private LocalDateTime submitTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }
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
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    public String getProblemTypeLabel() { return problemTypeLabel; }
    public void setProblemTypeLabel(String problemTypeLabel) { this.problemTypeLabel = problemTypeLabel; }
    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    public String getEffectSelfRating() { return effectSelfRating; }
    public void setEffectSelfRating(String effectSelfRating) { this.effectSelfRating = effectSelfRating; }
    public String getCaseSummary() { return caseSummary; }
    public void setCaseSummary(String caseSummary) { this.caseSummary = caseSummary; }
    public String getCounselingEffect() { return counselingEffect; }
    public void setCounselingEffect(String counselingEffect) { this.counselingEffect = counselingEffect; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public String getCloseType() { return closeType; }
    public void setCloseType(String closeType) { this.closeType = closeType; }
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
