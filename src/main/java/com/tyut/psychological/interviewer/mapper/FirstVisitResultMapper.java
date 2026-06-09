package com.tyut.psychological.interviewer.mapper;

import com.tyut.psychological.interviewer.entity.FirstVisitResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 初访结果Mapper接口
 */
@Mapper
public interface FirstVisitResultMapper {

    /**
     * 根据预约ID查询初访结果
     * @param appointmentId 预约ID
     * @return 初访结果
     */
    FirstVisitResult selectByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 插入初访结果
     * @param firstVisitResult 初访结果
     * @return 影响行数
     */
    int insert(FirstVisitResult firstVisitResult);

    /**
     * 更新初访结果
     * @param firstVisitResult 初访结果
     * @return 影响行数
     */
    int update(FirstVisitResult firstVisitResult);
}