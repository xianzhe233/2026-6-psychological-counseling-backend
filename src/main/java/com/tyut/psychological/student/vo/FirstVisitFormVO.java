package com.tyut.psychological.student.vo;

import java.time.LocalDateTime;

public class FirstVisitFormVO {
    private Long id;
    private Long studentId;
    private String mainProblem;
    private String problemDescription;
    private String expectedHelp;
    private Integer moodScore;
    private Integer sleepScore;
    private Integer stressScore;
    private Integer selfHarmFlag;
    private Integer emergencyFlag;
    private Integer riskScore;
    private String riskLevel;
    private String formStatus;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getMainProblem() { return mainProblem; }
    public void setMainProblem(String mainProblem) { this.mainProblem = mainProblem; }
    
    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }
    
    public String getExpectedHelp() { return expectedHelp; }
    public void setExpectedHelp(String expectedHelp) { this.expectedHelp = expectedHelp; }
    
    public Integer getMoodScore() { return moodScore; }
    public void setMoodScore(Integer moodScore) { this.moodScore = moodScore; }
    
    public Integer getSleepScore() { return sleepScore; }
    public void setSleepScore(Integer sleepScore) { this.sleepScore = sleepScore; }
    
    public Integer getStressScore() { return stressScore; }
    public void setStressScore(Integer stressScore) { this.stressScore = stressScore; }
    
    public Integer getSelfHarmFlag() { return selfHarmFlag; }
    public void setSelfHarmFlag(Integer selfHarmFlag) { this.selfHarmFlag = selfHarmFlag; }
    
    public Integer getEmergencyFlag() { return emergencyFlag; }
    public void setEmergencyFlag(Integer emergencyFlag) { this.emergencyFlag = emergencyFlag; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public String getFormStatus() { return formStatus; }
    public void setFormStatus(String formStatus) { this.formStatus = formStatus; }
    
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}