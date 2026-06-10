package com.tyut.psychological.statistics.mapper;

import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.vo.CounselorWorkloadVO;
import com.tyut.psychological.statistics.vo.MonthCountVO;
import com.tyut.psychological.statistics.vo.PieItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    long countFirstVisitAppointments(@Param("query") StatisticsQuery query);

    long countPendingAppointments(@Param("query") StatisticsQuery query);

    long countHighRiskStudents(@Param("query") StatisticsQuery query);

    long countWaitingQueue(@Param("query") StatisticsQuery query);

    long countConsultationSchedules(@Param("query") StatisticsQuery query);

    long countClosedCases(@Param("query") StatisticsQuery query);

    List<MonthCountVO> countFirstVisitByMonth(@Param("query") StatisticsQuery query);

    List<MonthCountVO> countConsultationByMonth(@Param("query") StatisticsQuery query);

    List<PieItemVO> countProblemTypes(@Param("query") StatisticsQuery query);

    List<PieItemVO> countCrisisLevels(@Param("query") StatisticsQuery query);

    List<CounselorWorkloadVO> countCounselorWorkload(@Param("query") StatisticsQuery query);
}
