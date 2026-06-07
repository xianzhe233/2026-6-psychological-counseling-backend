package com.tyut.psychological.student.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.student.dto.*;
import com.tyut.psychological.student.entity.*;
import com.tyut.psychological.student.mapper.StudentMapper;
import com.tyut.psychological.student.vo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;
    private final HttpSession session;

    public StudentServiceImpl(StudentMapper studentMapper, HttpSession session) {
        this.studentMapper = studentMapper;
        this.session = session;
    }

    private Long getCurrentStudentId() {
        // 从Session中获取当前学生ID
        // 这里需要根据实际的Session结构来获取
        // 暂时返回固定值，实际应该从Session中获取
        return 1L;
    }

    @Override
    public FirstVisitFormVO getLatestFirstVisitForm() {
        Long studentId = getCurrentStudentId();
        FirstVisitForm form = studentMapper.selectLatestFirstVisitForm(studentId);
        if (form == null) {
            return null;
        }
        return convertToFirstVisitFormVO(form);
    }

    @Override
    @Transactional
    public FirstVisitFormVO saveFirstVisitForm(FirstVisitFormRequest request) {
        Long studentId = getCurrentStudentId();
        
        // 计算风险分数
        int riskScore = calculateRiskScore(request);
        String riskLevel = calculateRiskLevel(riskScore);
        
        FirstVisitForm form = new FirstVisitForm();
        form.setStudentId(studentId);
        form.setMainProblem(request.getMainProblem());
        form.setProblemDescription(request.getProblemDescription());
        form.setExpectedHelp(request.getExpectedHelp());
        form.setMoodScore(request.getMoodScore());
        form.setSleepScore(request.getSleepScore());
        form.setStressScore(request.getStressScore());
        form.setSelfHarmFlag(request.getSelfHarmFlag());
        form.setEmergencyFlag(request.getEmergencyFlag());
        form.setRiskScore(riskScore);
        form.setRiskLevel(riskLevel);
        form.setFormStatus("SUBMITTED");
        form.setSubmitTime(LocalDateTime.now());
        form.setCreateTime(LocalDateTime.now());
        form.setUpdateTime(LocalDateTime.now());
        
        // 检查是否已有表单，有则更新，无则插入
        FirstVisitForm existingForm = studentMapper.selectLatestFirstVisitForm(studentId);
        if (existingForm != null) {
            form.setId(existingForm.getId());
            studentMapper.updateFirstVisitForm(form);
        } else {
            studentMapper.insertFirstVisitForm(form);
        }
        
        return convertToFirstVisitFormVO(form);
    }

    private int calculateRiskScore(FirstVisitFormRequest request) {
        int score = 0;
        score += (10 - request.getMoodScore()) * 2; // 情绪评分越低风险越高
        score += (10 - request.getSleepScore()) * 2; // 睡眠评分越低风险越高
        score += request.getStressScore() * 2; // 压力评分越高风险越高
        if (request.getSelfHarmFlag() == 1) {
            score += 20; // 自伤风险加分
        }
        if (request.getEmergencyFlag() == 1) {
            score += 15; // 紧急求助加分
        }
        return score;
    }

    private String calculateRiskLevel(int riskScore) {
        if (riskScore >= 50) {
            return "URGENT";
        } else if (riskScore >= 40) {
            return "HIGH";
        } else if (riskScore >= 30) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    @Override
    public ConsentStatusVO getConsentStatus(Long formId) {
        Long studentId = getCurrentStudentId();
        ConsentRecord record = studentMapper.selectConsentRecordByFormId(formId);
        
        ConsentStatusVO status = new ConsentStatusVO();
        status.setFormId(formId);
        
        if (record != null) {
            status.setSigned(true);
            status.setSignTime(record.getSignTime());
            status.setConsentVersion(record.getConsentVersion());
        } else {
            status.setSigned(false);
        }
        
        return status;
    }

    @Override
    @Transactional
    public void signConsent(ConsentSignRequest request) {
        Long studentId = getCurrentStudentId();
        
        // 检查是否已签署
        ConsentRecord existingRecord = studentMapper.selectConsentRecordByFormId(request.getFormId());
        if (existingRecord != null) {
            throw new BusinessException(409, "该表单已签署同意书");
        }
        
        ConsentRecord record = new ConsentRecord();
        record.setFormId(request.getFormId());
        record.setStudentId(studentId);
        record.setConsentVersion(request.getConsentVersion());
        record.setSignTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        
        studentMapper.insertConsentRecord(record);
    }

    @Override
    public List<AvailableSlotVO> getAvailableSlots(String date, Long interviewerId) {
        List<DutySchedule> schedules = studentMapper.selectAvailableDutySchedules(date, interviewerId);
        List<AvailableSlotVO> slots = new ArrayList<>();
        
        for (DutySchedule schedule : schedules) {
            AvailableSlotVO slot = new AvailableSlotVO();
            slot.setDutyScheduleId(schedule.getId());
            slot.setInterviewerId(schedule.getStaffId());
            slot.setInterviewerName("初访员"); // 需要关联查询真实姓名
            slot.setAppointmentDate(schedule.getDutyDate());
            slot.setSlotId(schedule.getSlotId());
            slot.setSlotName(schedule.getSlotName());
            slot.setStartTime(schedule.getStartTime());
            slot.setEndTime(schedule.getEndTime());
            slot.setRoomId(schedule.getRoomId());
            slot.setRoomName(schedule.getRoomName());
            slot.setCapacity(schedule.getCapacity());
            slot.setReservedCount(schedule.getReservedCount());
            slot.setRemaining(schedule.getCapacity() - schedule.getReservedCount());
            
            boolean available = schedule.getReservedCount() < schedule.getCapacity();
            slot.setAvailable(available);
            if (!available) {
                slot.setDisabledReason("该时间段已满");
            }
            
            slots.add(slot);
        }
        
        return slots;
    }

    @Override
    @Transactional
    public AppointmentCreateVO createAppointment(AppointmentCreateRequest request) {
        Long studentId = getCurrentStudentId();
        
        // 检查是否已签署同意书
        ConsentRecord consentRecord = studentMapper.selectConsentRecordByFormId(request.getFormId());
        if (consentRecord == null) {
            throw new BusinessException(400, "请先签署知情同意书");
        }
        
        // 检查是否有重复预约
        List<FirstVisitAppointment> existingAppointments = studentMapper.selectAppointmentsByStudentId(
            studentId, "PENDING", 0, 10);
        if (!existingAppointments.isEmpty()) {
            throw new BusinessException(409, "您已有待审核的预约，请等待审核或撤销后再预约");
        }
        
        // 检查值班安排是否存在且有容量
        DutySchedule schedule = studentMapper.selectDutyScheduleById(request.getDutyScheduleId());
        if (schedule == null) {
            throw new BusinessException(404, "值班安排不存在");
        }
        if (schedule.getReservedCount() >= schedule.getCapacity()) {
            throw new BusinessException(409, "该时间段已满，请选择其他时间段");
        }
        
        // 创建预约
        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setFormId(request.getFormId());
        appointment.setStudentId(studentId);
        appointment.setDutyScheduleId(request.getDutyScheduleId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setSlotId(request.getSlotId());
        appointment.setSlotName(schedule.getSlotName());
        appointment.setStartTime(schedule.getStartTime());
        appointment.setEndTime(schedule.getEndTime());
        appointment.setInterviewerId(request.getInterviewerId());
        appointment.setInterviewerName("初访员"); // 需要关联查询真实姓名
        appointment.setRoomId(request.getRoomId());
        appointment.setRoomName(schedule.getRoomName());
        appointment.setAppointmentStatus("PENDING");
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        studentMapper.insertFirstVisitAppointment(appointment);
        
        // 更新值班安排的已预约数量
        studentMapper.updateDutyScheduleReservedCount(request.getDutyScheduleId(), 1);
        
        AppointmentCreateVO result = new AppointmentCreateVO();
        result.setId(appointment.getId());
        result.setAppointmentNo(appointment.getAppointmentNo());
        result.setAppointmentStatus(appointment.getAppointmentStatus());
        
        return result;
    }

    private String generateAppointmentNo() {
        return "FV" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4);
    }

    @Override
    public PageResult<AppointmentVO> getMyAppointments(Integer pageNum, Integer pageSize, String status) {
        Long studentId = getCurrentStudentId();
        
        int offset = (pageNum - 1) * pageSize;
        List<FirstVisitAppointment> appointments = studentMapper.selectAppointmentsByStudentId(
            studentId, status, offset, pageSize);
        long total = studentMapper.countAppointmentsByStudentId(studentId, status);
        
        List<AppointmentVO> records = new ArrayList<>();
        for (FirstVisitAppointment appointment : appointments) {
            AppointmentVO vo = new AppointmentVO();
            vo.setId(appointment.getId());
            vo.setAppointmentNo(appointment.getAppointmentNo());
            vo.setAppointmentDate(appointment.getAppointmentDate());
            vo.setSlotName(appointment.getSlotName());
            vo.setStartTime(appointment.getStartTime());
            vo.setEndTime(appointment.getEndTime());
            vo.setInterviewerName(appointment.getInterviewerName());
            vo.setRoomName(appointment.getRoomName());
            vo.setAppointmentStatus(appointment.getAppointmentStatus());
            vo.setAuditRemark(appointment.getAuditRemark());
            vo.setRejectReason(appointment.getRejectReason());
            vo.setCreateTime(appointment.getCreateTime());
            records.add(vo);
        }
        
        PageResult<AppointmentVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((total + pageSize - 1) / pageSize);
        
        return result;
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, AppointmentCancelRequest request) {
        Long studentId = getCurrentStudentId();
        
        FirstVisitAppointment appointment = studentMapper.selectAppointmentById(id);
        if (appointment == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        if (!appointment.getStudentId().equals(studentId)) {
            throw new BusinessException(403, "无权操作此预约");
        }
        if (!"PENDING".equals(appointment.getAppointmentStatus())) {
            throw new BusinessException(400, "只有待审核的预约可以撤销");
        }
        
        // 更新预约状态
        studentMapper.updateAppointmentStatus(id, "CANCELED", request.getReason());
        
        // 释放值班容量
        studentMapper.updateDutyScheduleReservedCount(appointment.getDutyScheduleId(), -1);
    }

    @Override
    public PageResult<NotificationVO> getMyNotifications(Integer pageNum, Integer pageSize) {
        Long studentId = getCurrentStudentId();
        
        int offset = (pageNum - 1) * pageSize;
        List<NotificationLog> notifications = studentMapper.selectNotificationsByStudentId(
            studentId, offset, pageSize);
        long total = studentMapper.countNotificationsByStudentId(studentId);
        
        List<NotificationVO> records = new ArrayList<>();
        for (NotificationLog notification : notifications) {
            NotificationVO vo = new NotificationVO();
            vo.setId(notification.getId());
            vo.setTitle(notification.getTitle());
            vo.setContent(notification.getContent());
            vo.setNotifyType(notification.getNotifyType());
            vo.setSendTime(notification.getSendTime());
            records.add(vo);
        }
        
        PageResult<NotificationVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((total + pageSize - 1) / pageSize);
        
        return result;
    }

    private FirstVisitFormVO convertToFirstVisitFormVO(FirstVisitForm form) {
        FirstVisitFormVO vo = new FirstVisitFormVO();
        vo.setId(form.getId());
        vo.setStudentId(form.getStudentId());
        vo.setMainProblem(form.getMainProblem());
        vo.setProblemDescription(form.getProblemDescription());
        vo.setExpectedHelp(form.getExpectedHelp());
        vo.setMoodScore(form.getMoodScore());
        vo.setSleepScore(form.getSleepScore());
        vo.setStressScore(form.getStressScore());
        vo.setSelfHarmFlag(form.getSelfHarmFlag());
        vo.setEmergencyFlag(form.getEmergencyFlag());
        vo.setRiskScore(form.getRiskScore());
        vo.setRiskLevel(form.getRiskLevel());
        vo.setFormStatus(form.getFormStatus());
        vo.setSubmitTime(form.getSubmitTime());
        vo.setCreateTime(form.getCreateTime());
        vo.setUpdateTime(form.getUpdateTime());
        return vo;
    }
}