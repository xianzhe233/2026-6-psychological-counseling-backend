package com.tyut.psychological.consultation.vo;

import java.time.LocalDateTime;
import java.util.List;

public class ConsultationQueueDetailVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String phone;
    private String gender;
    private Long problemTypeId;
    private String problemTypeName;
    private String crisisLevel;
    private Integer priorityScore;
    private String queueStatus;
    private LocalDateTime enqueueTime;
    private Long firstVisitResultId;
    private Long appointmentId;
    private String appointmentNo;
    private String mainProblem;
    private String problemDescription;
    private String expectedHelp;
    private LocalDateTime interviewTime;
    private String interviewerName;
    private String summary;
    private String nextAction;
    private Integer riskScore;
    private String riskLevel;
    private List<ConsultationScheduleVO> schedules;

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
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    public String getProblemTypeName() { return problemTypeName; }
    public void setProblemTypeName(String problemTypeName) { this.problemTypeName = problemTypeName; }
    public String getCrisisLevel() { return crisisLevel; }
    public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }
    public String getQueueStatus() { return queueStatus; }
    public void setQueueStatus(String queueStatus) { this.queueStatus = queueStatus; }
    public LocalDateTime getEnqueueTime() { return enqueueTime; }
    public void setEnqueueTime(LocalDateTime enqueueTime) { this.enqueueTime = enqueueTime; }
    public Long getFirstVisitResultId() { return firstVisitResultId; }
    public void setFirstVisitResultId(Long firstVisitResultId) { this.firstVisitResultId = firstVisitResultId; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }
    public String getMainProblem() { return mainProblem; }
    public void setMainProblem(String mainProblem) { this.mainProblem = mainProblem; }
    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }
    public String getExpectedHelp() { return expectedHelp; }
    public void setExpectedHelp(String expectedHelp) { this.expectedHelp = expectedHelp; }
    public LocalDateTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalDateTime interviewTime) { this.interviewTime = interviewTime; }
    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public List<ConsultationScheduleVO> getSchedules() { return schedules; }
    public void setSchedules(List<ConsultationScheduleVO> schedules) { this.schedules = schedules; }
}
