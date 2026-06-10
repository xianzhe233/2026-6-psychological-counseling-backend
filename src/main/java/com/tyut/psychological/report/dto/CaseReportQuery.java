package com.tyut.psychological.report.dto;

public class CaseReportQuery {
    private Long counselorId;
    private String status;
    private String studentKeyword;
    private Integer pageNum;
    private Integer pageSize;

    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStudentKeyword() { return studentKeyword; }
    public void setStudentKeyword(String studentKeyword) { this.studentKeyword = studentKeyword; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public int getOffset() { return (pageNum - 1) * pageSize; }
}
