package com.tyut.psychological.common.log.vo;

import java.time.LocalDateTime;

public class OperationLogVO {
    private Long id;
    private String operatorName;
    private String roleName;
    private String moduleName;
    private String operationType;
    private String description;
    private String resultStatus;
    private Long durationMs;
    private String ipAddress;
    private LocalDateTime operateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getOperateTime() { return operateTime; }
    public void setOperateTime(LocalDateTime operateTime) { this.operateTime = operateTime; }
}
