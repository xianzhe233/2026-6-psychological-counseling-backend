package com.tyut.psychological.consultation.vo;

import java.time.LocalDate;

public class CounselorScheduleVO {
    private Long id;
    private String scheduleNo;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String phone;
    private Long problemTypeId;
    private String problemTypeLabel;
    private String crisisLevel;
    private LocalDate consultationDate;
    private String slotName;
    private String startTime;
    private String endTime;
    private String roomName;
    private Integer sessionIndex;
    private String status;
    private Long counselorId;
    private String counselorName;
    private String firstVisitSummary;
    private String nextAction;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getScheduleNo() { return scheduleNo; }
    public void setScheduleNo(String scheduleNo) { this.scheduleNo = scheduleNo; }
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
    public String getProblemTypeLabel() { return problemTypeLabel; }
    public void setProblemTypeLabel(String problemTypeLabel) { this.problemTypeLabel = problemTypeLabel; }
    public String getCrisisLevel() { return crisisLevel; }
    public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
    public LocalDate getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDate consultationDate) { this.consultationDate = consultationDate; }
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public Integer getSessionIndex() { return sessionIndex; }
    public void setSessionIndex(Integer sessionIndex) { this.sessionIndex = sessionIndex; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public String getCounselorName() { return counselorName; }
    public void setCounselorName(String counselorName) { this.counselorName = counselorName; }
    public String getFirstVisitSummary() { return firstVisitSummary; }
    public void setFirstVisitSummary(String firstVisitSummary) { this.firstVisitSummary = firstVisitSummary; }
    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }
}
