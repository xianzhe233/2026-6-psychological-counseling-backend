package com.tyut.psychological.statistics.vo;

public class OverviewStatsVO {
    private long totalConsultations;
    private long totalStudents;
    private long completedReports;
    private long activeCounselors;

    public long getTotalConsultations() { return totalConsultations; }
    public void setTotalConsultations(long totalConsultations) { this.totalConsultations = totalConsultations; }
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getCompletedReports() { return completedReports; }
    public void setCompletedReports(long completedReports) { this.completedReports = completedReports; }
    public long getActiveCounselors() { return activeCounselors; }
    public void setActiveCounselors(long activeCounselors) { this.activeCounselors = activeCounselors; }
}
