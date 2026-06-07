package com.tyut.psychological.student.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.student.dto.*;
import com.tyut.psychological.student.vo.*;

import java.util.List;

public interface StudentService {
    FirstVisitFormVO getLatestFirstVisitForm();
    FirstVisitFormVO saveFirstVisitForm(FirstVisitFormRequest request);
    ConsentStatusVO getConsentStatus(Long formId);
    void signConsent(ConsentSignRequest request);
    List<AvailableSlotVO> getAvailableSlots(String date, Long interviewerId);
    AppointmentCreateVO createAppointment(AppointmentCreateRequest request);
    PageResult<AppointmentVO> getMyAppointments(Integer pageNum, Integer pageSize, String status);
    void cancelAppointment(Long id, AppointmentCancelRequest request);
    PageResult<NotificationVO> getMyNotifications(Integer pageNum, Integer pageSize);
}