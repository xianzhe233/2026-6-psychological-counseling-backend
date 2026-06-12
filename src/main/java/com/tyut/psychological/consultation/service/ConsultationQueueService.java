package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.enums.QueueStatus;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.dto.ConsultationQueueQuery;
import com.tyut.psychological.consultation.entity.ConsultationQueue;
import com.tyut.psychological.consultation.mapper.ConsultationQueueMapper;
import com.tyut.psychological.consultation.mapper.ConsultationScheduleMapper;
import com.tyut.psychological.consultation.vo.ConsultationQueueDetailVO;
import com.tyut.psychological.consultation.vo.ConsultationQueueVO;
import com.tyut.psychological.consultation.vo.ConsultationScheduleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationQueueService {
    private final ConsultationQueueMapper consultationQueueMapper;
    private final ConsultationScheduleMapper consultationScheduleMapper;
    private final OperationLogService operationLogService;

    public ConsultationQueueService(ConsultationQueueMapper consultationQueueMapper,
                                    ConsultationScheduleMapper consultationScheduleMapper,
                                    OperationLogService operationLogService) {
        this.consultationQueueMapper = consultationQueueMapper;
        this.consultationScheduleMapper = consultationScheduleMapper;
        this.operationLogService = operationLogService;
    }

    public PageResult<ConsultationQueueVO> pageQueue(ConsultationQueueQuery query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        List<ConsultationQueueVO> records = consultationQueueMapper.pageQueue(query);
        long total = consultationQueueMapper.countQueue(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    public ConsultationQueueDetailVO getDetail(Long queueId) {
        ConsultationQueueDetailVO detail = consultationQueueMapper.selectDetailById(queueId);
        if (detail == null) {
            throw new BusinessException(404, "咨询队列记录不存在");
        }
        List<ConsultationScheduleVO> schedules = consultationScheduleMapper.selectByQueueId(queueId);
        detail.setSchedules(schedules);
        return detail;
    }

    public ConsultationQueue getRequired(Long queueId) {
        ConsultationQueue queue = consultationQueueMapper.selectById(queueId);
        if (queue == null) {
            throw new BusinessException(404, "咨询队列记录不存在");
        }
        return queue;
    }

    @Transactional
    public void suspend(Long queueId, String reason) {
        ConsultationQueue queue = getRequired(queueId);
        if (!QueueStatus.WAITING.name().equals(queue.getQueueStatus())) {
            throw new BusinessException(400, "只有等待安排状态的队列才能暂缓");
        }
        consultationQueueMapper.updateStatus(queueId, QueueStatus.SUSPENDED.name(), null);
        String desc = "队列ID: " + queueId;
        if (reason != null && !reason.isBlank()) {
            desc += "，原因: " + reason;
        }
        operationLogService.logSuccess("咨询队列", "暂缓队列", desc);
    }

    @Transactional
    public void markArranged(Long queueId) {
        consultationQueueMapper.updateStatus(queueId, QueueStatus.ARRANGED.name(), LocalDateTime.now());
    }

    @Transactional
    public void revertToWaitingIfNeeded(Long queueId) {
        long activeCount = consultationScheduleMapper.countActiveByQueueId(queueId);
        if (activeCount == 0) {
            consultationQueueMapper.updateStatus(queueId, QueueStatus.WAITING.name(), null);
        }
    }
}
