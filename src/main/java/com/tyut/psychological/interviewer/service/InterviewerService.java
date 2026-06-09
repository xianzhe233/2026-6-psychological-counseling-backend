package com.tyut.psychological.interviewer.service;

import com.tyut.psychological.appointment.entity.FirstVisitAppointment;
import com.tyut.psychological.appointment.mapper.FirstVisitAppointmentMapper;
import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.entity.ConsultationQueue;
import com.tyut.psychological.consultation.mapper.ConsultationQueueMapper;
import com.tyut.psychological.interviewer.dto.InterviewResultRequest;
import com.tyut.psychological.interviewer.entity.FirstVisitResult;
import com.tyut.psychological.interviewer.mapper.FirstVisitResultMapper;
import com.tyut.psychological.interviewer.vo.InterviewTaskDetailVO;
import com.tyut.psychological.interviewer.vo.InterviewTaskVO;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.student.entity.FirstVisitForm;
import com.tyut.psychological.student.mapper.StudentFormMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 初访员服务类
 * 实现初访任务列表、详情、提交初访结果等功能
 */
@Service
public class InterviewerService {
    
    private final FirstVisitAppointmentMapper appointmentMapper;
    private final FirstVisitResultMapper firstVisitResultMapper;
    private final StaffProfileMapper staffProfileMapper;
    private final StudentFormMapper studentFormMapper;
    private final ConsultationQueueMapper consultationQueueMapper;
    private final OperationLogService operationLogService;
    
    public InterviewerService(FirstVisitAppointmentMapper appointmentMapper,
                             FirstVisitResultMapper firstVisitResultMapper,
                             StaffProfileMapper staffProfileMapper,
                             StudentFormMapper studentFormMapper,
                             ConsultationQueueMapper consultationQueueMapper,
                             OperationLogService operationLogService) {
        this.appointmentMapper = appointmentMapper;
        this.firstVisitResultMapper = firstVisitResultMapper;
        this.staffProfileMapper = staffProfileMapper;
        this.studentFormMapper = studentFormMapper;
        this.consultationQueueMapper = consultationQueueMapper;
        this.operationLogService = operationLogService;
    }
    
    /**
     * 分页查询初访任务列表
     * 只能查看分配给当前初访员的任务
     * @param interviewerUserId 初访员用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 预约状态
     * @param riskLevel 风险等级
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageResult<InterviewTaskVO> pageInterviewTasks(Long interviewerUserId, LocalDate startDate, LocalDate endDate,
                                                         String status, String riskLevel, Integer pageNum, Integer pageSize) {
        // 获取初访员的staff_profile ID
        StaffProfile staffProfile = staffProfileMapper.selectByUserId(interviewerUserId);
        if (staffProfile == null) {
            throw new BusinessException(403, "当前用户不是工作人员");
        }
        if (!"INTERVIEWER".equals(staffProfile.getStaffType())) {
            throw new BusinessException(403, "当前用户不是初访员");
        }
        
        Long interviewerId = staffProfile.getId();
        List<InterviewTaskVO> records = appointmentMapper.pageInterviewTasks(interviewerId, startDate, endDate, status, riskLevel);
        long total = appointmentMapper.countInterviewTasks(interviewerId, startDate, endDate, status, riskLevel);
        
        // 内存分页（数据量小）
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, records.size());
        List<InterviewTaskVO> page = from < records.size() ? records.subList(from, to) : List.of();
        long pages = (total + pageSize - 1) / pageSize;
        
        return new PageResult<>(page, total, pageNum, pageSize, pages);
    }
    
    /**
     * 获取初访任务详情
     * 包含学生信息、登记表摘要、预约信息
     * @param appointmentId 预约ID
     * @param interviewerUserId 初访员用户ID
     * @return 任务详情
     */
    public InterviewTaskDetailVO getInterviewTaskDetail(Long appointmentId, Long interviewerUserId) {
        // 获取初访员的staff_profile ID
        StaffProfile staffProfile = staffProfileMapper.selectByUserId(interviewerUserId);
        if (staffProfile == null) {
            throw new BusinessException(403, "当前用户不是工作人员");
        }
        if (!"INTERVIEWER".equals(staffProfile.getStaffType())) {
            throw new BusinessException(403, "当前用户不是初访员");
        }
        
        Long interviewerId = staffProfile.getId();
        
        // 查询预约详情
        InterviewTaskDetailVO detail = appointmentMapper.selectInterviewTaskDetail(appointmentId, interviewerId);
        if (detail == null) {
            throw new BusinessException(404, "初访任务不存在或不属于当前初访员");
        }
        
        // 查询首访登记表摘要
        if (detail.getFormId() != null) {
            FirstVisitForm form = studentFormMapper.selectById(detail.getFormId());
            if (form != null) {
                detail.setMainProblem(form.getMainProblem());
                detail.setProblemDescription(form.getProblemDescription());
                detail.setRiskLevel(form.getRiskLevel());
                detail.setRiskScore(form.getRiskScore());
            }
        }
        
        return detail;
    }
    
