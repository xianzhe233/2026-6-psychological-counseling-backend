package com.tyut.psychological.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConsentSignRequest {
    @NotNull(message = "formId 不能为空")
    private Long formId;
    @NotBlank(message = "同意书版本不能为空")
    private String consentVersion;

    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }
}
