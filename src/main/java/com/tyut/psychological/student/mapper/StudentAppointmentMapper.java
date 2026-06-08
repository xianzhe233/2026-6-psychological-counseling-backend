package com.tyut.psychological.student.mapper;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentAppointmentMapper {
    
    @Insert("INSERT INTO first_visit_appointment (" +
            "appointment_no, form_id, student_id, duty_schedule_id, " +
            "appointment_date, slot_id, slot_name, start_time, end_time, " +
            "interviewer_id, interviewer_name, room_id, room_name, " +
            "appointment_status, create_time, update_time" +
            ") VALUES (" +
            "#{appointmentNo}, #{formId}, #{studentId}, #{dutyScheduleId}, " +
            "#{appointmentDate}, #{slotId}, #{slotName}, #{startTime}, #{endTime}, " +
            "#{interviewerId}, #{interviewerName}, #{roomId}, #{roomName}, " +
            "#{appointmentStatus}, NOW(), NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FirstVisitAppointment appointment);
    
    @Select("SELECT * FROM first_visit_appointment WHERE id = #{id}")
    FirstVisitAppointment selectById(Long id);
    
    @Select("SELECT * FROM first_visit_appointment WHERE student_id = #{studentId} AND appointment_status IN ('PENDING', 'APPROVED') LIMIT 1")
    FirstVisitAppointment selectActiveByStudentId(Long studentId);
}