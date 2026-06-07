package com.tyut.psychological.appointment.service;

import com.tyut.psychological.appointment.dto.ApproveRequest;
import com.tyut.psychological.appointment.dto.RejectRequest;
import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.appointment.vo.AppointmentAuditVO;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.common.notification.service.NotificationLogService;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.schedule.entity.DutySchedule;
import com.tyut.psychological.schedule.service.DutyScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 初访预约审核服务类
 * 实现预约审核列表、详情、通过、驳回、改约、优先标记等功能
 */
@Service
public class FirstVisitAppointmentService {
    private final FirstVisitAppointmentMapper appointmentMapper;
    private final DutyScheduleService dutyScheduleService;
    private final OperationLogService operationLogService;
    private final NotificationLogService notificationLogService;
    private final HttpServletRequest request;

    public FirstVisitAppointmentService(FirstVisitAppointmentMapper appointmentMapper,
                                       DutyScheduleService dutyScheduleService,
                                       OperationLogService operationLogService,
                                       NotificationLogService notificationLogService,
                                       HttpServletRequest request) {
        this.appointmentMapper = appointmentMapper;
        this.dutyScheduleService = dutyScheduleService;
        this.operationLogService = operationLogService;
        this.notificationLogService = notificationLogService;
        this.request = request;
    }

    /**
     * 分页查询审核列表
     * @param keyword 关键词
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param priorityFlag 优先标记
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageResult<AppointmentAuditVO> pageAuditList(String keyword, String status, String riskLevel,
                                                       LocalDate startDate, LocalDate endDate,
                                                       Integer priorityFlag, Integer pageNum, Integer pageSize) {
        List<AppointmentAuditVO> records = appointmentMapper.pageAuditList(keyword, status, riskLevel, startDate, endDate, priorityFlag);
        long total = appointmentMapper.countAuditList(keyword, status, riskLevel, startDate, endDate, priorityFlag);
        // 内存分页（数据量小）
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, records.size());
        List<AppointmentAuditVO> page = from < records.size() ? records.subList(from, to) : List.of();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(page, total, pageNum, pageSize, pages);
    }

    /**
     * 查询预约详情
     * @param id 预约ID
     * @return 预约详情
     */
    public AppointmentAuditVO getAppointmentDetail(Long id) {
        AppointmentAuditVO detail = appointmentMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        return detail;
    }

    /**
     * 审核通过
     * @param id 预约ID
     * @param request 审核通过请求
     */
    @Transactional
    public void approveAppointment(Long id, ApproveRequest approveRequest) {
        // 获取当前登录用户
        Long adminId = SessionUtils.getRequiredCurrentUser(request).getId();
        
        FirstVisitAppointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        
        // 只有PENDING状态可审核通过
        if (!"PENDING".equals(appointment.getAppointmentStatus())) {
            throw new BusinessException(400, "只有待审核状态的预约才能审核通过");
        }
        
        // 校验值班安排是否存在且可用
        DutySchedule dutySchedule = dutyScheduleService.getDutyScheduleById(approveRequest.getDutyScheduleId());
        if (dutySchedule == null) {
            throw new BusinessException(404, "值班安排不存在");
        }
        
        // 校验容量是否足够
        if (dutySchedule.getReservedCount() >= dutySchedule.getCapacity()) {
            throw new BusinessException(400, "值班容量已满");
        }
        
        // 更新预约状态
        appointment.setAppointmentStatus("APPROVED");
        appointment.setDutyScheduleId(approveRequest.getDutyScheduleId());
        appointment.setInterviewerId(approveRequest.getInterviewerId());
        appointment.setAppointmentDate(approveRequest.getAppointmentDate());
        appointment.setSlotId(approveRequest.getSlotId());
        appointment.setRoomId(approveRequest.getRoomId());
        appointment.setAuditAdminId(adminId);
        appointment.setAuditTime(LocalDateTime.now());
        appointment.setAuditRemark(approveRequest.getAuditRemark());
        
        appointmentMapper.update(appointment);
        
        // 增加值班安排的已预约数量
        dutyScheduleService.incrementReservedCount(approveRequest.getDutyScheduleId(), 1);
        
        // 写通知日志（这里简化处理，实际应该调用通知服务）
        writeNotificationLog(appointment, "APPOINTMENT_APPROVED");
        
        // 写操作日志（这里简化处理，实际应该调用日志服务）
        writeOperationLog("审核通过", "初访预约", "预约ID: " + id);
    }

