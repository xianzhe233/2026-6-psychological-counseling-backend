package com.tyut.psychological.consultation.entity;

import java.time.LocalDateTime;

public class ConsultationQueue {
    private Long id;
    private Long studentId;
    private Long firstVisitResultId;
    private Long problemTypeId;
    private String crisisLevel;
    private Integer priorityScore;
    private String queueStatus;
    private LocalDateTime enqueueTime;
    private LocalDateTime assignedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getFirstVisitResultId() { return firstVisitResultId; }
    public void setFirstVisitResultId(Long firstVisitResultId) { this.firstVisitResultId = firstVisitResultId; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    public String getCrisisLevel() { return crisisLevel; }
    public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }
    public String getQueueStatus() { return queueStatus; }
    public void setQueueStatus(String queueStatus) { this.queueStatus = queueStatus; }
    public LocalDateTime getEnqueueTime() { return enqueueTime; }
    public void setEnqueueTime(LocalDateTime enqueueTime) { this.enqueueTime = enqueueTime; }
    public LocalDateTime getAssignedTime() { return assignedTime; }
    public void setAssignedTime(LocalDateTime assignedTime) { this.assignedTime = assignedTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
