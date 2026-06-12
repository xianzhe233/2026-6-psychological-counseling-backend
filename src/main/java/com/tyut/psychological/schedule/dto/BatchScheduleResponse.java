package com.tyut.psychological.schedule.dto;

import java.util.List;

/**
 * 批量排班响应DTO
 */
public class BatchScheduleResponse {
    private Integer createdCount;
    private Integer skippedCount;
    private List<ConflictInfo> conflicts;

    // getter和setter方法
    public Integer getCreatedCount() { return createdCount; }
    public void setCreatedCount(Integer createdCount) { this.createdCount = createdCount; }
    
    public Integer getSkippedCount() { return skippedCount; }
    public void setSkippedCount(Integer skippedCount) { this.skippedCount = skippedCount; }
    
    public List<ConflictInfo> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictInfo> conflicts) { this.conflicts = conflicts; }

    /**
     * 冲突信息内部类
     */
    public static class ConflictInfo {
        private String date;
        private Long slotId;
        private String reason;

        public ConflictInfo(String date, Long slotId, String reason) {
            this.date = date;
            this.slotId = slotId;
            this.reason = reason;
        }

        // getter和setter方法
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public Long getSlotId() { return slotId; }
        public void setSlotId(Long slotId) { this.slotId = slotId; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}