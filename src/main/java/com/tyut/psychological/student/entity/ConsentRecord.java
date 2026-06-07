package com.tyut.psychological.student.entity;

import java.time.LocalDateTime;

public class ConsentRecord {
    private Long id;
    private Long formId;
    private Long studentId;
    private String consentVersion;
    private Integer signed;
    private LocalDateTime signTime;
    private String signIp;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }
    public Integer getSigned() { return signed; }
    public void setSigned(Integer signed) { this.signed = signed; }
    public LocalDateTime getSignTime() { return signTime; }
    public void setSignTime(LocalDateTime signTime) { this.signTime = signTime; }
    public String getSignIp() { return signIp; }
    public void setSignIp(String signIp) { this.signIp = signIp; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
