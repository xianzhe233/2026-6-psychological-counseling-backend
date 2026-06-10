package com.tyut.psychological.consultation.vo;

import java.time.LocalDateTime;

public class ConsultationRecordVO {
    private Long id;
    private Long scheduleId;
    private String recordStatus;
    private LocalDateTime consultationTime;
    private String contentSummary;
    private String nextPlan;
    private Integer needClose;
    private LocalDateTime saveTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public String getRecordStatus() { return recordStatus; }
    public void setRecordStatus(String recordStatus) { this.recordStatus = recordStatus; }
    public LocalDateTime getConsultationTime() { return consultationTime; }
    public void setConsultationTime(LocalDateTime consultationTime) { this.consultationTime = consultationTime; }
    public String getContentSummary() { return contentSummary; }
    public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
    public String getNextPlan() { return nextPlan; }
    public void setNextPlan(String nextPlan) { this.nextPlan = nextPlan; }
    public Integer getNeedClose() { return needClose; }
    public void setNeedClose(Integer needClose) { this.needClose = needClose; }
    public LocalDateTime getSaveTime() { return saveTime; }
    public void setSaveTime(LocalDateTime saveTime) { this.saveTime = saveTime; }
}
