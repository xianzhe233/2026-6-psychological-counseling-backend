package com.tyut.psychological.appointment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 驳回请求DTO
 */
public class RejectRequest {
    @NotBlank(message = "驳回原因不能为空")
    private String reason;

    // getter和setter方法
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}