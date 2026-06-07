package com.tyut.psychological.schedule.mapper;

import com.tyut.psychological.schedule.entity.TimeSlot;
import com.tyut.psychological.schedule.vo.TimeSlotVO;
import com.tyut.psychological.common.vo.OptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TimeSlotMapper {

    TimeSlot selectById(@Param("id") Long id);

    int insert(TimeSlot timeSlot);

    int update(TimeSlot timeSlot);

    List<TimeSlotVO> pageTimeSlots(@Param("keyword") String keyword, @Param("status") Integer status);

    long countTimeSlots(@Param("keyword") String keyword, @Param("status") Integer status);

    List<OptionVO> selectOptions();
}
