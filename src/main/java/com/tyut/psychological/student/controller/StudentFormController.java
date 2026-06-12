package com.tyut.psychological.student.controller;

import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.student.dto.ConsentSignRequest;
import com.tyut.psychological.student.dto.FirstVisitFormSaveRequest;
import com.tyut.psychological.student.service.StudentFormService;
import com.tyut.psychological.student.vo.ConsentStatusVO;
import com.tyut.psychological.student.vo.FirstVisitFormVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentFormController {
    private final StudentFormService studentFormService;

    public StudentFormController(StudentFormService studentFormService) {
        this.studentFormService = studentFormService;
    }

    @GetMapping("/first-visit/forms/latest")
    public Result<FirstVisitFormVO> getLatestForm(HttpServletRequest request) {
        return Result.success(studentFormService.getLatestForm(SessionUtils.getRequiredCurrentUser(request)));
    }

    @PostMapping("/first-visit/forms")
    public Result<FirstVisitFormVO> saveForm(@Valid @RequestBody FirstVisitFormSaveRequest request,
                                             HttpServletRequest httpRequest) {
        return Result.success(studentFormService.saveForm(SessionUtils.getRequiredCurrentUser(httpRequest), request));
    }

    @GetMapping("/consents/status")
    public Result<ConsentStatusVO> getConsentStatus(@RequestParam Long formId, HttpServletRequest request) {
        return Result.success(studentFormService.getConsentStatus(SessionUtils.getRequiredCurrentUser(request), formId));
    }

    @PostMapping("/consents/sign")
    public Result<Void> signConsent(@Valid @RequestBody ConsentSignRequest request, HttpServletRequest httpRequest) {
        studentFormService.signConsent(
                SessionUtils.getRequiredCurrentUser(httpRequest),
                request,
                httpRequest.getRemoteAddr()
        );
        return Result.success();
    }
}
