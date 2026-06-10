package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.consultation.dto.ExtensionAdminQuery;
import com.tyut.psychological.consultation.dto.ExtensionCreateRequest;
import com.tyut.psychological.consultation.dto.ExtensionQuery;
import com.tyut.psychological.consultation.entity.ExtensionRequest;
import com.tyut.psychological.consultation.mapper.ExtensionRequestMapper;
import com.tyut.psychological.consultation.vo.ExtensionRequestVO;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExtensionRequestService {
    private final ExtensionRequestMapper extensionRequestMapper;
    private final CounselorAccessService counselorAccessService;
    private final NotificationLogService notificationLogService;
    private final OperationLogService operationLogService;
    private final StaffProfileMapper staffProfileMapper;
    private final UserMapper userMapper;

    public ExtensionRequestService(ExtensionRequestMapper extensionRequestMapper,
                                   CounselorAccessService counselorAccessService,
                                   NotificationLogService notificationLogService,
                                   OperationLogService operationLogService,
                                   StaffProfileMapper staffProfileMapper,
                                   UserMapper userMapper) {
        this.extensionRequestMapper = extensionRequestMapper;
        this.counselorAccessService = counselorAccessService;
        this.notificationLogService = notificationLogService;
        this.operationLogService = operationLogService;
        this.staffProfileMapper = staffProfileMapper;
        this.userMapper = userMapper;
    }

    public PageResult<ExtensionRequestVO> pageForCounselor(Long counselorUserId, ExtensionQuery query) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        query.setCounselorId(counselor.getId());
        normalizeQuery(query);
        List<ExtensionRequestVO> records = extensionRequestMapper.pageForCounselor(query);
        long total = extensionRequestMapper.countForCounselor(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    @Transactional
    public ExtensionRequestVO create(Long counselorUserId, ExtensionCreateRequest request) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new BusinessException(400, "申请原因不能为空");
        }
        if (extensionRequestMapper.countCounselorStudentRelation(counselor.getId(), request.getStudentId()) == 0) {
            throw new BusinessException(400, "只能为本人负责过的学生提交追加申请");
        }

        ExtensionRequest entity = new ExtensionRequest();
        entity.setStudentId(request.getStudentId());
        entity.setCounselorId(counselor.getId());
        entity.setRequestSessions(request.getRequestSessions());
        entity.setReason(request.getReason().trim());
        entity.setRequestStatus("PENDING");
        extensionRequestMapper.insert(entity);

        operationLogService.logSuccess("追加咨询", "提交追加申请",
                "学生ID: " + request.getStudentId() + "，次数: " + request.getRequestSessions());

        ExtensionRequestVO vo = new ExtensionRequestVO();
        vo.setId(entity.getId());
        vo.setStudentId(entity.getStudentId());
        vo.setRequestSessions(entity.getRequestSessions());
        vo.setReason(entity.getReason());
        vo.setStatus(entity.getRequestStatus());
        SysUser student = userMapper.selectById(request.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
        }
        return vo;
    }

    public PageResult<ExtensionRequestVO> pageForAdmin(ExtensionAdminQuery query) {
        normalizeAdminQuery(query);
        List<ExtensionRequestVO> records = extensionRequestMapper.pageForAdmin(query);
        long total = extensionRequestMapper.countForAdmin(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    @Transactional
    public void approve(Long adminUserId, Long requestId) {
        audit(adminUserId, requestId, "APPROVED", null);
    }

    @Transactional
    public void reject(Long adminUserId, Long requestId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException(400, "驳回原因不能为空");
        }
        audit(adminUserId, requestId, "REJECTED", reason.trim());
    }

    private void audit(Long adminUserId, Long requestId, String status, String remark) {
        ExtensionRequest request = extensionRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException(404, "追加申请不存在");
        }
        if (!"PENDING".equals(request.getRequestStatus())) {
            throw new BusinessException(400, "该申请已审核，不能重复操作");
        }

        extensionRequestMapper.updateAudit(requestId, status, adminUserId, remark);

        StaffProfile counselor = staffProfileMapper.selectById(request.getCounselorId());
        if (counselor != null) {
            SysUser counselorUser = userMapper.selectById(counselor.getUserId());
            if (counselorUser != null) {
                notificationLogService.logExtensionAudited(
                        counselorUser.getId(),
                        counselorUser.getRealName(),
                        counselorUser.getPhone(),
                        requestId,
                        status,
                        remark);
            }
        }

        String action = "APPROVED".equals(status) ? "通过追加申请" : "驳回追加申请";
        operationLogService.logSuccess("追加咨询", action,
                "申请ID: " + requestId + (remark != null ? "，备注: " + remark : ""));
    }

    private void normalizeQuery(ExtensionQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }

    private void normalizeAdminQuery(ExtensionAdminQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }
}
