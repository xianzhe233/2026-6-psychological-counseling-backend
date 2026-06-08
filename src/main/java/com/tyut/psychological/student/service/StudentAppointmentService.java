package com.tyut.psychological.student.service;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.schedule.entity.DutySchedule;
import com.tyut.psychological.schedule.entity.TimeSlot;
import com.tyut.psychological.schedule.mapper.TimeSlotMapper;
import com.tyut.psychological.schedule.service.DutyScheduleService;
import com.tyut.psychological.student.dto.AppointmentCreateRequest;
import com.tyut.psychological.student.entity.ConsentRecord;
import com.tyut.psychological.student.entity.FirstVisitForm;
import com.tyut.psychological.student.mapper.StudentAppointmentMapper;
import com.tyut.psychological.student.vo.AvailableSlotVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentAppointmentService {
    private final StudentAppointmentMapper studentAppointmentMapper;
    private final FirstVisitAppointmentMapper firstVisitAppointmentMapper;
    private final DutyScheduleService dutyScheduleService;
    private final TimeSlotMapper timeSlotMapper;

    public StudentAppointmentService(StudentAppointmentMapper studentAppointmentMapper,
                                     FirstVisitAppointmentMapper firstVisitAppointmentMapper,
                                     DutyScheduleService dutyScheduleService,
                                     TimeSlotMapper timeSlotMapper) {
        this.studentAppointmentMapper = studentAppointmentMapper;
        this.firstVisitAppointmentMapper = firstVisitAppointmentMapper;
        this.dutyScheduleService = dutyScheduleService;
        this.timeSlotMapper = timeSlotMapper;
    }

    public List<AvailableSlotVO> getAvailableSlots(CurrentUserVO user, LocalDate date, Long interviewerId) {
        requireStudent(user);
        if (date == null) {
            throw new BusinessException(400, "预约日期不能为空");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException(400, "不能选择过去的日期");
        }
        return studentAppointmentMapper.selectAvailableSlots(date, interviewerId);
    }

    @Transactional
    public Map<String, Object> createAppointment(CurrentUserVO user, AppointmentCreateRequest request) {
        requireStudent(user);
        Long studentId = user.getId();

        FirstVisitForm form = studentAppointmentMapper.selectOwnedSubmittedForm(request.getFormId(), studentId);
        if (form == null) {
            throw new BusinessException(400, "首访登记表不存在或尚未提交");
        }

        ConsentRecord consentRecord = studentAppointmentMapper.selectSignedConsent(request.getFormId(), studentId);
        if (consentRecord == null) {
            throw new BusinessException(409, "请先签署知情同意书再提交预约");
        }

        long unfinishedCount = firstVisitAppointmentMapper.checkStudentUnfinishedAppointment(studentId, null);
        if (unfinishedCount > 0) {
            throw new BusinessException(409, "您已有未完成的预约，不能重复预约");
        }

        DutySchedule dutySchedule = dutyScheduleService.getDutyScheduleById(request.getDutyScheduleId());
        if (dutySchedule == null) {
            throw new BusinessException(404, "值班安排不存在");
        }
        if (!"INTERVIEWER".equals(dutySchedule.getStaffType())) {
            throw new BusinessException(400, "所选值班安排不是初访排班");
        }
        if (dutySchedule.getStatus() == null || dutySchedule.getStatus() != 1) {
            throw new BusinessException(400, "所选值班安排已停用");
        }
        if (!request.getAppointmentDate().equals(dutySchedule.getDutyDate())) {
            throw new BusinessException(400, "预约日期与值班日期不一致");
        }
        if (!request.getSlotId().equals(dutySchedule.getSlotId())) {
            throw new BusinessException(400, "所选时间段与值班安排不一致");
        }
        if (request.getInterviewerId() != null && !request.getInterviewerId().equals(dutySchedule.getStaffId())) {
            throw new BusinessException(400, "所选初访员与值班安排不一致");
        }
        if (request.getRoomId() != null && dutySchedule.getRoomId() != null && !request.getRoomId().equals(dutySchedule.getRoomId())) {
            throw new BusinessException(400, "所选咨询室与值班安排不一致");
        }
        if (dutySchedule.getCapacity() == null || dutySchedule.getReservedCount() == null
                || dutySchedule.getReservedCount() >= dutySchedule.getCapacity()) {
            throw new BusinessException(409, "该时间段已约满，请选择其他时间");
        }

        TimeSlot timeSlot = timeSlotMapper.selectById(dutySchedule.getSlotId());
        if (timeSlot == null || timeSlot.getStatus() == null || timeSlot.getStatus() != 1) {
            throw new BusinessException(400, "所选时间段不可用");
        }

        FirstVisitAppointment appointment = new FirstVisitAppointment();
        appointment.setAppointmentNo(generateAppointmentNo(request.getAppointmentDate()));
        appointment.setFormId(form.getId());
        appointment.setStudentId(studentId);
        appointment.setInterviewerId(dutySchedule.getStaffId());
        appointment.setDutyScheduleId(dutySchedule.getId());
        appointment.setAppointmentDate(dutySchedule.getDutyDate());
        appointment.setSlotId(dutySchedule.getSlotId());
        appointment.setRoomId(dutySchedule.getRoomId());
        appointment.setAppointmentStatus("PENDING");
        appointment.setPriorityFlag(0);

        firstVisitAppointmentMapper.insert(appointment);
        dutyScheduleService.incrementReservedCount(dutySchedule.getId(), 1);

        Map<String, Object> result = new HashMap<>();
        result.put("id", appointment.getId());
        result.put("appointmentNo", appointment.getAppointmentNo());
        result.put("appointmentStatus", appointment.getAppointmentStatus());
        return result;
    }

    private void requireStudent(CurrentUserVO user) {
        if (user == null || user.getRoles() == null || !user.getRoles().contains(RoleCode.STUDENT)) {
            throw new BusinessException(403, "当前角色无权访问");
        }
    }

    private String generateAppointmentNo(LocalDate appointmentDate) {
        long sequence = studentAppointmentMapper.countAppointmentsByDate(appointmentDate) + 1;
        String dateStr = appointmentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("FV%s%04d", dateStr, sequence);
    }
}
