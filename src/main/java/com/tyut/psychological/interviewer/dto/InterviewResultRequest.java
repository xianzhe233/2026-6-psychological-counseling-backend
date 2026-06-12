package com.tyut.psychological.interviewer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 初访结果提交请求DTO
 */
public class InterviewResultRequest {
    
    /**
     * 危机等级：LOW/MEDIUM/HIGH/URGENT
     */
    @NotBlank(message = "危机等级不能为空")
    private String crisisLevel;
    
    /**
     * 问题类型ID
     */
    @NotNull(message = "问题类型不能为空")
    private Long problemTypeId;
    
    /**
     * 初访时间
     */
    @NotNull(message = "初访时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;
    
    /**
     * 初访结论：NO_NEED/ARRANGE_CONSULTATION/TRANSFER
     */
    @NotBlank(message = "初访结论不能为空")
    private String conclusion;
    
    /**
     * 初访摘要
     */
    private String summary;
    
    /**
     * 后续建议（转介送诊时必填）
     */
    private String nextAction;
    
    // getter和setter方法
    public String getCrisisLevel() { return crisisLevel; }
    public void setCrisisLevel(String crisisLevel) { this.crisisLevel = crisisLevel; }
    
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
    
    public LocalDateTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalDateTime interviewTime) { this.interviewTime = interviewTime; }
    
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }
}