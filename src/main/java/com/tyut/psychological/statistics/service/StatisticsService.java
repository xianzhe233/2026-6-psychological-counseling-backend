package com.tyut.psychological.statistics.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.mapper.StatisticsMapper;
import com.tyut.psychological.statistics.vo.ChartSeriesVO;
import com.tyut.psychological.statistics.vo.ChartVO;
import com.tyut.psychological.statistics.vo.CounselorWorkloadVO;
import com.tyut.psychological.statistics.vo.MonthCountVO;
import com.tyut.psychological.statistics.vo.OverviewStatsVO;
import com.tyut.psychological.statistics.vo.PieItemVO;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private final StatisticsMapper mapper;

    public StatisticsService(StatisticsMapper mapper) { this.mapper = mapper; }

    public OverviewStatsVO overview(StatisticsQuery q) {
        validateRange(q);
        OverviewStatsVO result = mapper.overview(q);
        return result == null ? new OverviewStatsVO() : result;
    }

    public ChartVO consultationTrend(StatisticsQuery q) {
        validateRange(q);
        List<MonthCountVO> raw = mapper.monthlyConsultations(q);
        ChartVO c = new ChartVO();
        c.setXAxis(monthAxis(q));
        ChartSeriesVO s = new ChartSeriesVO();
        s.setName("咨询量");
        s.setData(pick(c.getXAxis(), toMap(raw)));
        c.getSeries().add(s);
        return c;
    }

    public ChartVO completionTrend(StatisticsQuery q) {
        validateRange(q);
        List<MonthCountVO> raw = mapper.monthlyReports(q);
        ChartVO c = new ChartVO();
        c.setXAxis(monthAxis(q));
        ChartSeriesVO s = new ChartSeriesVO();
        s.setName("结案量");
        s.setData(pick(c.getXAxis(), toMap(raw)));
        c.getSeries().add(s);
        return c;
    }

    public ChartVO newStudentTrend(StatisticsQuery q) {
        validateRange(q);
        List<MonthCountVO> raw = mapper.monthlyNewStudents(q);
        ChartVO c = new ChartVO();
        c.setXAxis(monthAxis(q));
        ChartSeriesVO s = new ChartSeriesVO();
        s.setName("新增学生");
        s.setData(pick(c.getXAxis(), toMap(raw)));
        c.getSeries().add(s);
        return c;
    }

    public List<PieItemVO> consultationDistribution(StatisticsQuery q) {
        validateRange(q);
        return mapper.consultationDistribution(q);
    }

    public List<PieItemVO> problemTypeDistribution(StatisticsQuery q) {
        validateRange(q);
        return mapper.problemTypeDistribution(q);
    }

    public ChartVO workload(StatisticsQuery q) {
        validateRange(q);
        List<CounselorWorkloadVO> raw = mapper.counselorWorkload(q);
        ChartVO c = new ChartVO();
        for (CounselorWorkloadVO w : raw) c.getXAxis().add(w.getCounselorName());

        ChartSeriesVO cs = new ChartSeriesVO();
        cs.setName("咨询量");
        cs.setData(raw.stream().map(CounselorWorkloadVO::getConsultationCount).collect(Collectors.toList()));
        c.getSeries().add(cs);

        ChartSeriesVO rs = new ChartSeriesVO();
        rs.setName("结案量");
        rs.setData(raw.stream().map(CounselorWorkloadVO::getReportCount).collect(Collectors.toList()));
        c.getSeries().add(rs);
        return c;
    }

    public List<CounselorWorkloadVO> workloadTable(StatisticsQuery q) {
        validateRange(q);
        return mapper.counselorWorkload(q);
    }

    private List<String> monthAxis(StatisticsQuery q) {
        return q.getStartDate()
                .withDayOfMonth(1)
                .datesUntil(q.getEndDate().with(TemporalAdjusters.firstDayOfNextMonth()), Period.ofMonths(1))
                .map(MONTH_FMT::format).collect(Collectors.toList());
    }

    private void validateRange(StatisticsQuery q) {
        if (q == null || q.getStartDate() == null || q.getEndDate() == null) {
            throw new BusinessException(400, "统计开始日期和结束日期不能为空");
        }
        if (q.getStartDate().isAfter(q.getEndDate())) {
            throw new BusinessException(400, "开始日期不能晚于结束日期");
        }
    }

    private Map<String, Long> toMap(List<MonthCountVO> raw) {
        Map<String, Long> m = new LinkedHashMap<>();
        for (MonthCountVO r : raw) m.put(r.getMonth(), r.getCount());
        return m;
    }

    private List<Long> pick(List<String> months, Map<String, Long> map) {
        return months.stream().map(m -> map.getOrDefault(m, 0L)).collect(Collectors.toList());
    }
}
