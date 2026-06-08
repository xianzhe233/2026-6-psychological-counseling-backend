package com.tyut.psychological.student.controller;

import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.student.dto.AppointmentCreateRequest;
import com.tyut.psychological.student.service.StudentAppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentAppointmentController {
    private final StudentAppointmentService appointmentService;
    
    public StudentAppointmentController(StudentAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments/available-slots")
    public Result<List<Map<String, Object>>> getAvailableSlots(
            @RequestParam String date,
            @RequestParam(required = false) Long interviewerId) {
        
        // Mock data for testing
        List<Map<String, Object>> slots = new ArrayList<>();
        
        // Create some mock slots
        Map<String, Object> slot1 = new HashMap<>();
        slot1.put("dutyScheduleId", 5001);
        slot1.put("interviewerId", 2002);
        slot1.put("interviewerName", "初访员张老师");
        slot1.put("appointmentDate", date);
        slot1.put("slotId", 4001);
        slot1.put("slotName", "上午第一时段");
        slot1.put("startTime", "08:30");
        slot1.put("endTime", "10:00");
        slot1.put("roomId", 3001);
        slot1.put("roomName", "咨询室101");
        slot1.put("capacity", 2);
        slot1.put("reservedCount", 1);
        slot1.put("remaining", 1);
        slot1.put("available", true);
        slot1.put("disabledReason", null);
        slots.add(slot1);
        
        Map<String, Object> slot2 = new HashMap<>();
        slot2.put("dutyScheduleId", 5002);
        slot2.put("interviewerId", 2002);
        slot2.put("interviewerName", "初访员张老师");
        slot2.put("appointmentDate", date);
        slot2.put("slotId", 4002);
        slot2.put("slotName", "上午第二时段");
        slot2.put("startTime", "10:10");
        slot2.put("endTime", "11:40");
        slot2.put("roomId", 3002);
        slot2.put("roomName", "咨询室102");
        slot2.put("capacity", 2);
        slot2.put("reservedCount", 0);
        slot2.put("remaining", 2);
        slot2.put("available", true);
        slot2.put("disabledReason", null);
        slots.add(slot2);
        
        Map<String, Object> slot3 = new HashMap<>();
        slot3.put("dutyScheduleId", 5003);
        slot3.put("interviewerId", 2005);
        slot3.put("interviewerName", "赵老师");
        slot3.put("appointmentDate", date);
        slot3.put("slotId", 4003);
        slot3.put("slotName", "下午第一时段");
        slot3.put("startTime", "14:30");
        slot3.put("endTime", "16:00");
        slot3.put("roomId", 3001);
        slot3.put("roomName", "咨询室101");
        slot3.put("capacity", 3);
        slot3.put("reservedCount", 1);
        slot3.put("remaining", 2);
        slot3.put("available", false);
        slot3.put("disabledReason", "该时间段已停用");
        slots.add(slot3);
        
        return Result.success(slots);
    }
    
    @PostMapping("/appointments")
    public Result<Map<String, Object>> createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request,
            HttpServletRequest httpRequest) {
        Long studentId = SessionUtils.getRequiredCurrentUser(httpRequest).getId();
        Map<String, Object> result = appointmentService.createAppointment(studentId, request);
        return Result.success(result);
    }
}