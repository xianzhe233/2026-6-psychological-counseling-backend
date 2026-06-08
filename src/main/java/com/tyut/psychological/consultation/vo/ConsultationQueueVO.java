package com.tyut.psychological.consultation.vo;

import java.time.LocalDateTime;

public class ConsultationQueueVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String phone;
    private Long problemTypeId;
    private String problemTypeName;
    private String crisisLevel;
    private Integer priorityScore;
    private LocalDateTime enqueueTime;
    private String queueStatus;
    private String summary;
    private String nextAction;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    public String getProblemTypeName() { return problemTypeName; }
    public void setProblemTypeName(String problemTypeName) { this.problemTypeName = problemTypeName; }
    public String getCrisisLevel() { return crisisLevel; }
    public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }
    public LocalDateTime getEnqueueTime() { return enqueueTime; }
    public void setEnqueueTime(LocalDateTime enqueueTime) { this.enqueueTime = enqueueTime; }
    public String getQueueStatus() { return queueStatus; }
    public void setQueueStatus(String queueStatus) { this.queueStatus = queueStatus; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }
}
