package com.tyut.psychological.appointment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 学生取消预约请求DTO
 */
public class StudentAppointmentCancelRequest {
    @NotBlank(message = "取消原因不能为空")
    private String reason;

    // getter和setter方法
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}