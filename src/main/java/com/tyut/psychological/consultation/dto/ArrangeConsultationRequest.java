package com.tyut.psychological.consultation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ArrangeConsultationRequest {
    @NotNull(message = "队列ID不能为空")
    private Long queueId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "咨询师ID不能为空")
    private Long counselorId;

    @NotNull(message = "咨询日期不能为空")
    private LocalDate consultationDate;

    @NotNull(message = "时间段ID不能为空")
    private Long slotId;

    @NotNull(message = "咨询室ID不能为空")
    private Long roomId;

    private String remark;

    public Long getQueueId() { return queueId; }
    public void setQueueId(Long queueId) { this.queueId = queueId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }
    public LocalDate getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDate consultationDate) { this.consultationDate = consultationDate; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