    /**
     * 驳回预约
     * @param id 预约ID
     * @param request 驳回请求
     */
    @Transactional
    public void rejectAppointment(Long id, RejectRequest rejectRequest) {
        // 获取当前登录用户
        Long adminId = SessionUtils.getRequiredCurrentUser(request).getId();
        
        FirstVisitAppointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        
        // 只有PENDING状态可驳回
        if (!"PENDING".equals(appointment.getAppointmentStatus())) {
            throw new BusinessException(400, "只有待审核状态的预约才能驳回");
        }
        
        // 释放已占用的容量
        if (appointment.getDutyScheduleId() != null) {
            dutyScheduleService.decrementReservedCount(appointment.getDutyScheduleId(), 1);
        }
        
        // 更新预约状态
        appointment.setAppointmentStatus("REJECTED");
        appointment.setAuditAdminId(adminId);
        appointment.setAuditTime(LocalDateTime.now());
        appointment.setCancelReason(rejectRequest.getReason());
        
        appointmentMapper.update(appointment);
        
        // 写操作日志
        writeOperationLog("驳回预约", "初访预约", "预约ID: " + id);
    }

    /**
     * 改约
     * @param id 预约ID
     * @param request 改约请求（复用审核通过请求）
     */
    @Transactional
    public void rescheduleAppointment(Long id, ApproveRequest rescheduleRequest) {
        // 获取当前登录用户
        Long adminId = SessionUtils.getRequiredCurrentUser(request).getId();
        
        FirstVisitAppointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        
        // PENDING或APPROVED可改约
        if (!"PENDING".equals(appointment.getAppointmentStatus()) && 
            !"APPROVED".equals(appointment.getAppointmentStatus())) {
            throw new BusinessException(400, "只有待审核或已通过状态的预约才能改约");
        }
        
        // 释放旧值班安排的容量
        if (appointment.getDutyScheduleId() != null) {
            dutyScheduleService.decrementReservedCount(appointment.getDutyScheduleId(), 1);
        }
        
        // 校验新值班安排
        DutySchedule newDutySchedule = dutyScheduleService.getDutyScheduleById(rescheduleRequest.getDutyScheduleId());
        if (newDutySchedule == null) {
            throw new BusinessException(404, "新值班安排不存在");
        }
        
        // 校验新容量是否足够
        if (newDutySchedule.getReservedCount() >= newDutySchedule.getCapacity()) {
            throw new BusinessException(400, "新值班容量已满");
        }
        
        // 更新预约信息
        appointment.setDutyScheduleId(rescheduleRequest.getDutyScheduleId());
        appointment.setInterviewerId(rescheduleRequest.getInterviewerId());
        appointment.setAppointmentDate(rescheduleRequest.getAppointmentDate());
        appointment.setSlotId(rescheduleRequest.getSlotId());
        appointment.setRoomId(rescheduleRequest.getRoomId());
        appointment.setAuditAdminId(adminId);
        appointment.setAuditTime(LocalDateTime.now());
        appointment.setAuditRemark(rescheduleRequest.getAuditRemark());
        
        appointmentMapper.update(appointment);
        
        // 增加新值班安排的已预约数量
        dutyScheduleService.incrementReservedCount(rescheduleRequest.getDutyScheduleId(), 1);
        
        // 写通知日志
        writeNotificationLog(appointment, "APPOINTMENT_RESCHEDULED");
        
        // 写操作日志
        writeOperationLog("改约", "初访预约", "预约ID: " + id);
    }

    /**
     * 标记优先
     * @param id 预约ID
     */
    @Transactional
    public void markPriority(Long id) {
        FirstVisitAppointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        
        // 设置优先标记
        appointment.setPriorityFlag(1);
        appointmentMapper.update(appointment);
        
        // 写操作日志
        writeOperationLog("标记优先", "初访预约", "预约ID: " + id);
    }

    /**
     * 写通知日志
     * @param appointment 预约信息
     * @param notifyType 通知类型
     */
    private void writeNotificationLog(FirstVisitAppointment appointment, String notifyType) {
        // 获取学生信息
        AppointmentAuditVO studentInfo = appointmentMapper.selectDetailById(appointment.getId());
        if (studentInfo == null) {
            return;
        }
        
        // 获取时间段和咨询室信息
        String slotName = studentInfo.getSlotName();
        String roomName = studentInfo.getRoomName();
        String appointmentDate = studentInfo.getAppointmentDate() != null ? 
            studentInfo.getAppointmentDate().toString() : "";
        
        // 记录通知日志
        if ("APPOINTMENT_APPROVED".equals(notifyType)) {
            notificationLogService.logAppointmentApproved(
                appointment.getStudentId(),
                studentInfo.getStudentName(),
                null, // 手机号暂时为空
                appointment.getId(),
                appointmentDate,
                slotName,
                roomName
            );
        } else if ("APPOINTMENT_RESCHEDULED".equals(notifyType)) {
            notificationLogService.logAppointmentRescheduled(
                appointment.getStudentId(),
                studentInfo.getStudentName(),
                null, // 手机号暂时为空
                appointment.getId(),
                appointmentDate,
                slotName,
                roomName
            );
        }
    }

    /**
     * 写操作日志
     * @param operationType 操作类型
     * @param moduleName 模块名称
     * @param operationDesc 操作描述
     */
    private void writeOperationLog(String operationType, String moduleName, String operationDesc) {
        operationLogService.logSuccess(moduleName, operationType, operationDesc);
    }
}