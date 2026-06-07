package com.tyut.psychological.schedule.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 值班安排保存请求DTO
 */
public class DutyScheduleSaveRequest {
    @NotNull(message = "工作人员ID不能为空")
    private Long staffId;
    
    @NotNull(message = "工作人员类型不能为空")
    private String staffType;
    
    @NotNull(message = "值班日期不能为空")
    private LocalDate dutyDate;
    
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;
    
    private Long roomId;
    
    @NotNull(message = "容量不能为空")
    private Integer capacity;
    
    private Integer status = 1;

    // getter和setter方法
    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    
    public String getStaffType() { return staffType; }
    public void setStaffType(String staffType) { this.staffType = staffType; }
    
    public LocalDate getDutyDate() { return dutyDate; }
    public void setDutyDate(LocalDate dutyDate) { this.dutyDate = dutyDate; }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}