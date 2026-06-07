package com.tyut.psychological.schedule.mapper;

import com.tyut.psychological.schedule.entity.DutySchedule;
import com.tyut.psychological.schedule.vo.DutyScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 值班安排Mapper接口
 */
@Mapper
public interface DutyScheduleMapper {

    /**
     * 根据ID查询值班安排
     */
    DutySchedule selectById(@Param("id") Long id);

    /**
     * 插入值班安排
     */
    int insert(DutySchedule dutySchedule);

    /**
     * 更新值班安排
     */
    int update(DutySchedule dutySchedule);

    /**
     * 分页查询值班安排
     * @param staffType 工作人员类型
     * @param staffId 工作人员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 状态
     * @return 值班安排VO列表
     */
    List<DutyScheduleVO> pageDutySchedules(@Param("staffType") String staffType,
                                          @Param("staffId") Long staffId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("status") Integer status);

    /**
     * 统计值班安排数量
     */
    long countDutySchedules(@Param("staffType") String staffType,
                           @Param("staffId") Long staffId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate,
                           @Param("status") Integer status);

    /**
     * 检查是否存在冲突的值班安排
     * @param staffId 工作人员ID
     * @param dutyDate 值班日期
     * @param slotId 时间段ID
     * @param excludeId 排除的ID（用于修改时排除自身）
     * @return 冲突数量
     */
    long checkConflict(@Param("staffId") Long staffId,
                      @Param("dutyDate") LocalDate dutyDate,
                      @Param("slotId") Long slotId,
                      @Param("excludeId") Long excludeId);

    /**
     * 检查容量是否足够
     * @param id 值班安排ID
     * @param newCapacity 新容量
     * @return 是否足够
     */
    boolean checkCapacity(@Param("id") Long id, @Param("newCapacity") Integer newCapacity);

    /**
     * 增加已预约数量
     * @param id 值班安排ID
     * @param count 增加数量
     * @return 影响行数
     */
    int incrementReservedCount(@Param("id") Long id, @Param("count") int count);

    /**
     * 减少已预约数量
     * @param id 值班安排ID
     * @param count 减少数量
     * @return 影响行数
     */
    int decrementReservedCount(@Param("id") Long id, @Param("count") int count);
}