package com.tyut.psychological.report.entity;

import java.time.LocalDateTime;

public class CaseReport {
    private Long id;
    private Long studentId;
    private Long counselorId;
    private Long problemTypeId;
    private Integer totalSessions;
    private String effectSelfRating;
    private String caseSummary;
    private String counselingEffect;
    private String suggestion;
    private String closeType;
    private String reportStatus;
    private String reportFilePath;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
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
    public String getReportFilePath() { return reportFilePath; }
    public void setReportFilePath(String reportFilePath) { this.reportFilePath = reportFilePath; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
