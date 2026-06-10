package com.tyut.psychological.schedule.service;

import com.tyut.psychological.appointment.vo.AvailableSlotVO;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.schedule.dto.BatchScheduleRequest;
import com.tyut.psychological.schedule.dto.BatchScheduleResponse;
import com.tyut.psychological.schedule.dto.DutyScheduleSaveRequest;
import com.tyut.psychological.schedule.entity.DutySchedule;
import com.tyut.psychological.schedule.mapper.DutyScheduleMapper;
import com.tyut.psychological.schedule.vo.DutyScheduleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 值班安排服务类
 * 实现值班安排的增删改查、冲突检测、容量校验等功能
 */
@Service
public class DutyScheduleService {
    private final DutyScheduleMapper dutyScheduleMapper;
    private final StaffProfileMapper staffProfileMapper;
    private final OperationLogService operationLogService;

    public DutyScheduleService(DutyScheduleMapper dutyScheduleMapper, 
                              StaffProfileMapper staffProfileMapper,
                              OperationLogService operationLogService) {
        this.dutyScheduleMapper = dutyScheduleMapper;
        this.staffProfileMapper = staffProfileMapper;
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询值班安排
     * @param staffType 工作人员类型
     * @param staffId 工作人员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageResult<DutyScheduleVO> pageDutySchedules(String staffType, Long staffId, 
                                                       LocalDate startDate, LocalDate endDate, 
                                                       Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        List<DutyScheduleVO> records = dutyScheduleMapper.pageDutySchedules(staffType, staffId, startDate, endDate, status);
        long total = dutyScheduleMapper.countDutySchedules(staffType, staffId, startDate, endDate, status);
        // 内存分页（数据量小）
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, records.size());
        List<DutyScheduleVO> page = from < records.size() ? records.subList(from, to) : List.of();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(page, total, pageNum, pageSize, pages);
    }

    /**
     * 新增值班安排
     * @param request 值班安排请求
     * @return 值班安排ID
     */
    @Transactional
    public Long createDutySchedule(DutyScheduleSaveRequest request) {
        // 校验工作人员是否存在且启用
        StaffProfile staffProfile = staffProfileMapper.selectById(request.getStaffId());
        if (staffProfile == null) {
            throw new BusinessException(404, "工作人员不存在");
        }
        if (staffProfile.getStatus() != 1) {
            throw new BusinessException(400, "只有启用工作人员才能排班");
        }
        
        // 检查冲突
        checkConflict(request.getStaffId(), request.getDutyDate(), request.getSlotId(), null);
        
        DutySchedule dutySchedule = new DutySchedule();
        dutySchedule.setStaffId(request.getStaffId());
        dutySchedule.setStaffType(request.getStaffType());
        dutySchedule.setDutyDate(request.getDutyDate());
        dutySchedule.setSlotId(request.getSlotId());
        dutySchedule.setRoomId(request.getRoomId());
        dutySchedule.setCapacity(request.getCapacity());
        dutySchedule.setReservedCount(0);
        dutySchedule.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        
        dutyScheduleMapper.insert(dutySchedule);
        
        // 记录操作日志
        operationLogService.logSuccess("值班管理", "新增值班", "值班ID: " + dutySchedule.getId());
        
        return dutySchedule.getId();
    }

