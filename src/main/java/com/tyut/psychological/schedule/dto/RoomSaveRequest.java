package com.tyut.psychological.schedule.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomSaveRequest {
    @NotBlank(message = "咨询室名称不能为空")
    private String roomName;
    private String location;
    private Integer capacity = 1;
    private Integer status = 1;
    private String remark;

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
