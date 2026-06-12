package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.enums.ScheduleStatus;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.dto.SaveConsultationRecordRequest;
import com.tyut.psychological.consultation.entity.ConsultationRecord;
import com.tyut.psychological.consultation.entity.ConsultationSchedule;
import com.tyut.psychological.consultation.mapper.ConsultationRecordMapper;
import com.tyut.psychological.consultation.vo.ConsultationRecordVO;
import com.tyut.psychological.profile.entity.StaffProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ConsultationRecordService {
    private static final Set<String> VALID_RECORD_STATUSES = Set.of(
            "COMPLETED", "ABSENT", "LEAVE", "DROPPED", "CLOSED");

    private final ConsultationRecordMapper consultationRecordMapper;
    private final ConsultationScheduleService consultationScheduleService;
    private final CounselorAccessService counselorAccessService;
    private final OperationLogService operationLogService;

    public ConsultationRecordService(ConsultationRecordMapper consultationRecordMapper,
                                     ConsultationScheduleService consultationScheduleService,
                                     CounselorAccessService counselorAccessService,
                                     OperationLogService operationLogService) {
        this.consultationRecordMapper = consultationRecordMapper;
        this.consultationScheduleService = consultationScheduleService;
        this.counselorAccessService = counselorAccessService;
        this.operationLogService = operationLogService;
    }

    public ConsultationRecordVO getBySchedule(Long counselorUserId, Long scheduleId) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        consultationScheduleService.requireOwnedSchedule(counselor.getId(), scheduleId);
        return consultationRecordMapper.selectByScheduleId(scheduleId);
    }

    @Transactional
    public ConsultationRecordVO save(Long counselorUserId, Long scheduleId, SaveConsultationRecordRequest request) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        ConsultationSchedule schedule = consultationScheduleService.requireOwnedSchedule(counselor.getId(), scheduleId);

        if (ScheduleStatus.CANCELED.name().equals(schedule.getScheduleStatus())) {
            throw new BusinessException(400, "已取消的咨询安排不能录入记录");
        }
        if (!VALID_RECORD_STATUSES.contains(request.getRecordStatus())) {
            throw new BusinessException(400, "记录状态无效");
        }
        if (request.getNeedClose() != 0 && request.getNeedClose() != 1) {
            throw new BusinessException(400, "是否需要结案取值无效");
        }

        ConsultationRecord existing = consultationRecordMapper.selectEntityByScheduleId(scheduleId);
        if (existing == null) {
            ConsultationRecord record = new ConsultationRecord();
            record.setScheduleId(scheduleId);
            record.setStudentId(schedule.getStudentId());
            record.setCounselorId(counselor.getId());
            record.setSessionNo(schedule.getSessionNo());
            record.setConsultationTime(request.getConsultationTime());
            record.setRecordStatus(request.getRecordStatus());
            record.setContentSummary(request.getContentSummary());
            record.setNextPlan(request.getNextPlan());
            record.setNeedClose(request.getNeedClose());
            consultationRecordMapper.insert(record);
        } else {
            existing.setConsultationTime(request.getConsultationTime());
            existing.setRecordStatus(request.getRecordStatus());
            existing.setContentSummary(request.getContentSummary());
            existing.setNextPlan(request.getNextPlan());
            existing.setNeedClose(request.getNeedClose());
            consultationRecordMapper.update(existing);
        }

        consultationScheduleService.updateStatus(scheduleId, request.getRecordStatus());

        operationLogService.logSuccess("咨询记录", "保存咨询记录",
                "安排ID: " + scheduleId + "，状态: " + request.getRecordStatus());

        return consultationRecordMapper.selectByScheduleId(scheduleId);
    }
}