    /**
     * 提交初访结果
     * 校验预约状态必须为APPROVED，校验初访员归属
     * 写入first_visit_result，更新预约状态为COMPLETED
     * 若结论为ARRANGE_CONSULTATION，创建咨询队列
     * @param appointmentId 预约ID
     * @param interviewerUserId 初访员用户ID
     * @param request 初访结果请求
     */
    @Transactional
    public void submitInterviewResult(Long appointmentId, Long interviewerUserId, InterviewResultRequest request) {
        // 获取初访员的staff_profile ID
        StaffProfile staffProfile = staffProfileMapper.selectByUserId(interviewerUserId);
        if (staffProfile == null) {
            throw new BusinessException(403, "当前用户不是工作人员");
        }
        if (!"INTERVIEWER".equals(staffProfile.getStaffType())) {
            throw new BusinessException(403, "当前用户不是初访员");
        }
        
        Long interviewerId = staffProfile.getId();
        
        // 查询预约
        FirstVisitAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        
        // 校验预约状态必须为APPROVED
        if (!"APPROVED".equals(appointment.getAppointmentStatus())) {
            throw new BusinessException(400, "只有已通过状态的预约才能录入初访结果");
        }
        
        // 校验初访员归属
        if (!interviewerId.equals(appointment.getInterviewerId())) {
            throw new BusinessException(403, "该预约不属于当前初访员");
        }
        
        // 检查是否已存在初访结果
        FirstVisitResult existingResult = firstVisitResultMapper.selectByAppointmentId(appointmentId);
        if (existingResult != null) {
            throw new BusinessException(400, "该预约已提交过初访结果");
        }
        
        // 校验结论为TRANSFER时，后续建议必填
        if ("TRANSFER".equals(request.getConclusion()) && (request.getNextAction() == null || request.getNextAction().isBlank())) {
            throw new BusinessException(400, "转介送诊时后续建议必填");
        }
        
        // 创建初访结果
        FirstVisitResult firstVisitResult = new FirstVisitResult();
        firstVisitResult.setAppointmentId(appointmentId);
        firstVisitResult.setInterviewerId(interviewerId);
        firstVisitResult.setCrisisLevel(request.getCrisisLevel());
        firstVisitResult.setProblemTypeId(request.getProblemTypeId());
        firstVisitResult.setInterviewTime(request.getInterviewTime());
        firstVisitResult.setConclusion(request.getConclusion());
        firstVisitResult.setSummary(request.getSummary());
        firstVisitResult.setNextAction(request.getNextAction());
        
        // 插入初访结果
        firstVisitResultMapper.insert(firstVisitResult);
        
        // 更新预约状态为COMPLETED
        appointment.setAppointmentStatus("COMPLETED");
        appointmentMapper.update(appointment);
        
        // 若结论为ARRANGE_CONSULTATION，创建咨询队列
        if ("ARRANGE_CONSULTATION".equals(request.getConclusion())) {
            createConsultationQueue(appointment, firstVisitResult);
        }
        
        // 写操作日志
        writeOperationLog("提交初访结果", "初访任务", "预约ID: " + appointmentId + ", 初访员ID: " + interviewerId);
    }
    
    /**
     * 创建咨询队列
     * 当初访结论为安排咨询时，将学生加入咨询队列
     * @param appointment 预约信息
     * @param firstVisitResult 初访结果
     */
    private void createConsultationQueue(FirstVisitAppointment appointment, FirstVisitResult firstVisitResult) {
        ConsultationQueue queue = new ConsultationQueue();
        queue.setStudentId(appointment.getStudentId());
        queue.setFirstVisitResultId(firstVisitResult.getId());
        queue.setProblemTypeId(firstVisitResult.getProblemTypeId());
        queue.setCrisisLevel(firstVisitResult.getCrisisLevel());
        queue.setPriorityScore(calculatePriorityScore(firstVisitResult.getCrisisLevel(), appointment.getPriorityFlag()));
        queue.setQueueStatus("WAITING");
        queue.setEnqueueTime(LocalDateTime.now());
        
        consultationQueueMapper.insert(queue);
    }
    
    /**
     * 计算优先级分数
     * 根据危机等级和优先标记计算
     * @param crisisLevel 危机等级
     * @param priorityFlag 优先标记
     * @return 优先级分数
     */
    private int calculatePriorityScore(String crisisLevel, Integer priorityFlag) {
        int score = 0;
        
        // 根据危机等级设置基础分数
        if ("URGENT".equals(crisisLevel)) {
            score = 1000;
        } else if ("HIGH".equals(crisisLevel)) {
            score = 800;
        } else if ("MEDIUM".equals(crisisLevel)) {
            score = 500;
        } else {
            score = 100;
        }
        
        // 若有优先标记，额外加100分
        if (priorityFlag != null && priorityFlag == 1) {
            score += 100;
        }
        
        return score;
    }
    
    /**
     * 写操作日志
     * @param operationType 操作类型
     * @param moduleName 模块名称
     * @param operationDesc 操作描述
     */
    private void writeOperationLog(String operationType, String moduleName, String operationDesc) {
        operationLogService.logSuccess(moduleName, operationType, operationDesc);
    }
}