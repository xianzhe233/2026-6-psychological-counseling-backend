package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.enums.QueueStatus;
import com.tyut.psychological.common.enums.ScheduleStatus;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.consultation.dto.ArrangeConsultationRequest;
import com.tyut.psychological.consultation.dto.CounselorScheduleQuery;
import com.tyut.psychological.consultation.dto.ScheduleQuery;
import com.tyut.psychological.consultation.entity.ConsultationQueue;
import com.tyut.psychological.consultation.entity.ConsultationSchedule;
import com.tyut.psychological.consultation.mapper.ConsultationScheduleMapper;
import com.tyut.psychological.consultation.vo.ArrangeResultVO;
import com.tyut.psychological.consultation.vo.AvailableSlotVO;
import com.tyut.psychological.consultation.vo.ConflictInfoVO;
import com.tyut.psychological.consultation.vo.ConflictResponseVO;
import com.tyut.psychological.consultation.vo.ConsultationScheduleVO;
import com.tyut.psychological.consultation.vo.CounselorScheduleVO;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.schedule.entity.CounselingRoom;
import com.tyut.psychological.schedule.entity.TimeSlot;
import com.tyut.psychological.schedule.mapper.CounselingRoomMapper;
import com.tyut.psychological.schedule.mapper.DutyScheduleMapper;
import com.tyut.psychological.schedule.mapper.TimeSlotMapper;
import com.tyut.psychological.schedule.vo.DutyScheduleVO;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultationScheduleService {
    private final ConsultationScheduleMapper consultationScheduleMapper;
    private final ConsultationQueueService consultationQueueService;
    private final StaffProfileMapper staffProfileMapper;
    private final DutyScheduleMapper dutyScheduleMapper;
    private final CounselingRoomMapper counselingRoomMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final UserMapper userMapper;
    private final NotificationLogService notificationLogService;
    private final OperationLogService operationLogService;

    public ConsultationScheduleService(ConsultationScheduleMapper consultationScheduleMapper,
                                       ConsultationQueueService consultationQueueService,
                                       StaffProfileMapper staffProfileMapper,
                                       DutyScheduleMapper dutyScheduleMapper,
                                       CounselingRoomMapper counselingRoomMapper,
                                       TimeSlotMapper timeSlotMapper,
                                       UserMapper userMapper,
                                       NotificationLogService notificationLogService,
                                       OperationLogService operationLogService) {
        this.consultationScheduleMapper = consultationScheduleMapper;
        this.consultationQueueService = consultationQueueService;
        this.staffProfileMapper = staffProfileMapper;
        this.dutyScheduleMapper = dutyScheduleMapper;
        this.counselingRoomMapper = counselingRoomMapper;
        this.timeSlotMapper = timeSlotMapper;
        this.userMapper = userMapper;
        this.notificationLogService = notificationLogService;
        this.operationLogService = operationLogService;
    }

    public List<AvailableSlotVO> getCounselorAvailableSlots(Long counselorId, LocalDate startDate) {
        validateCounselor(counselorId);
        if (startDate == null) {
            throw new BusinessException(400, "开始日期不能为空");
        }

        List<DutyScheduleVO> dutySchedules = dutyScheduleMapper.pageDutySchedules(
                "COUNSELOR", counselorId, startDate, startDate, 1);
        List<AvailableSlotVO> result = new ArrayList<>();
        for (DutyScheduleVO duty : dutySchedules) {
            AvailableSlotVO slot = new AvailableSlotVO();
            slot.setDutyScheduleId(duty.getId());
            slot.setCounselorId(counselorId);
            slot.setCounselorName(duty.getStaffName());
            slot.setConsultationDate(duty.getDutyDate());
            slot.setSlotId(duty.getSlotId());
            slot.setSlotName(duty.getSlotName());
            slot.setStartTime(duty.getStartTime());
            slot.setEndTime(duty.getEndTime());
            slot.setRoomId(duty.getRoomId());
            slot.setRoomName(duty.getRoomName());

            String disabledReason = resolveDisabledReason(counselorId, duty.getDutyDate(), duty.getSlotId(), duty.getRoomId());
            if (disabledReason == null) {
                slot.setAvailable(true);
                slot.setDisabledReason(null);
            } else {
                slot.setAvailable(false);
                slot.setDisabledReason(disabledReason);
            }
            result.add(slot);
        }
        return result;
    }

    @Transactional
    public ArrangeResultVO arrange(Long assistantUserId, ArrangeConsultationRequest request) {
        ConsultationQueue queue = consultationQueueService.getRequired(request.getQueueId());
        if (!QueueStatus.WAITING.name().equals(queue.getQueueStatus())) {
            throw new BusinessException(400, "队列状态必须为等待安排");
        }
        if (!queue.getStudentId().equals(request.getStudentId())) {
            throw new BusinessException(400, "学生信息与队列不匹配");
        }

        StaffProfile counselor = validateCounselor(request.getCounselorId());
        StaffProfile assistant = requireAssistant(assistantUserId);

        CounselingRoom room = counselingRoomMapper.selectById(request.getRoomId());
        if (room == null || room.getStatus() != 1) {
            throw new BusinessException(404, "咨询室不存在或已停用");
        }
        TimeSlot timeSlot = timeSlotMapper.selectById(request.getSlotId());
        if (timeSlot == null || timeSlot.getStatus() != 1) {
            throw new BusinessException(404, "时间段不存在或已停用");
        }

        List<ConflictInfoVO> conflicts = collectConflicts(
                request.getCounselorId(),
                request.getStudentId(),
                request.getRoomId(),
                request.getConsultationDate(),
                request.getSlotId(),
                null);
        if (!conflicts.isEmpty()) {
            throw new BusinessException(409, "该咨询时间存在冲突",
                    new ConflictResponseVO(conflicts));
        }

        Integer sessionNo = consultationScheduleMapper.selectNextSessionNo(
                request.getStudentId(), request.getCounselorId());
        if (sessionNo == null) {
            sessionNo = 1;
        }

        ConsultationSchedule schedule = new ConsultationSchedule();
        schedule.setScheduleNo(generateScheduleNo(request.getConsultationDate()));
        schedule.setQueueId(request.getQueueId());
        schedule.setStudentId(request.getStudentId());
        schedule.setCounselorId(request.getCounselorId());
        schedule.setAssistantId(assistant.getId());
        schedule.setConsultationDate(request.getConsultationDate());
        schedule.setSlotId(request.getSlotId());
        schedule.setRoomId(request.getRoomId());
        schedule.setSessionNo(sessionNo);
        schedule.setSourceType("QUEUE");
        schedule.setScheduleStatus(ScheduleStatus.RESERVED.name());
        consultationScheduleMapper.insert(schedule);

        consultationQueueService.markArranged(request.getQueueId());

        SysUser student = userMapper.selectById(request.getStudentId());
        if (student != null) {
            notificationLogService.logConsultationArranged(
                    student.getId(),
                    student.getRealName(),
                    student.getPhone(),
                    schedule.getId(),
                    request.getConsultationDate().toString(),
                    timeSlot.getSlotName(),
                    room.getRoomName());
        }

        String operationDesc = "安排编号: " + schedule.getScheduleNo()
                + "，学生ID: " + request.getStudentId()
                + "，咨询师ID: " + request.getCounselorId();
        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            operationDesc += "，备注: " + request.getRemark();
        }
        operationLogService.logSuccess("咨询安排", "安排正式咨询", operationDesc);

        return new ArrangeResultVO(schedule.getId(), schedule.getScheduleNo());
    }

    public PageResult<CounselorScheduleVO> pageForCounselor(Long counselorUserId, CounselorScheduleQuery query) {
        StaffProfile counselor = validateCounselorByUserId(counselorUserId);
        query.setCounselorId(counselor.getId());
        normalizeCounselorQuery(query);
        List<CounselorScheduleVO> records = consultationScheduleMapper.pageForCounselor(query);
        long total = consultationScheduleMapper.countForCounselor(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    public CounselorScheduleVO getCounselorScheduleDetail(Long counselorUserId, Long scheduleId) {
        StaffProfile counselor = validateCounselorByUserId(counselorUserId);
        CounselorScheduleVO detail = consultationScheduleMapper.selectDetailForCounselor(scheduleId, counselor.getId());
        if (detail == null) {
            throw new BusinessException(404, "咨询安排不存在或无权查看");
        }
        return detail;
    }

    public ConsultationSchedule requireOwnedSchedule(Long counselorStaffId, Long scheduleId) {
        ConsultationSchedule schedule = consultationScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(404, "咨询安排不存在");
        }
        if (!counselorStaffId.equals(schedule.getCounselorId())) {
            throw new BusinessException(403, "无权操作该咨询安排");
        }
        return schedule;
    }

    public void updateStatus(Long scheduleId, String scheduleStatus) {
        consultationScheduleMapper.updateStatus(scheduleId, scheduleStatus);
    }

    public PageResult<ConsultationScheduleVO> pageForAssistant(ScheduleQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        List<ConsultationScheduleVO> records = consultationScheduleMapper.pageForAssistant(query);
        long total = consultationScheduleMapper.countForAssistant(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    @Transactional
    public void cancel(Long assistantUserId, Long scheduleId, String reason) {
        ConsultationSchedule schedule = consultationScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(404, "咨询安排不存在");
        }
        if (!ScheduleStatus.RESERVED.name().equals(schedule.getScheduleStatus())) {
            throw new BusinessException(400, "只有已预约状态的安排才能取消");
        }

        requireAssistant(assistantUserId);

        consultationScheduleMapper.updateStatus(scheduleId, ScheduleStatus.CANCELED.name());
        if (schedule.getQueueId() != null) {
            consultationQueueService.revertToWaitingIfNeeded(schedule.getQueueId());
        }

        TimeSlot timeSlot = timeSlotMapper.selectById(schedule.getSlotId());
        CounselingRoom room = schedule.getRoomId() != null
                ? counselingRoomMapper.selectById(schedule.getRoomId()) : null;
        SysUser student = userMapper.selectById(schedule.getStudentId());
        if (student != null) {
            notificationLogService.logConsultationCanceled(
                    student.getId(),
                    student.getRealName(),
                    student.getPhone(),
                    scheduleId,
                    schedule.getConsultationDate().toString(),
                    timeSlot != null ? timeSlot.getSlotName() : "",
                    room != null ? room.getRoomName() : "",
                    reason);
        }

        operationLogService.logSuccess("咨询安排", "取消咨询安排",
                "安排ID: " + scheduleId + "，原因: " + reason);
    }

    private void normalizeCounselorQuery(CounselorScheduleQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }

    private StaffProfile validateCounselorByUserId(Long counselorUserId) {
        StaffProfile counselor = staffProfileMapper.selectByUserId(counselorUserId);
        if (counselor == null) {
            throw new BusinessException(400, "咨询师信息无效");
        }
        return validateCounselor(counselor.getId());
    }

    private StaffProfile requireAssistant(Long assistantUserId) {
        StaffProfile assistant = staffProfileMapper.selectByUserId(assistantUserId);
        if (assistant == null || assistant.getStatus() != 1) {
            throw new BusinessException(400, "心理助理信息无效");
        }
        if (!"ASSISTANT".equals(assistant.getStaffType())) {
            throw new BusinessException(403, "当前人员无权执行心理助理操作");
        }
        return assistant;
    }

    private StaffProfile validateCounselor(Long counselorId) {
        StaffProfile counselor = staffProfileMapper.selectById(counselorId);
        if (counselor == null) {
            throw new BusinessException(404, "咨询师不存在");
        }
        if (!"COUNSELOR".equals(counselor.getStaffType())) {
            throw new BusinessException(400, "所选人员不是咨询师");
        }
        if (counselor.getStatus() != 1) {
            throw new BusinessException(400, "咨询师未启用");
        }
        return counselor;
    }

    private String resolveDisabledReason(Long counselorId, LocalDate date, Long slotId, Long roomId) {
        if (consultationScheduleMapper.countCounselorDuty(counselorId, date, slotId) == 0) {
            return "咨询师该日期未排值班";
        }
        if (consultationScheduleMapper.checkCounselorConflict(counselorId, date, slotId, null) > 0) {
            return "咨询师该时间已有安排";
        }
        if (roomId != null && consultationScheduleMapper.checkRoomConflict(roomId, date, slotId, null) > 0) {
            return "咨询室该时间已被占用";
        }
        return null;
    }

    private List<ConflictInfoVO> collectConflicts(Long counselorId,
                                                  Long studentId,
                                                  Long roomId,
                                                  LocalDate consultationDate,
                                                  Long slotId,
                                                  Long excludeId) {
        List<ConflictInfoVO> conflicts = new ArrayList<>();
        String dateStr = consultationDate.toString();

        if (consultationScheduleMapper.countCounselorDuty(counselorId, consultationDate, slotId) == 0) {
            conflicts.add(new ConflictInfoVO(dateStr, slotId, "咨询师该日期未排值班"));
        }
        if (consultationScheduleMapper.checkCounselorConflict(counselorId, consultationDate, slotId, excludeId) > 0) {
            conflicts.add(new ConflictInfoVO(dateStr, slotId, "咨询师该时间已有安排"));
        }
        if (consultationScheduleMapper.checkStudentConflict(studentId, consultationDate, slotId, excludeId) > 0) {
            conflicts.add(new ConflictInfoVO(dateStr, slotId, "学生该时间已有安排"));
        }
        if (consultationScheduleMapper.checkRoomConflict(roomId, consultationDate, slotId, excludeId) > 0) {
            conflicts.add(new ConflictInfoVO(dateStr, slotId, "咨询室该时间已被占用"));
        }
        return conflicts;
    }

    private String generateScheduleNo(LocalDate consultationDate) {
        String prefix = "CS" + consultationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = consultationScheduleMapper.countScheduleNoByPrefix(prefix);
        return prefix + String.format("%04d", count + 1);
    }
}
