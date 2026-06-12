package com.tyut.psychological.consultation.mapper;

import com.tyut.psychological.consultation.dto.CounselorScheduleQuery;
import com.tyut.psychological.consultation.dto.ScheduleQuery;
import com.tyut.psychological.consultation.entity.ConsultationSchedule;
import com.tyut.psychological.consultation.vo.ConsultationScheduleVO;
import com.tyut.psychological.consultation.vo.CounselorScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ConsultationScheduleMapper {

    ConsultationSchedule selectById(@Param("id") Long id);

    int insert(ConsultationSchedule schedule);

    int updateStatus(@Param("id") Long id, @Param("scheduleStatus") String scheduleStatus);

    long checkCounselorConflict(@Param("counselorId") Long counselorId,
                                @Param("consultationDate") LocalDate consultationDate,
                                @Param("slotId") Long slotId,
                                @Param("excludeId") Long excludeId);

    long checkStudentConflict(@Param("studentId") Long studentId,
                              @Param("consultationDate") LocalDate consultationDate,
                              @Param("slotId") Long slotId,
                              @Param("excludeId") Long excludeId);

    long checkRoomConflict(@Param("roomId") Long roomId,
                           @Param("consultationDate") LocalDate consultationDate,
                           @Param("slotId") Long slotId,
                           @Param("excludeId") Long excludeId);

    long countCounselorDuty(@Param("counselorId") Long counselorId,
                            @Param("consultationDate") LocalDate consultationDate,
                            @Param("slotId") Long slotId);

    Integer selectNextSessionNo(@Param("studentId") Long studentId,
                                @Param("counselorId") Long counselorId);

    long countScheduleNoByPrefix(@Param("prefix") String prefix);

    List<ConsultationScheduleVO> pageForAssistant(@Param("query") ScheduleQuery query);

    long countForAssistant(@Param("query") ScheduleQuery query);

    List<ConsultationScheduleVO> selectByQueueId(@Param("queueId") Long queueId);

    long countActiveByQueueId(@Param("queueId") Long queueId);

    List<CounselorScheduleVO> pageForCounselor(@Param("query") CounselorScheduleQuery query);

    long countForCounselor(@Param("query") CounselorScheduleQuery query);

    CounselorScheduleVO selectDetailForCounselor(@Param("id") Long id, @Param("counselorId") Long counselorId);

    int closeSchedulesByStudentAndCounselor(@Param("studentId") Long studentId,
                                            @Param("counselorId") Long counselorId);
}
