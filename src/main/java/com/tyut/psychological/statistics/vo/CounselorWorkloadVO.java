package com.tyut.psychological.statistics.vo;

public class CounselorWorkloadVO {
    private Long counselorId;
    private String counselorName;
    private long consultationCount;
    private long studentCount;
    private long reportCount;

    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public String getCounselorName() { return counselorName; }
    public void setCounselorName(String counselorName) { this.counselorName = counselorName; }
    public long getConsultationCount() { return consultationCount; }
    public void setConsultationCount(long consultationCount) { this.consultationCount = consultationCount; }
    public long getStudentCount() { return studentCount; }
    public void setStudentCount(long studentCount) { this.studentCount = studentCount; }
    public long getReportCount() { return reportCount; }
    public void setReportCount(long reportCount) { this.reportCount = reportCount; }
}
