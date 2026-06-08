package com.tyut.psychological.student.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AppointmentCreateRequest {
    @NotNull(message = "登记表ID不能为空")
    private Long formId;
    
    @NotNull(message = "值班安排ID不能为空")
    private Long dutyScheduleId;
    
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;
    
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;
    
    private Long interviewerId;
    private Long roomId;

    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
    public Long getDutyScheduleId() { return dutyScheduleId; }
    public void setDutyScheduleId(Long dutyScheduleId) { this.dutyScheduleId = dutyScheduleId; }
    
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public Long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(Long interviewerId) { this.interviewerId = interviewerId; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}