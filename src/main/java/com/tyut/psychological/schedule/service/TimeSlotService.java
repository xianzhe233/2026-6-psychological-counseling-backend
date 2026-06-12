package com.tyut.psychological.schedule.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.vo.OptionVO;
import com.tyut.psychological.schedule.dto.TimeSlotSaveRequest;
import com.tyut.psychological.schedule.entity.TimeSlot;
import com.tyut.psychological.schedule.mapper.TimeSlotMapper;
import com.tyut.psychological.schedule.vo.TimeSlotVO;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class TimeSlotService {
    private final TimeSlotMapper timeSlotMapper;

    public TimeSlotService(TimeSlotMapper timeSlotMapper) {
        this.timeSlotMapper = timeSlotMapper;
    }

    // 分页查询时间段
    public PageResult<TimeSlotVO> pageTimeSlots(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        List<TimeSlotVO> records = timeSlotMapper.pageTimeSlots(keyword, status);
        long total = timeSlotMapper.countTimeSlots(keyword, status);
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, records.size());
        List<TimeSlotVO> page = from < records.size() ? records.subList(from, to) : List.of();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(page, total, pageNum, pageSize, pages);
    }

    // 新增时间段，校验结束时间必须晚于开始时间
    public Long createTimeSlot(TimeSlotSaveRequest request) {
        LocalTime start = LocalTime.parse(request.getStartTime());
        LocalTime end = LocalTime.parse(request.getEndTime());
        if (!end.isAfter(start)) {
            throw new BusinessException(400, "结束时间必须晚于开始时间");
        }
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSlotName(request.getSlotName());
        timeSlot.setStartTime(start);
        timeSlot.setEndTime(end);
        timeSlot.setIntervalMinutes(request.getIntervalMinutes() != null ? request.getIntervalMinutes() : 10);
        timeSlot.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        timeSlotMapper.insert(timeSlot);
        return timeSlot.getId();
    }

    // 修改时间段
    public void updateTimeSlot(Long id, TimeSlotSaveRequest request) {
        TimeSlot existing = timeSlotMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "时间段不存在");
        }
        LocalTime start = LocalTime.parse(request.getStartTime());
        LocalTime end = LocalTime.parse(request.getEndTime());
        if (!end.isAfter(start)) {
            throw new BusinessException(400, "结束时间必须晚于开始时间");
        }
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(id);
        timeSlot.setSlotName(request.getSlotName());
        timeSlot.setStartTime(start);
        timeSlot.setEndTime(end);
        timeSlot.setIntervalMinutes(request.getIntervalMinutes());
        timeSlot.setStatus(request.getStatus());
        timeSlotMapper.update(timeSlot);
    }

    // 时间段下拉选项
    public List<OptionVO> getTimeSlotOptions() {
        return timeSlotMapper.selectOptions();
    }
}
