package com.tyut.psychological.common.notification.mapper;

import com.tyut.psychological.common.notification.entity.NotificationLog;
import org.apache.ibatis.annotations.Mapper;

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
}