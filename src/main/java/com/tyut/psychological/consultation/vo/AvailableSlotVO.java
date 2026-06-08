package com.tyut.psychological.consultation.vo;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailableSlotVO {
    private Long dutyScheduleId;
    private Long counselorId;
    private String counselorName;
    private LocalDate consultationDate;
    private Long slotId;
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long roomId;
    private String roomName;
    private Boolean available;
    private String disabledReason;

    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public String getCounselorName() { return counselorName; }
    public void setCounselorName(String counselorName) { this.counselorName = counselorName; }
    public LocalDate getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDate consultationDate) { this.consultationDate = consultationDate; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public String getDisabledReason() { return disabledReason; }
    public void setDisabledReason(String disabledReason) { this.disabledReason = disabledReason; }
}
