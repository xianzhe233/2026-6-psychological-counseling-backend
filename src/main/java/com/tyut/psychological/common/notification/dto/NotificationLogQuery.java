package com.tyut.psychological.common.notification.dto;

import java.time.LocalDateTime;

public class NotificationLogQuery {
    private Integer pageNum;
    private Integer pageSize;
    private String notifyType;
    private String sendStatus;
    private String keyword;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize; }
    public int offset() { return (pageNum - 1) * pageSize; }
    public String getNotifyType() { return notifyType; }
    public void setNotifyType(String notifyType) { this.notifyType = notifyType; }
    public String getSendStatus() { return sendStatus; }
    public void setSendStatus(String sendStatus) { this.sendStatus = sendStatus; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
