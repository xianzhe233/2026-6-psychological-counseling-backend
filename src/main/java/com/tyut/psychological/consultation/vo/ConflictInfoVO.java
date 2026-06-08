package com.tyut.psychological.consultation.vo;

public class ConflictInfoVO {
    private String date;
    private Long slotId;
    private String reason;

    public ConflictInfoVO() {
    }

    public ConflictInfoVO(String date, Long slotId, String reason) {
        this.date = date;
        this.slotId = slotId;
        this.reason = reason;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
