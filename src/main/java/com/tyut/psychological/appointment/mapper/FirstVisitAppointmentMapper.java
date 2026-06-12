package com.tyut.psychological.appointment.mapper;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.vo.AppointmentAuditVO;
import com.tyut.psychological.appointment.vo.StudentAppointmentVO;
import com.tyut.psychological.interviewer.vo.InterviewTaskDetailVO;
import com.tyut.psychological.interviewer.vo.InterviewTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 初访预约Mapper接口
 */
@Mapper
public interface FirstVisitAppointmentMapper {

    /**
     * 根据ID查询预约
     */
    FirstVisitAppointment selectById(@Param("id") Long id);

    /**
     * 插入预约
     */
    int insert(FirstVisitAppointment appointment);

    /**
     * 更新预约
     */
    int update(FirstVisitAppointment appointment);

    /**
     * 分页查询审核列表
     * @param keyword 关键词（学生姓名、学号、预约编号）
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param priorityFlag 优先标记
     * @return 审核列表VO
     */
    List<AppointmentAuditVO> pageAuditList(@Param("keyword") String keyword,
                                          @Param("status") String status,
                                          @Param("riskLevel") String riskLevel,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("priorityFlag") Integer priorityFlag);

    /**
     * 统计审核列表数量
     */
    long countAuditList(@Param("keyword") String keyword,
                       @Param("status") String status,
                       @Param("riskLevel") String riskLevel,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("priorityFlag") Integer priorityFlag);

    /**
     * 根据ID查询预约详情
     * @param id 预约ID
     * @return 预约详情VO
     */
    AppointmentAuditVO selectDetailById(@Param("id") Long id);

    /**
     * 检查学生是否有未完成的预约
     * @param studentId 学生ID
     * @param excludeId 排除的预约ID
     * @return 未完成预约数量
     */
    long checkStudentUnfinishedAppointment(@Param("studentId") Long studentId, @Param("excludeId") Long excludeId);

    /**
     * 分页查询学生预约列表
     * @param studentId 学生ID
     * @param status 预约状态
     * @return 学生预约列表VO
     */
    List<StudentAppointmentVO> pageStudentAppointments(@Param("studentId") Long studentId,
                                                      @Param("status") String status);

    /**
     * 统计学生预约数量
     * @param studentId 学生ID
     * @param status 预约状态
     * @return 预约数量
     */
    long countStudentAppointments(@Param("studentId") Long studentId,
                                 @Param("status") String status);

    /**
     * 查询学生预约详情
     * @param id 预约ID
     * @param studentId 学生ID
     * @return 学生预约详情VO
     */
    StudentAppointmentVO selectStudentAppointmentDetail(@Param("id") Long id,
                                                       @Param("studentId") Long studentId);
    
    /**
     * 分页查询初访任务列表
     * 只能查看分配给当前初访员的任务
     * @param interviewerId 初访员staff_profile ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @return 初访任务列表VO
     */
    List<InterviewTaskVO> pageInterviewTasks(@Param("interviewerId") Long interviewerId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("status") String status,
                                            @Param("riskLevel") String riskLevel);
    
    /**
     * 统计初访任务数量
     * @param interviewerId 初访员staff_profile ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @return 任务数量
     */
    long countInterviewTasks(@Param("interviewerId") Long interviewerId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            @Param("status") String status,
                            @Param("riskLevel") String riskLevel);
    
    /**
     * 查询初访任务详情
     * 包含学生信息、预约信息
     * @param appointmentId 预约ID
     * @param interviewerId 初访员staff_profile ID
     * @return 任务详情VO
     */
    InterviewTaskDetailVO selectInterviewTaskDetail(@Param("appointmentId") Long appointmentId,
                                                   @Param("interviewerId") Long interviewerId);
}