package com.tyut.psychological.consultation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExtensionCreateRequest {
    @NotNull(message = "学生不能为空")
    private Long studentId;
    @NotNull(message = "追加次数不能为空")
    @Min(value = 1, message = "追加次数必须大于0")
    private Integer requestSessions;
    @NotBlank(message = "申请原因不能为空")
    private String reason;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Integer getRequestSessions() { return requestSessions; }
    public void setRequestSessions(Integer requestSessions) { this.requestSessions = requestSessions; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
