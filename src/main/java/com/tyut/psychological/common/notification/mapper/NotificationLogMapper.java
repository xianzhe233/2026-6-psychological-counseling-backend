package com.tyut.psychological.common.notification.mapper;

import com.tyut.psychological.common.notification.dto.NotificationLogQuery;
import com.tyut.psychological.common.notification.entity.NotificationLog;
import com.tyut.psychological.common.notification.vo.NotificationLogVO;
import com.tyut.psychological.common.notification.vo.StudentNotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationLogMapper {

    int insert(NotificationLog notificationLog);

    List<StudentNotificationVO> selectByStudentId(@Param("studentId") Long studentId);

    long countByStudentId(@Param("studentId") Long studentId);

    List<NotificationLogVO> pageForAdmin(@Param("q") NotificationLogQuery q);

    long countForAdmin(@Param("q") NotificationLogQuery q);
}