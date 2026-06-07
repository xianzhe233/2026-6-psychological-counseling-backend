package com.tyut.psychological.student.controller;

import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.student.dto.*;
import com.tyut.psychological.student.service.StudentService;
import com.tyut.psychological.student.vo.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/first-visit/forms/latest")
    public Result<FirstVisitFormVO> getLatestFirstVisitForm() {
        return Result.success(studentService.getLatestFirstVisitForm());
    }

    @PostMapping("/first-visit/forms")
    public Result<FirstVisitFormVO> saveFirstVisitForm(@Valid @RequestBody FirstVisitFormRequest request) {
        return Result.success(studentService.saveFirstVisitForm(request));
    }

    @GetMapping("/consents/status")
    public Result<ConsentStatusVO> getConsentStatus(@RequestParam Long formId) {
        return Result.success(studentService.getConsentStatus(formId));
    }

    @PostMapping("/consents/sign")
    public Result<Void> signConsent(@Valid @RequestBody ConsentSignRequest request) {
        studentService.signConsent(request);
        return Result.success();
    }

    @GetMapping("/appointments/available-slots")
    public Result<List<AvailableSlotVO>> getAvailableSlots(
            @RequestParam String date,
            @RequestParam(required = false) Long interviewerId) {
        return Result.success(studentService.getAvailableSlots(date, interviewerId));
    }

    @PostMapping("/appointments")
    public Result<AppointmentCreateVO> createAppointment(@Valid @RequestBody AppointmentCreateRequest request) {
        return Result.success(studentService.createAppointment(request));
    }

    @GetMapping("/appointments")
    public Result<PageResult<AppointmentVO>> getMyAppointments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(studentService.getMyAppointments(pageNum, pageSize, status));
    }

    @PostMapping("/appointments/{id}/cancel")
    public Result<Void> cancelAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentCancelRequest request) {
        studentService.cancelAppointment(id, request);
        return Result.success();
    }

    @GetMapping("/notifications")
    public Result<PageResult<NotificationVO>> getMyNotifications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(studentService.getMyNotifications(pageNum, pageSize));
    }
}