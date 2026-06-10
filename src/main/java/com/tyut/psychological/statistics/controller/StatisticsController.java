package com.tyut.psychological.statistics.controller;

import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.service.StatisticsService;
import com.tyut.psychological.statistics.vo.ChartVO;
import com.tyut.psychological.statistics.vo.CounselorWorkloadVO;
import com.tyut.psychological.statistics.vo.OverviewStatsVO;
import com.tyut.psychological.statistics.vo.PieItemVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    private final StatisticsService service;

    public StatisticsController(StatisticsService service) { this.service = service; }

    private StatisticsQuery q(LocalDate start, LocalDate end) {
        StatisticsQuery q = new StatisticsQuery();
        q.setStartDate(start); q.setEndDate(end);
        return q;
    }

    @GetMapping("/overview")
    public Map<String, OverviewStatsVO> overview(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.overview(q(startDate, endDate)));
    }

    @GetMapping("/consultation-trend")
    public Map<String, ChartVO> consultationTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.consultationTrend(q(startDate, endDate)));
    }

    @GetMapping("/completion-trend")
    public Map<String, ChartVO> completionTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.completionTrend(q(startDate, endDate)));
    }

    @GetMapping("/new-student-trend")
    public Map<String, ChartVO> newStudentTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.newStudentTrend(q(startDate, endDate)));
    }

    @GetMapping("/consultation-distribution")
    public Map<String, List<PieItemVO>> consultationDistribution(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.consultationDistribution(q(startDate, endDate)));
    }

    @GetMapping("/problem-type-distribution")
    public Map<String, List<PieItemVO>> problemTypeDistribution(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.problemTypeDistribution(q(startDate, endDate)));
    }

    @GetMapping("/workload-chart")
    public Map<String, ChartVO> workloadChart(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.workload(q(startDate, endDate)));
    }

    @GetMapping("/workload-table")
    public Map<String, List<CounselorWorkloadVO>> workloadTable(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Map.of("data", service.workloadTable(q(startDate, endDate)));
    }
}
