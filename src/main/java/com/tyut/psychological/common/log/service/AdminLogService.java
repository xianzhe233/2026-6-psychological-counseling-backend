package com.tyut.psychological.common.log.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.log.dto.OperationLogQuery;
import com.tyut.psychological.common.log.mapper.OperationLogMapper;
import com.tyut.psychological.common.log.vo.OperationLogVO;
import com.tyut.psychological.common.notification.dto.NotificationLogQuery;
import com.tyut.psychological.common.notification.mapper.NotificationLogMapper;
import com.tyut.psychological.common.notification.vo.NotificationLogVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLogService {
    private final NotificationLogMapper notificationLogMapper;
    private final OperationLogMapper operationLogMapper;

    public AdminLogService(NotificationLogMapper notificationLogMapper,
                           OperationLogMapper operationLogMapper) {
        this.notificationLogMapper = notificationLogMapper;
        this.operationLogMapper = operationLogMapper;
    }

    public PageResult<NotificationLogVO> pageNotificationLogs(NotificationLogQuery query) {
        normalizeNotificationQuery(query);
        List<NotificationLogVO> records = notificationLogMapper.pageForAdmin(query);
        long total = notificationLogMapper.countForAdmin(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    public PageResult<OperationLogVO> pageOperationLogs(OperationLogQuery query) {
        normalizeOperationQuery(query);
        List<OperationLogVO> records = operationLogMapper.pageForAdmin(query);
        long total = operationLogMapper.countForAdmin(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    private void normalizeNotificationQuery(NotificationLogQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }

    private void normalizeOperationQuery(OperationLogQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
    }
}
