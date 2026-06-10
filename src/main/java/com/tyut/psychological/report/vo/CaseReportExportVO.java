package com.tyut.psychological.report.vo;

public class CaseReportExportVO {
    private Long id;
    private String reportNo;
    private String studentName;
    private String studentNo;
    private String gender;
    private String college;
    private String phone;
    private String problemTypeLabel;
    private Integer totalSessions;
    private String effectSelfRating;
    private String caseSummary;
    private String suggestion;
    private String reportStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProblemTypeLabel() { return problemTypeLabel; }
    public void setProblemTypeLabel(String problemTypeLabel) { this.problemTypeLabel = problemTypeLabel; }
    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    public String getEffectSelfRating() { return effectSelfRating; }
    public void setEffectSelfRating(String effectSelfRating) { this.effectSelfRating = effectSelfRating; }
    public String getCaseSummary() { return caseSummary; }
    public void setCaseSummary(String caseSummary) { this.caseSummary = caseSummary; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }
}
