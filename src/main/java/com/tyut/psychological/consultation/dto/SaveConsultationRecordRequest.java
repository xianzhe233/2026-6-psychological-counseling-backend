package com.tyut.psychological.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class SaveConsultationRecordRequest {
    @NotBlank(message = "记录状态不能为空")
    private String recordStatus;
    private LocalDateTime consultationTime;
    private String contentSummary;
    private String nextPlan;
    @NotNull(message = "是否需要结案不能为空")
    private Integer needClose;

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
}
