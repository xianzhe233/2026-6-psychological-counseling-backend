package com.tyut.psychological.consultation.mapper;

import com.tyut.psychological.consultation.entity.ConsultationRecord;
import com.tyut.psychological.consultation.vo.ConsultationRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConsultationRecordMapper {

    ConsultationRecordVO selectByScheduleId(@Param("scheduleId") Long scheduleId);

    ConsultationRecord selectEntityByScheduleId(@Param("scheduleId") Long scheduleId);

    int insert(ConsultationRecord record);

    int update(ConsultationRecord record);
}
