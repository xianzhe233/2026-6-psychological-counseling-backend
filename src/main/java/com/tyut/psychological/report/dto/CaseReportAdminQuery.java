package com.tyut.psychological.report.dto;

import java.time.LocalDate;

public class CaseReportAdminQuery {
    private String studentKeyword;
    private Long counselorId;
    private Long problemTypeId;
    private String closeType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum;
    private Integer pageSize;

    public String getStudentKeyword() { return studentKeyword; }
    public void setStudentKeyword(String studentKeyword) { this.studentKeyword = studentKeyword; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    public String getCloseType() { return closeType; }
    public void setCloseType(String closeType) { this.closeType = closeType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public int getOffset() { return (pageNum - 1) * pageSize; }
}
