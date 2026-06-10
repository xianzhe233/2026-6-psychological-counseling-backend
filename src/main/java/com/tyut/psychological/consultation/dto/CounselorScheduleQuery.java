package com.tyut.psychological.consultation.dto;

import java.time.LocalDate;

public class CounselorScheduleQuery {
    private Long counselorId;
    private String studentKeyword;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer pageNum;
    private Integer pageSize;

    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public String getStudentKeyword() { return studentKeyword; }
    public void setStudentKeyword(String studentKeyword) { this.studentKeyword = studentKeyword; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
