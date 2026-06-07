package com.tyut.psychological.student.vo;

import java.time.LocalDateTime;

public class ConsentStatusVO {
    private Long formId;
    private Boolean signed;
    private LocalDateTime signTime;
    private String consentVersion;

    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public Boolean getSigned() { return signed; }
    public void setSigned(Boolean signed) { this.signed = signed; }
    public LocalDateTime getSignTime() { return signTime; }
    public void setSignTime(LocalDateTime signTime) { this.signTime = signTime; }
    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }
}
