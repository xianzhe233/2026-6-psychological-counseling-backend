package com.tyut.psychological.student.controller;

import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.student.dto.AppointmentCreateRequest;
import com.tyut.psychological.student.service.StudentAppointmentService;
import com.tyut.psychological.student.vo.AvailableSlotVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public Result<List<AvailableSlotVO>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long interviewerId,
            HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        return Result.success(appointmentService.getAvailableSlots(currentUser, date, interviewerId));
    }

    @PostMapping("/appointments")
    public Result<Map<String, Object>> createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request,
            HttpServletRequest httpRequest) {
        CurrentUserVO currentUser = SessionUtils.getRequiredCurrentUser(httpRequest);
        Map<String, Object> result = appointmentService.createAppointment(currentUser, request);
        return Result.success(result);
    }
}
