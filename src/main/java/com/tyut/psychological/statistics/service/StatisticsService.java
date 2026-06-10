package com.tyut.psychological.statistics.service;

import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.mapper.StatisticsMapper;
import com.tyut.psychological.statistics.vo.ChartSeriesVO;
import com.tyut.psychological.statistics.vo.ChartVO;
import com.tyut.psychological.statistics.vo.CounselorWorkloadVO;
import com.tyut.psychological.statistics.vo.MonthCountVO;
import com.tyut.psychological.statistics.vo.OverviewStatsVO;
import com.tyut.psychological.statistics.vo.PieItemVO;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private final StatisticsMapper mapper;

    public StatisticsService(StatisticsMapper mapper) { this.mapper = mapper; }

    public OverviewStatsVO overview(StatisticsQuery q) { return mapper.overview(q); }

    public ChartVO consultationTrend(StatisticsQuery q) {
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
        return mapper.consultationDistribution(q);
    }

    public List<PieItemVO> problemTypeDistribution(StatisticsQuery q) {
        return mapper.problemTypeDistribution(q);
    }

    public ChartVO workload(StatisticsQuery q) {
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
        return mapper.counselorWorkload(q);
    }

    private List<String> monthAxis(StatisticsQuery q) {
        return q.getStartDate().datesUntil(q.getEndDate().plusMonths(1), java.time.temporal.ChronoUnit.MONTHS)
                .map(MONTH_FMT::format).collect(Collectors.toList());
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
