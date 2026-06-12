package com.tyut.psychological.report.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CaseReportRequest {
    @NotNull(message = "学生不能为空")
    private Long studentId;
    @NotNull(message = "问题类型不能为空")
    private Long problemTypeId;
    @NotNull(message = "咨询总次数不能为空")
    @Min(value = 1, message = "咨询总次数必须大于0")
    private Integer totalSessions;
    private String effectSelfRating;
    private String caseSummary;
    private String counselingEffect;
    private String suggestion;
    @NotBlank(message = "结案类型不能为空")
    private String closeType;
    private String reportStatus;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
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
}
