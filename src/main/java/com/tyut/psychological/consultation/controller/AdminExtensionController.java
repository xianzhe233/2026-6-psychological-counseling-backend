package com.tyut.psychological.consultation.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.consultation.dto.AuditExtensionRequest;
import com.tyut.psychological.consultation.dto.ExtensionAdminQuery;
import com.tyut.psychological.consultation.service.ExtensionRequestService;
import com.tyut.psychological.consultation.vo.ExtensionRequestVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/extension-requests")
public class AdminExtensionController {
    private final ExtensionRequestService extensionRequestService;

    public AdminExtensionController(ExtensionRequestService extensionRequestService) {
        this.extensionRequestService = extensionRequestService;
    }

    @GetMapping
    public Result<PageResult<ExtensionRequestVO>> pageExtensionRequests(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        ExtensionAdminQuery query = new ExtensionAdminQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setStatus(status);
        return Result.success(extensionRequestService.pageForAdmin(query));
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, HttpServletRequest request) {
        Long adminUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        extensionRequestService.approve(adminUserId, id);
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id,
                               @RequestBody(required = false) AuditExtensionRequest auditRequest,
                               HttpServletRequest request) {
        Long adminUserId = SessionUtils.getRequiredCurrentUser(request).getId();
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        String reason = auditRequest != null ? auditRequest.getReason() : null;
        extensionRequestService.reject(adminUserId, id, reason);
        return Result.success();
    }
}
