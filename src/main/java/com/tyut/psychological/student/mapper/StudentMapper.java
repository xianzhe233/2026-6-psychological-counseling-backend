package com.tyut.psychological.student.mapper;

import com.tyut.psychological.student.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    // 首访登记表
    FirstVisitForm selectLatestFirstVisitForm(@Param("studentId") Long studentId);
    int insertFirstVisitForm(FirstVisitForm form);
    int updateFirstVisitForm(FirstVisitForm form);
    
    // 知情同意书
    ConsentRecord selectConsentRecordByFormId(@Param("formId") Long formId);
    int insertConsentRecord(ConsentRecord record);
    
    // 值班安排
    List<DutySchedule> selectAvailableDutySchedules(@Param("date") String date, @Param("interviewerId") Long interviewerId);
    DutySchedule selectDutyScheduleById(@Param("id") Long id);
    int updateDutyScheduleReservedCount(@Param("id") Long id, @Param("increment") int increment);
    
    // 预约
    int insertFirstVisitAppointment(FirstVisitAppointment appointment);
    FirstVisitAppointment selectAppointmentById(@Param("id") Long id);
    List<FirstVisitAppointment> selectAppointmentsByStudentId(@Param("studentId") Long studentId, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    int countAppointmentsByStudentId(@Param("studentId") Long studentId, @Param("status") String status);
    int updateAppointmentStatus(@Param("id") Long id, @Param("status") String status, @Param("reason") String reason);
    
    // 通知
    List<NotificationLog> selectNotificationsByStudentId(@Param("studentId") Long studentId, @Param("offset") int offset, @Param("limit") int limit);
    int countNotificationsByStudentId(@Param("studentId") Long studentId);
}