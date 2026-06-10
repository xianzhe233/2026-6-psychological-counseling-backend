package com.tyut.psychological.consultation.dto;

public class ExtensionAdminQuery {
    private String status;
    private Integer pageNum;
    private Integer pageSize;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public int getOffset() { return (pageNum - 1) * pageSize; }
}
