package com.tyut.psychological.statistics.dto;

import java.time.LocalDate;

public class StatisticsQuery {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long counselorId;
    private Long problemTypeId;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public Long getProblemTypeId() { return problemTypeId; }
    public void setProblemTypeId(Long problemTypeId) { this.problemTypeId = problemTypeId; }
}
