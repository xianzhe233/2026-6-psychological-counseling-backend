package com.tyut.psychological.student.dto;

import jakarta.validation.constraints.NotBlank;

public class AppointmentCancelRequest {
    @NotBlank(message = "撤销原因不能为空")
    private String reason;

    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}