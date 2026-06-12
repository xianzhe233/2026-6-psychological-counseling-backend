package com.tyut.psychological.report.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.enums.ReportStatus;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.mapper.ConsultationScheduleMapper;
import com.tyut.psychological.consultation.service.CounselorAccessService;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.report.dto.CaseReportAdminQuery;
import com.tyut.psychological.report.dto.CaseReportQuery;
import com.tyut.psychological.report.dto.CaseReportRequest;
import com.tyut.psychological.report.entity.CaseReport;
import com.tyut.psychological.report.mapper.CaseReportMapper;
import com.tyut.psychological.report.vo.CaseReportVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaseReportService {
    private final CaseReportMapper caseReportMapper;
    private final CounselorAccessService counselorAccessService;
    private final ConsultationScheduleMapper consultationScheduleMapper;
    private final OperationLogService operationLogService;

    public CaseReportService(CaseReportMapper caseReportMapper,
                             CounselorAccessService counselorAccessService,
                             ConsultationScheduleMapper consultationScheduleMapper,
                             OperationLogService operationLogService) {
        this.caseReportMapper = caseReportMapper;
        this.counselorAccessService = counselorAccessService;
        this.consultationScheduleMapper = consultationScheduleMapper;
        this.operationLogService = operationLogService;
    }

    public PageResult<CaseReportVO> pageForCounselor(Long counselorUserId, CaseReportQuery query) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        query.setCounselorId(counselor.getId());
        normalizeQuery(query);
        List<CaseReportVO> records = caseReportMapper.pageForCounselor(query);
        long total = caseReportMapper.countForCounselor(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    public CaseReportVO getDetailForCounselor(Long counselorUserId, Long reportId) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        CaseReportVO detail = caseReportMapper.selectDetailForCounselor(reportId, counselor.getId());
        if (detail == null) {
            throw new BusinessException(404, "结案报告不存在或无权查看");
        }
        return detail;
    }

    @Transactional
    public CaseReportVO save(Long counselorUserId, CaseReportRequest request) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        validateStudentRelation(counselor.getId(), request.getStudentId());
        validateDraftRequest(request);

        CaseReport report = buildReport(counselor.getId(), request);
        report.setReportStatus(ReportStatus.DRAFT.name());
        caseReportMapper.insert(report);

        operationLogService.logSuccess("结案报告", "保存结案报告草稿",
                "学生ID: " + request.getStudentId() + "，报告ID: " + report.getId());

        return caseReportMapper.selectDetailForCounselor(report.getId(), counselor.getId());
    }

    @Transactional
    public CaseReportVO update(Long counselorUserId, Long reportId, CaseReportRequest request) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        CaseReport existing = requireOwnedDraft(reportId, counselor.getId());
        validateStudentRelation(counselor.getId(), request.getStudentId());
        validateDraftRequest(request);

        applyRequest(existing, request);
        existing.setReportStatus(ReportStatus.DRAFT.name());
        caseReportMapper.update(existing);

        operationLogService.logSuccess("结案报告", "更新结案报告草稿",
                "报告ID: " + reportId);

        return caseReportMapper.selectDetailForCounselor(reportId, counselor.getId());
    }

    @Transactional
    public CaseReportVO submit(Long counselorUserId, Long reportId) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        CaseReport existing = requireOwnedReport(reportId, counselor.getId());
        if (ReportStatus.SUBMITTED.name().equals(existing.getReportStatus())) {
            throw new BusinessException(400, "报告已提交，不能重复提交");
        }
        validateSubmitFields(existing);

        caseReportMapper.submit(reportId);
        consultationScheduleMapper.closeSchedulesByStudentAndCounselor(
                existing.getStudentId(), counselor.getId());

        operationLogService.logSuccess("结案报告", "提交结案报告",
                "报告ID: " + reportId + "，学生ID: " + existing.getStudentId());

        return caseReportMapper.selectDetailForCounselor(reportId, counselor.getId());
    }

    public PageResult<CaseReportVO> pageForAdmin(CaseReportAdminQuery query) {
        normalizeAdminQuery(query);
        List<CaseReportVO> records = caseReportMapper.pageForAdmin(query);
        long total = caseReportMapper.countForAdmin(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    public CaseReportVO getDetailForAdmin(Long reportId) {
        CaseReportVO detail = caseReportMapper.selectDetailForAdmin(reportId);
        if (detail == null) {
            throw new BusinessException(404, "已提交的结案报告不存在");
        }
        return detail;
    }

    private CaseReport requireOwnedDraft(Long reportId, Long counselorStaffId) {
        CaseReport report = requireOwnedReport(reportId, counselorStaffId);
        if (!ReportStatus.DRAFT.name().equals(report.getReportStatus())) {
            throw new BusinessException(400, "只有草稿状态的报告可以修改");
        }
        return report;
    }

    private CaseReport requireOwnedReport(Long reportId, Long counselorStaffId) {
        CaseReport report = caseReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(404, "结案报告不存在");
        }
        if (!counselorStaffId.equals(report.getCounselorId())) {
            throw new BusinessException(403, "无权操作该结案报告");
        }
        return report;
    }

    private void validateStudentRelation(Long counselorStaffId, Long studentId) {
        if (caseReportMapper.countCounselorStudentRelation(counselorStaffId, studentId) == 0) {
            throw new BusinessException(400, "只能为本人负责过的学生填写结案报告");
        }
    }

    private void validateDraftRequest(CaseReportRequest request) {
        if (request.getProblemTypeId() == null) {
            throw new BusinessException(400, "问题类型不能为空");
        }
        if (request.getTotalSessions() == null || request.getTotalSessions() < 1) {
            throw new BusinessException(400, "咨询总次数必须大于0");
        }
        if (request.getCloseType() == null || request.getCloseType().isBlank()) {
            throw new BusinessException(400, "结案类型不能为空");
        }
    }

    private void validateSubmitFields(CaseReport report) {
        if (report.getProblemTypeId() == null) {
            throw new BusinessException(400, "问题类型不能为空");
        }
        if (report.getTotalSessions() == null || report.getTotalSessions() < 1) {
            throw new BusinessException(400, "咨询总次数必须大于0");
        }
        if (report.getEffectSelfRating() == null || report.getEffectSelfRating().isBlank()) {
            throw new BusinessException(400, "咨询效果自评不能为空");
        }
        if (report.getCloseType() == null || report.getCloseType().isBlank()) {
            throw new BusinessException(400, "结案类型不能为空");
        }
    }

    private CaseReport buildReport(Long counselorStaffId, CaseReportRequest request) {
        CaseReport report = new CaseReport();
        report.setStudentId(request.getStudentId());
        report.setCounselorId(counselorStaffId);
        applyRequest(report, request);
        return report;
    }

    private void applyRequest(CaseReport report, CaseReportRequest request) {
        report.setStudentId(request.getStudentId());
        report.setProblemTypeId(request.getProblemTypeId());
        report.setTotalSessions(request.getTotalSessions());
        report.setEffectSelfRating(request.getEffectSelfRating());
        report.setCaseSummary(request.getCaseSummary());
        report.setCounselingEffect(request.getCounselingEffect());
        report.setSuggestion(request.getSuggestion());
        report.setCloseType(request.getCloseType());
    }

    private void normalizeQuery(CaseReportQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }

    private void normalizeAdminQuery(CaseReportAdminQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }
}