    /**
     * 修改值班安排
     * @param id 值班安排ID
     * @param request 值班安排请求
     */
    @Transactional
    public void updateDutySchedule(Long id, DutyScheduleSaveRequest request) {
        DutySchedule existing = dutyScheduleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "值班安排不存在");
        }
        
        // 校验工作人员是否存在且启用
        StaffProfile staffProfile = staffProfileMapper.selectById(request.getStaffId());
        if (staffProfile == null) {
            throw new BusinessException(404, "工作人员不存在");
        }
        if (staffProfile.getStatus() != 1) {
            throw new BusinessException(400, "只有启用工作人员才能排班");
        }
        
        // 检查冲突（排除自身）
        checkConflict(request.getStaffId(), request.getDutyDate(), request.getSlotId(), id);
        
        // 检查容量是否足够
        if (request.getCapacity() < existing.getReservedCount()) {
            throw new BusinessException(400, "容量不能小于已预约数");
        }
        
        DutySchedule dutySchedule = new DutySchedule();
        dutySchedule.setId(id);
        dutySchedule.setStaffId(request.getStaffId());
        dutySchedule.setStaffType(request.getStaffType());
        dutySchedule.setDutyDate(request.getDutyDate());
        dutySchedule.setSlotId(request.getSlotId());
        dutySchedule.setRoomId(request.getRoomId());
        dutySchedule.setCapacity(request.getCapacity());
        dutySchedule.setStatus(request.getStatus());
        
        dutyScheduleMapper.update(dutySchedule);
        
        // 记录操作日志
        operationLogService.logSuccess("值班管理", "修改值班", "值班ID: " + id);
    }

    /**
     * 检查值班冲突
     * @param staffId 工作人员ID
     * @param dutyDate 值班日期
     * @param slotId 时间段ID
     * @param excludeId 排除的ID（用于修改时排除自身）
     */
    private void checkConflict(Long staffId, LocalDate dutyDate, Long slotId, Long excludeId) {
        long conflictCount = dutyScheduleMapper.checkConflict(staffId, dutyDate, slotId, excludeId);
        if (conflictCount > 0) {
            throw new BusinessException(409, "该工作人员在该时间段已有值班安排");
        }
    }

    /**
     * 根据ID查询值班安排
     * @param id 值班安排ID
     * @return 值班安排
     */
    public DutySchedule getDutyScheduleById(Long id) {
        return dutyScheduleMapper.selectById(id);
    }

    /**
     * 增加已预约数量
     * @param id 值班安排ID
     * @param count 增加数量
     */
    @Transactional
    public void incrementReservedCount(Long id, int count) {
        DutySchedule dutySchedule = dutyScheduleMapper.selectById(id);
        if (dutySchedule == null) {
            throw new BusinessException(404, "值班安排不存在");
        }
        
        // 检查容量是否足够
        if (dutySchedule.getReservedCount() + count > dutySchedule.getCapacity()) {
            throw new BusinessException(400, "值班容量已满");
        }
        
        dutyScheduleMapper.incrementReservedCount(id, count);
    }

    /**
     * 减少已预约数量
     * @param id 值班安排ID
     * @param count 减少数量
     */
    @Transactional
    public void decrementReservedCount(Long id, int count) {
        DutySchedule dutySchedule = dutyScheduleMapper.selectById(id);
        if (dutySchedule == null) {
            throw new BusinessException(404, "值班安排不存在");
        }
        
        // 如果已经是0，直接返回
        if (dutySchedule.getReservedCount() <= 0) {
            return;
        }
        
        dutyScheduleMapper.decrementReservedCount(id, count);
    }

    /**
     * 批量排班
     * @param request 批量排班请求
     * @return 批量排班响应
     */
    @Transactional
    public BatchScheduleResponse batchCreateDutySchedules(BatchScheduleRequest request) {
        // 校验工作人员是否存在且启用
        StaffProfile staffProfile = staffProfileMapper.selectById(request.getStaffId());
        if (staffProfile == null) {
            throw new BusinessException(404, "工作人员不存在");
        }
        if (staffProfile.getStatus() != 1) {
            throw new BusinessException(400, "只有启用工作人员才能排班");
        }
        
        BatchScheduleResponse response = new BatchScheduleResponse();
        List<BatchScheduleResponse.ConflictInfo> conflicts = new ArrayList<>();
        int createdCount = 0;
        int skippedCount = 0;
        
        // 遍历日期范围
        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            // 检查是否是指定的星期几
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            int dayValue = dayOfWeek.getValue(); // 1=周一, 7=周日
            
            if (request.getWeekdays().contains(dayValue)) {
                // 遍历时间段
                for (Long slotId : request.getSlotIds()) {
                    // 检查冲突
                    long conflictCount = dutyScheduleMapper.checkConflict(
                        request.getStaffId(), currentDate, slotId, null);
                    
                    if (conflictCount > 0) {
                        // 存在冲突，记录并跳过
                        conflicts.add(new BatchScheduleResponse.ConflictInfo(
                            currentDate.toString(), slotId, "该时间段已有值班安排"));
                        skippedCount++;
                    } else {
                        // 不存在冲突，创建值班安排
                        DutySchedule dutySchedule = new DutySchedule();
                        dutySchedule.setStaffId(request.getStaffId());
                        dutySchedule.setStaffType(request.getStaffType());
                        dutySchedule.setDutyDate(currentDate);
                        dutySchedule.setSlotId(slotId);
                        dutySchedule.setRoomId(request.getRoomId());
                        dutySchedule.setCapacity(request.getCapacity());
                        dutySchedule.setReservedCount(0);
                        dutySchedule.setStatus(1);
                        
                        dutyScheduleMapper.insert(dutySchedule);
                        createdCount++;
                    }
                }
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        response.setCreatedCount(createdCount);
        response.setSkippedCount(skippedCount);
        response.setConflicts(conflicts);
        
        // 记录操作日志
        operationLogService.logSuccess("值班管理", "批量排班", 
            "创建数量: " + createdCount + ", 跳过数量: " + skippedCount);
        
        return response;
    }

    /**
     * 查询可预约时间段
     * @param date 日期
     * @param interviewerId 初访员ID（可选）
     * @return 可预约时间段列表
     */
    public List<AvailableSlotVO> getAvailableSlots(LocalDate date, Long interviewerId) {
        List<DutyScheduleVO> dutySchedules = dutyScheduleMapper.pageDutySchedules(
            "INTERVIEWER", interviewerId, date, date, 1); // 状态为启用
        
        List<AvailableSlotVO> availableSlots = new ArrayList<>();
        
        for (DutyScheduleVO dutySchedule : dutySchedules) {
            AvailableSlotVO slot = new AvailableSlotVO();
            slot.setDutyScheduleId(dutySchedule.getId());
            slot.setInterviewerId(dutySchedule.getStaffId());
            slot.setInterviewerName(dutySchedule.getStaffName());
            slot.setAppointmentDate(dutySchedule.getDutyDate());
            slot.setSlotId(dutySchedule.getSlotId());
            slot.setSlotName(dutySchedule.getSlotName());
            slot.setStartTime(dutySchedule.getStartTime());
            slot.setEndTime(dutySchedule.getEndTime());
            slot.setRoomId(dutySchedule.getRoomId());
            slot.setRoomName(dutySchedule.getRoomName());
            slot.setCapacity(dutySchedule.getCapacity());
            slot.setReservedCount(dutySchedule.getReservedCount());
            slot.setRemaining(dutySchedule.getCapacity() - dutySchedule.getReservedCount());
            
            // 判断是否可用
            boolean available = dutySchedule.getReservedCount() < dutySchedule.getCapacity();
            slot.setAvailable(available);
            slot.setDisabledReason(available ? null : "该时间段已约满");
            
            availableSlots.add(slot);
        }
        
        return availableSlots;
    }
}