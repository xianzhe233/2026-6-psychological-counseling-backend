package com.tyut.psychological.statistics.mapper;

import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.vo.CounselorWorkloadVO;
import com.tyut.psychological.statistics.vo.MonthCountVO;
import com.tyut.psychological.statistics.vo.OverviewStatsVO;
import com.tyut.psychological.statistics.vo.PieItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    OverviewStatsVO overview(@Param("q") StatisticsQuery q);

    List<MonthCountVO> monthlyConsultations(@Param("q") StatisticsQuery q);

    List<MonthCountVO> monthlyReports(@Param("q") StatisticsQuery q);

    List<MonthCountVO> monthlyNewStudents(@Param("q") StatisticsQuery q);

    List<PieItemVO> consultationDistribution(@Param("q") StatisticsQuery q);

    List<PieItemVO> problemTypeDistribution(@Param("q") StatisticsQuery q);

    List<CounselorWorkloadVO> counselorWorkload(@Param("q") StatisticsQuery q);
}
