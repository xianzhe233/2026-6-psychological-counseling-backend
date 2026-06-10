package com.tyut.psychological.common.log.dto;

import java.time.LocalDateTime;

public class OperationLogQuery {
    private Integer pageNum;
    private Integer pageSize;
    private String operationType;
    private String resultStatus;
    private String keyword;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize; }
    public int offset() { return (pageNum - 1) * pageSize; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
