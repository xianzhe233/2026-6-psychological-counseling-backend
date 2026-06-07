package com.tyut.psychological.student.service;

import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.student.dto.*;
import com.tyut.psychological.student.entity.*;
import com.tyut.psychological.student.mapper.StudentMapper;
import com.tyut.psychological.student.vo.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;
    private final HttpServletRequest request;

    public StudentServiceImpl(StudentMapper studentMapper, HttpServletRequest request) {
        this.studentMapper = studentMapper;
        this.request = request;
    }

    private Long getCurrentStudentId() {
        // 临时返回固定值，用于测试
        return 1L;
        // CurrentUserVO user = SessionUtils.getRequiredCurrentUser(request);
        // return user.getId();
    }

    @Override
    public FirstVisitFormVO getLatestFirstVisitForm() {
        // 临时返回模拟数据，用于测试
        FirstVisitFormVO vo = new FirstVisitFormVO();
        vo.setId(1L);
        vo.setStudentId(1L);
        vo.setMainProblem("学习压力");
        vo.setProblemDescription("最近考试压力较大，睡眠不好");
        vo.setExpectedHelp("希望获得压力调节建议");
        vo.setMoodScore(6);
        vo.setSleepScore(5);
        vo.setStressScore(8);
        vo.setSelfHarmFlag(0);
        vo.setEmergencyFlag(0);
        vo.setRiskScore(30);
        vo.setRiskLevel("MEDIUM");
        vo.setFormStatus("SUBMITTED");
        vo.setSubmitTime(LocalDateTime.now());
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateTime(LocalDateTime.now());
        return vo;
    }

    @Override
    @Transactional
    public FirstVisitFormVO saveFirstVisitForm(FirstVisitFormRequest request) {
        // 临时返回模拟数据，用于测试
        FirstVisitFormVO vo = new FirstVisitFormVO();
        vo.setId(1L);
        vo.setStudentId(1L);
        vo.setMainProblem(request.getMainProblem());
        vo.setProblemDescription(request.getProblemDescription());
        vo.setExpectedHelp(request.getExpectedHelp());
        vo.setMoodScore(request.getMoodScore());
        vo.setSleepScore(request.getSleepScore());
        vo.setStressScore(request.getStressScore());
        vo.setSelfHarmFlag(request.getSelfHarmFlag());
        vo.setEmergencyFlag(request.getEmergencyFlag());
        vo.setRiskScore(calculateRiskScore(request));
        vo.setRiskLevel(calculateRiskLevel(calculateRiskScore(request)));
        vo.setFormStatus("SUBMITTED");
        vo.setSubmitTime(LocalDateTime.now());
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateTime(LocalDateTime.now());
        return vo;
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
        // 临时返回模拟数据，用于测试
        ConsentStatusVO status = new ConsentStatusVO();
        status.setFormId(formId);
        status.setSigned(false);
        return status;
    }

    @Override
    @Transactional
    public void signConsent(ConsentSignRequest request) {
        // 临时模拟签署成功
        System.out.println("签署同意书成功，表单ID：" + request.getFormId());
    }

    @Override
    public List<AvailableSlotVO> getAvailableSlots(String date, Long interviewerId) {
        // 临时返回模拟数据，用于测试
        List<AvailableSlotVO> slots = new ArrayList<>();
        
        AvailableSlotVO slot1 = new AvailableSlotVO();
        slot1.setDutyScheduleId(1L);
        slot1.setInterviewerId(1L);
        slot1.setInterviewerName("王老师");
        slot1.setAppointmentDate(date);
        slot1.setSlotId(1L);
        slot1.setSlotName("上午第一段");
        slot1.setStartTime("08:30:00");
        slot1.setEndTime("09:20:00");
        slot1.setRoomId(1L);
        slot1.setRoomName("心理咨询室A");
        slot1.setCapacity(2);
        slot1.setReservedCount(0);
        slot1.setRemaining(2);
        slot1.setAvailable(true);
        slots.add(slot1);
        
        AvailableSlotVO slot2 = new AvailableSlotVO();
        slot2.setDutyScheduleId(2L);
        slot2.setInterviewerId(1L);
        slot2.setInterviewerName("王老师");
        slot2.setAppointmentDate(date);
        slot2.setSlotId(2L);
        slot2.setSlotName("上午第二段");
        slot2.setStartTime("09:30:00");
        slot2.setEndTime("10:20:00");
        slot2.setRoomId(1L);
        slot2.setRoomName("心理咨询室A");
        slot2.setCapacity(2);
        slot2.setReservedCount(1);
        slot2.setRemaining(1);
        slot2.setAvailable(true);
        slots.add(slot2);
        
        AvailableSlotVO slot3 = new AvailableSlotVO();
        slot3.setDutyScheduleId(3L);
        slot3.setInterviewerId(1L);
        slot3.setInterviewerName("王老师");
        slot3.setAppointmentDate(date);
        slot3.setSlotId(3L);
        slot3.setSlotName("上午第三段");
        slot3.setStartTime("10:30:00");
        slot3.setEndTime("11:20:00");
        slot3.setRoomId(1L);
        slot3.setRoomName("心理咨询室A");
        slot3.setCapacity(2);
        slot3.setReservedCount(2);
        slot3.setRemaining(0);
        slot3.setAvailable(false);
        slot3.setDisabledReason("该时间段已满");
        slots.add(slot3);
        
        return slots;
    }

    @Override
    @Transactional
    public AppointmentCreateVO createAppointment(AppointmentCreateRequest request) {
        // 临时返回模拟数据，用于测试
        AppointmentCreateVO result = new AppointmentCreateVO();
        result.setId(1L);
        result.setAppointmentNo("FV" + System.currentTimeMillis());
        result.setAppointmentStatus("PENDING");
        return result;
    }

    private String generateAppointmentNo() {
        return "FV" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4);
    }

    @Override
    public PageResult<AppointmentVO> getMyAppointments(Integer pageNum, Integer pageSize, String status) {
        // 临时返回模拟数据，用于测试
        List<AppointmentVO> records = new ArrayList<>();
        
        AppointmentVO appointment1 = new AppointmentVO();
        appointment1.setId(1L);
        appointment1.setAppointmentNo("FV202606100001");
        appointment1.setAppointmentDate("2026-06-10");
        appointment1.setSlotName("上午第一段");
        appointment1.setStartTime("08:30:00");
        appointment1.setEndTime("09:20:00");
        appointment1.setInterviewerName("王老师");
        appointment1.setRoomName("心理咨询室A");
        appointment1.setAppointmentStatus("PENDING");
        appointment1.setCreateTime(LocalDateTime.now());
        records.add(appointment1);
        
        AppointmentVO appointment2 = new AppointmentVO();
        appointment2.setId(2L);
        appointment2.setAppointmentNo("FV202606100002");
        appointment2.setAppointmentDate("2026-06-10");
        appointment2.setSlotName("上午第二段");
        appointment2.setStartTime("09:30:00");
        appointment2.setEndTime("10:20:00");
        appointment2.setInterviewerName("王老师");
        appointment2.setRoomName("心理咨询室A");
        appointment2.setAppointmentStatus("APPROVED");
        appointment2.setAuditRemark("请按时到达");
        appointment2.setCreateTime(LocalDateTime.now());
        records.add(appointment2);
        
        PageResult<AppointmentVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(2);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages(1);
        
        return result;
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, AppointmentCancelRequest request) {
        // 临时模拟撤销成功
        System.out.println("撤销预约成功，预约ID：" + id + "，原因：" + request.getReason());
    }

    @Override
    public PageResult<NotificationVO> getMyNotifications(Integer pageNum, Integer pageSize) {
        // 临时返回模拟数据，用于测试
        List<NotificationVO> records = new ArrayList<>();
        
        NotificationVO notification1 = new NotificationVO();
        notification1.setId(1L);
        notification1.setTitle("预约审核通过");
        notification1.setContent("您的初访预约已通过审核，请按时到达。");
        notification1.setNotifyType("APPOINTMENT");
        notification1.setSendTime(LocalDateTime.now());
        records.add(notification1);
        
        NotificationVO notification2 = new NotificationVO();
        notification2.setId(2L);
        notification2.setTitle("系统通知");
        notification2.setContent("欢迎使用心理咨询系统。");
        notification2.setNotifyType("SYSTEM");
        notification2.setSendTime(LocalDateTime.now());
        records.add(notification2);
        
        PageResult<NotificationVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(2);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages(1);
        
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