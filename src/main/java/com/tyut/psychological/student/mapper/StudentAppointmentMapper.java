package com.tyut.psychological.student.mapper;

import com.tyut.psychological.student.entity.ConsentRecord;
import com.tyut.psychological.student.entity.FirstVisitForm;
import com.tyut.psychological.student.vo.AvailableSlotVO;
import com.tyut.psychological.student.vo.MyAppointmentVO;
import com.tyut.psychological.student.vo.MyNotificationVO;
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

    /**
     * 查询学生预约列表
     * @param studentId 学生ID
     * @param status 预约状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 预约列表
     */
    List<MyAppointmentVO> selectStudentAppointments(@Param("studentId") Long studentId,
                                                    @Param("status") String status,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * 统计学生预约数量
     * @param studentId 学生ID
     * @param status 预约状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 预约数量
     */
    long countStudentAppointments(@Param("studentId") Long studentId,
                                  @Param("status") String status,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    /**
     * 查询学生通知列表
     * @param studentId 学生ID
     * @param notifyType 通知类型（可选）
     * @return 通知列表
     */
    List<MyNotificationVO> selectStudentNotifications(@Param("studentId") Long studentId, @Param("notifyType") String notifyType);

    /**
     * 统计学生通知数量
     * @param studentId 学生ID
     * @param notifyType 通知类型（可选）
     * @return 通知数量
     */
    long countStudentNotifications(@Param("studentId") Long studentId, @Param("notifyType") String notifyType);
}
