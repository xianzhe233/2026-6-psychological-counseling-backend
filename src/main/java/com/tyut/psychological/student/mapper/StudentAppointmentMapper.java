package com.tyut.psychological.student.mapper;

import com.tyut.psychological.student.entity.ConsentRecord;
import com.tyut.psychological.student.entity.FirstVisitForm;
import com.tyut.psychological.student.vo.AvailableSlotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StudentAppointmentMapper {
    FirstVisitForm selectOwnedSubmittedForm(@Param("formId") Long formId, @Param("studentId") Long studentId);

    ConsentRecord selectSignedConsent(@Param("formId") Long formId, @Param("studentId") Long studentId);

    List<AvailableSlotVO> selectAvailableSlots(@Param("date") LocalDate date, @Param("interviewerId") Long interviewerId);

    long countAppointmentsByDate(@Param("date") LocalDate date);
}
