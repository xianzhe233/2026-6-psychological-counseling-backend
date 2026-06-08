package com.tyut.psychological.consultation.dto;

public class ConsultationQueueQuery {
    private String keyword;
    private String crisisLevel;
    private Long problemTypeId;
    private String status;
    private Integer pageNum;
    private Integer pageSize;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getCrisisLevel() { return crisisLevel; }
    public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
