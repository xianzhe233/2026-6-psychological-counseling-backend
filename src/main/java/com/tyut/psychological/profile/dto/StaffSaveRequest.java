package com.tyut.psychological.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class StaffSaveRequest {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String staffNo;
    @NotBlank(message = "工作人员类型不能为空")
    private String staffType;
    private String title;
    private String specialty;
    private String introduction;
    private Integer maxDailyAppointments;
    private Integer status = 1;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStaffNo() { return staffNo; }
    public void setStaffNo(String staffNo) { this.staffNo = staffNo; }
    public String getStaffType() { return staffType; }
    public void setStaffType(String staffType) { this.staffType = staffType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public Integer getMaxDailyAppointments() { return maxDailyAppointments; }
    public void setMaxDailyAppointments(Integer maxDailyAppointments) { this.maxDailyAppointments = maxDailyAppointments; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
