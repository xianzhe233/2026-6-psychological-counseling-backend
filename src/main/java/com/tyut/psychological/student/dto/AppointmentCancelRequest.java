package com.tyut.psychological.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 撤销预约请求DTO
 */
public class AppointmentCancelRequest {
    @NotBlank(message = "撤销原因不能为空")
    @Size(min = 2, max = 200, message = "撤销原因长度应在2-200字之间")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
