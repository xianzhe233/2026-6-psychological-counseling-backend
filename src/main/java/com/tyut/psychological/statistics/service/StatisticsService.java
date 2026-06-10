package com.tyut.psychological.statistics.service;

import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.mapper.StatisticsMapper;
import com.tyut.psychological.statistics.vo.BarChartVO;
import com.tyut.psychological.statistics.vo.ChartSeriesVO;
import com.tyut.psychological.statistics.vo.CounselorWorkloadVO;
import com.tyut.psychological.statistics.vo.LineChartVO;
import com.tyut.psychological.statistics.vo.MonthCountVO;
import com.tyut.psychological.statistics.vo.OverviewStatsVO;
import com.tyut.psychological.statistics.vo.PieItemVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final Map<String, String> CRISIS_LABELS = Map.of(
            "LOW", "低风险",
            "MEDIUM", "中风险",
            "HIGH", "高风险",
            "URGENT", "紧急风险"
    );

    private final StatisticsMapper statisticsMapper;

    public StatisticsService(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    public OverviewStatsVO getOverview(StatisticsQuery query) {
        OverviewStatsVO overview = new OverviewStatsVO();
        overview.setFirstVisitAppointmentCount(statisticsMapper.countFirstVisitAppointments(query));
        overview.setPendingAppointmentCount(statisticsMapper.countPendingAppointments(query));
        overview.setHighRiskStudentCount(statisticsMapper.countHighRiskStudents(query));
        overview.setWaitingQueueCount(statisticsMapper.countWaitingQueue(query));
        overview.setConsultationScheduleCount(statisticsMapper.countConsultationSchedules(query));
        overview.setClosedCaseCount(statisticsMapper.countClosedCases(query));
        return overview;
    }

    public LineChartVO getMonthlyTrend(StatisticsQuery query) {
        List<String> months = buildMonthAxis(query);
        Map<String, Long> firstVisitMap = toMonthMap(statisticsMapper.countFirstVisitByMonth(query));
        Map<String, Long> consultationMap = toMonthMap(statisticsMapper.countConsultationByMonth(query));

        LineChartVO chart = new LineChartVO();
        chart.setXAxis(months);
        chart.getSeries().add(new ChartSeriesVO("初访预约数", mapCounts(months, firstVisitMap)));
        chart.getSeries().add(new ChartSeriesVO("正式咨询数", mapCounts(months, consultationMap)));
        return chart;
    }

    public List<PieItemVO> getProblemTypeDistribution(StatisticsQuery query) {
        return statisticsMapper.countProblemTypes(query);
    }

    public BarChartVO getCrisisLevelDistribution(StatisticsQuery query) {
        List<PieItemVO> rawItems = statisticsMapper.countCrisisLevels(query);
        List<String> xAxis = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        for (PieItemVO item : rawItems) {
            xAxis.add(CRISIS_LABELS.getOrDefault(item.getName(), item.getName()));
            values.add(item.getValue());
        }
        BarChartVO chart = new BarChartVO();
        chart.setXAxis(xAxis);
        chart.getSeries().add(new ChartSeriesVO("学生人数", values));
        return chart;
    }

    public BarChartVO getCounselorWorkload(StatisticsQuery query) {
        List<CounselorWorkloadVO> rows = statisticsMapper.countCounselorWorkload(query);
        List<String> xAxis = new ArrayList<>();
        List<Long> sessionCounts = new ArrayList<>();
        List<Long> totalMinutes = new ArrayList<>();
        for (CounselorWorkloadVO row : rows) {
            xAxis.add(row.getCounselorName());
            sessionCounts.add(row.getSessionCount());
            totalMinutes.add(row.getTotalMinutes());
        }
        BarChartVO chart = new BarChartVO();
        chart.setXAxis(xAxis);
        chart.getSeries().add(new ChartSeriesVO("咨询人次", sessionCounts));
        chart.getSeries().add(new ChartSeriesVO("咨询总时长", totalMinutes));
        return chart;
    }

    private Map<String, Long> toMonthMap(List<MonthCountVO> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (MonthCountVO row : rows) {
            map.put(row.getMonth(), row.getCount());
        }
        return map;
    }

    private List<Long> mapCounts(List<String> months, Map<String, Long> source) {
        List<Long> values = new ArrayList<>();
        for (String month : months) {
            values.add(source.getOrDefault(month, 0L));
        }
        return values;
    }

    private List<String> buildMonthAxis(StatisticsQuery query) {
        LocalDate start = query.getStartDate();
        LocalDate end = query.getEndDate();
        if (start == null || end == null) {
            YearMonth current = YearMonth.now();
            start = current.minusMonths(5).atDay(1);
            end = current.atEndOfMonth();
        }
        List<String> months = new ArrayList<>();
        YearMonth cursor = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);
        while (!cursor.isAfter(endMonth)) {
            months.add(cursor.format(MONTH_FORMATTER));
            cursor = cursor.plusMonths(1);
        }
        return months;
    }
}
