package com.tyut.psychological.statistics.vo;

public class OverviewStatsVO {
    private long firstVisitAppointmentCount;
    private long pendingAppointmentCount;
    private long highRiskStudentCount;
    private long waitingQueueCount;
    private long consultationScheduleCount;
    private long closedCaseCount;

    public long getFirstVisitAppointmentCount() { return firstVisitAppointmentCount; }
    public void setFirstVisitAppointmentCount(long firstVisitAppointmentCount) {
        this.firstVisitAppointmentCount = firstVisitAppointmentCount;
    }
    public long getPendingAppointmentCount() { return pendingAppointmentCount; }
    public void setPendingAppointmentCount(long pendingAppointmentCount) {
        this.pendingAppointmentCount = pendingAppointmentCount;
    }
    public long getHighRiskStudentCount() { return highRiskStudentCount; }
    public void setHighRiskStudentCount(long highRiskStudentCount) {
        this.highRiskStudentCount = highRiskStudentCount;
    }
    public long getWaitingQueueCount() { return waitingQueueCount; }
    public void setWaitingQueueCount(long waitingQueueCount) {
        this.waitingQueueCount = waitingQueueCount;
    }
    public long getConsultationScheduleCount() { return consultationScheduleCount; }
    public void setConsultationScheduleCount(long consultationScheduleCount) {
        this.consultationScheduleCount = consultationScheduleCount;
    }
    public long getClosedCaseCount() { return closedCaseCount; }
    public void setClosedCaseCount(long closedCaseCount) {
        this.closedCaseCount = closedCaseCount;
    }
}
