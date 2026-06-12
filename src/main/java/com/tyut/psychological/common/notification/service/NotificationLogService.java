package com.tyut.psychological.common.notification.service;

import com.tyut.psychological.common.notification.entity.NotificationLog;
import com.tyut.psychological.common.notification.mapper.NotificationLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知日志服务类
 * 提供记录通知日志的功能
 */
@Service
public class NotificationLogService {
    private final NotificationLogMapper notificationLogMapper;

    public NotificationLogService(NotificationLogMapper notificationLogMapper) {
        this.notificationLogMapper = notificationLogMapper;
    }

    /**
     * 记录通知日志
     * @param receiverUserId 接收人用户ID
     * @param receiverName 接收人姓名
     * @param phone 手机号
     * @param notifyType 通知类型
     * @param title 标题
     * @param content 内容
     * @param relatedId 关联业务ID
     */
    public void log(Long receiverUserId, String receiverName, String phone, 
                   String notifyType, String title, String content, Long relatedId) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setReceiverUserId(receiverUserId);
        notificationLog.setReceiverName(receiverName);
        notificationLog.setPhone(phone);
        notificationLog.setNotifyType(notifyType);
        notificationLog.setTitle(title);
        notificationLog.setContent(content);
        notificationLog.setSendStatus("SUCCESS");
        notificationLog.setSendTime(LocalDateTime.now());
        notificationLog.setRelatedId(relatedId);
        
        notificationLogMapper.insert(notificationLog);
    }

    /**
     * 记录预约审核通过通知
     * @param receiverUserId 接收人用户ID
     * @param receiverName 接收人姓名
     * @param phone 手机号
     * @param appointmentId 预约ID
     * @param appointmentDate 预约日期
     * @param slotName 时间段名称
     * @param roomName 咨询室名称
     */
    public void logAppointmentApproved(Long receiverUserId, String receiverName, String phone,
                                      Long appointmentId, String appointmentDate, 
                                      String slotName, String roomName) {
        String title = "初访预约审核通过";
        String content = "您的初访预约已审核通过，预约日期：" + appointmentDate + 
                        "，时间段：" + slotName + "，地点：" + roomName;
        log(receiverUserId, receiverName, phone, "APPOINTMENT_APPROVED", title, content, appointmentId);
    }

    /**
     * 记录预约改约通知
     * @param receiverUserId 接收人用户ID
     * @param receiverName 接收人姓名
     * @param phone 手机号
     * @param appointmentId 预约ID
     * @param appointmentDate 新预约日期
     * @param slotName 新时间段名称
     * @param roomName 新咨询室名称
     */
    public void logAppointmentRescheduled(Long receiverUserId, String receiverName, String phone,
                                         Long appointmentId, String appointmentDate, 
                                         String slotName, String roomName) {
        String title = "初访预约改约通知";
        String content = "您的初访预约已改约，新预约日期：" + appointmentDate + 
                        "，时间段：" + slotName + "，地点：" + roomName;
        log(receiverUserId, receiverName, phone, "APPOINTMENT_RESCHEDULED", title, content, appointmentId);
    }

    public void logConsultationArranged(Long receiverUserId, String receiverName, String phone,
                                        Long scheduleId, String consultationDate,
                                        String slotName, String roomName) {
        String title = "正式咨询安排通知";
        String content = "您的正式咨询已安排，日期：" + consultationDate
                + "，时间段：" + slotName + "，地点：" + roomName;
        log(receiverUserId, receiverName, phone, "CONSULTATION_ARRANGED", title, content, scheduleId);
    }

    public void logConsultationCanceled(Long receiverUserId, String receiverName, String phone,
                                        Long scheduleId, String consultationDate,
                                        String slotName, String roomName, String reason) {
        String title = "咨询安排取消通知";
        String content = "您的正式咨询安排已取消，原日期：" + consultationDate
                + "，时间段：" + slotName + "，地点：" + roomName
                + "，原因：" + reason;
        log(receiverUserId, receiverName, phone, "CONSULTATION_CANCELED", title, content, scheduleId);
    }

    public void logExtensionAudited(Long receiverUserId, String receiverName, String phone,
                                    Long requestId, String status, String remark) {
        String title = "追加咨询审核结果";
        String statusLabel = "APPROVED".equals(status) ? "通过" : "驳回";
        String content = "您的追加咨询申请已审核，结果：" + statusLabel;
        if (remark != null && !remark.isBlank()) {
            content += "，备注：" + remark;
        }
        log(receiverUserId, receiverName, phone, "EXTENSION_AUDITED", title, content, requestId);
    }
}