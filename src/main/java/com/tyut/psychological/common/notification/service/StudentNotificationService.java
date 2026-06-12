package com.tyut.psychological.common.notification.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.notification.mapper.NotificationLogMapper;
import com.tyut.psychological.common.notification.vo.StudentNotificationVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生通知服务类
 */
@Service
public class StudentNotificationService {
    private final NotificationLogMapper notificationLogMapper;

    public StudentNotificationService(NotificationLogMapper notificationLogMapper) {
        this.notificationLogMapper = notificationLogMapper;
    }

    /**
     * 分页查询学生通知
     * @param studentId 学生ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageResult<StudentNotificationVO> pageStudentNotifications(Long studentId, Integer pageNum, Integer pageSize) {
        List<StudentNotificationVO> records = notificationLogMapper.selectByStudentId(studentId);
        long total = notificationLogMapper.countByStudentId(studentId);
        // 内存分页（数据量小）
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, records.size());
        List<StudentNotificationVO> page = from < records.size() ? records.subList(from, to) : List.of();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(page, total, pageNum, pageSize, pages);
    }
}