package com.tyut.psychological.common.notification.mapper;

import com.tyut.psychological.common.notification.dto.NotificationLogQuery;
import com.tyut.psychological.common.notification.entity.NotificationLog;
import com.tyut.psychological.common.notification.vo.NotificationLogVO;
import com.tyut.psychological.common.notification.vo.StudentNotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知日志Mapper接口
 */
@Mapper
public interface NotificationLogMapper {

    /**
     * 插入通知日志
     * @param notificationLog 通知日志
     * @return 影响行数
     */
    int insert(NotificationLog notificationLog);

    /**
     * 查询学生通知列表
     * @param studentId 学生ID
     * @return 学生通知列表
     */
    List<StudentNotificationVO> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计学生通知数量
     * @param studentId 学生ID
     * @return 通知数量
     */
    long countByStudentId(@Param("studentId") Long studentId);

    List<NotificationLogVO> pageForAdmin(@Param("query") NotificationLogQuery query);

    long countForAdmin(@Param("query") NotificationLogQuery query);
}