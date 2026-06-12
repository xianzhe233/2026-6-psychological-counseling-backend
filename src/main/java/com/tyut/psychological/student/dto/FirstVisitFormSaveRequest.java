package com.tyut.psychological.student.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FirstVisitFormSaveRequest {
    @NotBlank(message = "主要困扰不能为空")
    private String mainProblem;
    @NotBlank(message = "问题详细描述不能为空")
    private String problemDescription;
    @NotBlank(message = "期望帮助不能为空")
    private String expectedHelp;
    @NotNull(message = "情绪评分不能为空")
    @Min(value = 0, message = "情绪评分范围应为0-10")
    @Max(value = 10, message = "情绪评分范围应为0-10")
    private Integer moodScore;
    @NotNull(message = "睡眠评分不能为空")
    @Min(value = 0, message = "睡眠评分范围应为0-10")
    @Max(value = 10, message = "睡眠评分范围应为0-10")
    private Integer sleepScore;
    @NotNull(message = "压力评分不能为空")
    @Min(value = 0, message = "压力评分范围应为0-10")
    @Max(value = 10, message = "压力评分范围应为0-10")
    private Integer stressScore;
    @NotNull(message = "自伤风险不能为空")
    private Integer selfHarmFlag;
    @NotNull(message = "紧急求助不能为空")
    private Integer emergencyFlag;

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
}
