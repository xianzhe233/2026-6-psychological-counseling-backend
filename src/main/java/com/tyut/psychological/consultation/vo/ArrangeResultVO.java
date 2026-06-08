package com.tyut.psychological.consultation.vo;

public class ArrangeResultVO {
    private Long id;
    private String scheduleNo;

    public ArrangeResultVO() {
    }

    public ArrangeResultVO(Long id, String scheduleNo) {
        this.id = id;
        this.scheduleNo = scheduleNo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getScheduleNo() { return scheduleNo; }
    public void setScheduleNo(String scheduleNo) { this.scheduleNo = scheduleNo; }
}
