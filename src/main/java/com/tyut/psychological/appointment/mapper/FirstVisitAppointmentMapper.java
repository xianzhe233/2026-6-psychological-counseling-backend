package com.tyut.psychological.appointment.mapper;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.vo.AppointmentAuditVO;
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
}