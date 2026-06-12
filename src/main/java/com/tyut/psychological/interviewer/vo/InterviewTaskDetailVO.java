package com.tyut.psychological.interviewer.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 初访任务详情VO
 * 包含学生信息、登记表摘要、预约信息
 */
public class InterviewTaskDetailVO {
    private Long appointmentId;
    private String appointmentNo;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String phone;
    private Long formId;
    private String mainProblem;
    private String problemDescription;
    private String expectedHelp;
    private Integer moodScore;
    private Integer sleepScore;
    private Integer stressScore;
    private Integer selfHarmFlag;
    private Integer emergencyFlag;
    private String riskLevel;
    private Integer riskScore;
    private LocalDate appointmentDate;
    private Long slotId;
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long roomId;
    private String roomName;
    private Long interviewerId;
    private String interviewerName;
    private Integer priorityFlag;
    private String appointmentStatus;
    private LocalDateTime createTime;
    // 初访结果（已完成预约时有值）
    private LatestResultVO latestResult;

    /**
     * 初访结果内嵌VO
     */
    public static class LatestResultVO {
        private String crisisLevel;
        private Long problemTypeId;
        private String problemTypeLabel;
        private LocalDateTime interviewTime;
        private String conclusion;
        private String summary;
        private String nextAction;
        private LocalDateTime submitTime;

        public String getCrisisLevel() { return crisisLevel; }
        public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
        public Long getProblemTypeId() { return problemTypeId; }
        public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
        public String getProblemTypeLabel() { return problemTypeLabel; }
        public void setProblemTypeLabel(String problemTypeLabel) { this.problemTypeLabel = problemTypeLabel; }
        public LocalDateTime getInterviewTime() { return interviewTime; }
        public void setInterviewTime(LocalDateTime interviewTime) { this.interviewTime = interviewTime; }
        public String getConclusion() { return conclusion; }
        public void setConclusion(String conclusion) { this.conclusion = conclusion; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getNextAction() { return nextAction; }
        public void setNextAction(String nextAction) { this.nextAction = nextAction; }
        public LocalDateTime getSubmitTime() { return submitTime; }
        public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    }
    
    // getter和setter方法
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    
    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }
    
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
    
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
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

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }

    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }

    public Integer getPriorityFlag() { return priorityFlag; }
    public void setPriorityFlag(Integer priorityFlag) { this.priorityFlag = priorityFlag; }

    public String getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LatestResultVO getLatestResult() { return latestResult; }
    public void setLatestResult(LatestResultVO latestResult) { this.latestResult = latestResult; }
}