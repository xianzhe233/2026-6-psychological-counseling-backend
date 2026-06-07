package com.tyut.psychological.common.log.service;

import com.tyut.psychological.common.log.entity.OperationLog;
import com.tyut.psychological.common.log.mapper.OperationLogMapper;
import com.tyut.psychological.common.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务类
 * 提供记录操作日志的功能
 */
@Service
public class OperationLogService {
    private final OperationLogMapper operationLogMapper;
    private final HttpServletRequest request;

    public OperationLogService(OperationLogMapper operationLogMapper, HttpServletRequest request) {
        this.operationLogMapper = operationLogMapper;
        this.request = request;
    }

    /**
     * 记录操作日志
     * @param moduleName 模块名称
     * @param operationType 操作类型
     * @param operationDesc 操作描述
     * @param resultStatus 结果状态
     */
    public void log(String moduleName, String operationType, String operationDesc, String resultStatus) {
        OperationLog operationLog = new OperationLog();
        
        // 获取当前登录用户信息
        try {
            Long userId = SessionUtils.getRequiredCurrentUser(request).getId();
            String realName = SessionUtils.getRequiredCurrentUser(request).getRealName();
            String roleCode = SessionUtils.getRequiredCurrentUser(request).getPrimaryRole().name();
            
            operationLog.setOperatorUserId(userId);
            operationLog.setOperatorName(realName);
            operationLog.setRoleCode(roleCode);
        } catch (Exception e) {
            // 如果获取用户信息失败，设置为未知
            operationLog.setOperatorUserId(0L);
            operationLog.setOperatorName("未知用户");
            operationLog.setRoleCode("UNKNOWN");
        }
        
        operationLog.setModuleName(moduleName);
        operationLog.setOperationType(operationType);
        operationLog.setOperationDesc(operationDesc);
        operationLog.setRequestUrl(request.getRequestURI());
        operationLog.setResultStatus(resultStatus);
        operationLog.setIpAddress(getClientIpAddress());
        operationLog.setExecutionTime(0L); // 简化处理，实际应该计算执行时间
        
        operationLogMapper.insert(operationLog);
    }

    /**
     * 记录成功操作日志
     * @param moduleName 模块名称
     * @param operationType 操作类型
     * @param operationDesc 操作描述
     */
    public void logSuccess(String moduleName, String operationType, String operationDesc) {
        log(moduleName, operationType, operationDesc, "SUCCESS");
    }

    /**
     * 记录失败操作日志
     * @param moduleName 模块名称
     * @param operationType 操作类型
     * @param operationDesc 操作描述
     * @param errorMessage 错误信息
     */
    public void logFailure(String moduleName, String operationType, String operationDesc, String errorMessage) {
        OperationLog operationLog = new OperationLog();
        
        // 获取当前登录用户信息
        try {
            Long userId = SessionUtils.getRequiredCurrentUser(request).getId();
            String realName = SessionUtils.getRequiredCurrentUser(request).getRealName();
            String roleCode = SessionUtils.getRequiredCurrentUser(request).getPrimaryRole().name();
            
            operationLog.setOperatorUserId(userId);
            operationLog.setOperatorName(realName);
            operationLog.setRoleCode(roleCode);
        } catch (Exception e) {
            // 如果获取用户信息失败，设置为未知
            operationLog.setOperatorUserId(0L);
            operationLog.setOperatorName("未知用户");
            operationLog.setRoleCode("UNKNOWN");
        }
        
        operationLog.setModuleName(moduleName);
        operationLog.setOperationType(operationType);
        operationLog.setOperationDesc(operationDesc);
        operationLog.setRequestUrl(request.getRequestURI());
        operationLog.setResultStatus("FAILED");
        operationLog.setErrorMessage(errorMessage);
        operationLog.setIpAddress(getClientIpAddress());
        operationLog.setExecutionTime(0L); // 简化处理，实际应该计算执行时间
        
        operationLogMapper.insert(operationLog);
    }

    /**
     * 获取客户端IP地址
     * @return IP地址
     */
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}