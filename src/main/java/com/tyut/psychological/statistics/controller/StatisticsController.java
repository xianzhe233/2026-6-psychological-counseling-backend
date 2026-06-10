package com.tyut.psychological.statistics.controller;

import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.statistics.dto.StatisticsQuery;
import com.tyut.psychological.statistics.service.StatisticsService;
import com.tyut.psychological.statistics.vo.BarChartVO;
import com.tyut.psychological.statistics.vo.LineChartVO;
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

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public Result<OverviewStatsVO> overview(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long problemTypeId,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(statisticsService.getOverview(buildQuery(startDate, endDate, counselorId, problemTypeId)));
    }

    @GetMapping("/monthly-trend")
    public Result<LineChartVO> monthlyTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long problemTypeId,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(statisticsService.getMonthlyTrend(buildQuery(startDate, endDate, counselorId, problemTypeId)));
    }

    @GetMapping("/problem-types")
    public Result<List<PieItemVO>> problemTypes(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long problemTypeId,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(statisticsService.getProblemTypeDistribution(buildQuery(startDate, endDate, counselorId, problemTypeId)));
    }

    @GetMapping("/crisis-levels")
    public Result<BarChartVO> crisisLevels(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long problemTypeId,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(statisticsService.getCrisisLevelDistribution(buildQuery(startDate, endDate, counselorId, problemTypeId)));
    }

    @GetMapping("/counselor-workload")
    public Result<BarChartVO> counselorWorkload(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long problemTypeId,
            HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(statisticsService.getCounselorWorkload(buildQuery(startDate, endDate, counselorId, problemTypeId)));
    }

    private StatisticsQuery buildQuery(LocalDate startDate, LocalDate endDate, Long counselorId, Long problemTypeId) {
        StatisticsQuery query = new StatisticsQuery();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCounselorId(counselorId);
        query.setProblemTypeId(problemTypeId);
        return query;
    }
}
