# 后端模块与类设计

> 本文件按模块列出建议包、Controller、Service、Mapper、DTO、VO 和关键方法，便于直接分工实现。

---

## 1. common 公共模块

### 1.1 包结构

```text
common/
├── api/
│   ├── Result.java
│   ├── PageResult.java
│   └── PageQuery.java
├── exception/
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── enums/
│   ├── RoleCode.java
│   ├── RiskLevel.java
│   ├── AppointmentStatus.java
│   ├── FirstVisitConclusion.java
│   ├── QueueStatus.java
│   ├── ScheduleStatus.java
│   └── ReportStatus.java
└── util/
    ├── PasswordUtils.java
    ├── SessionUtils.java
    ├── DateTimeUtils.java
    └── FileDownloadUtils.java
```

### 1.2 Result

方法：

- `success()`
- `success(T data)`
- `fail(Integer code, String message)`

### 1.3 BusinessException

字段：

- `code`
- `message`

用于业务冲突、无权限、数据不存在等场景。

### 1.4 PasswordUtils

方法：

- `String hash(String rawPassword)`：生成密码摘要。
- `boolean matches(String rawPassword, String passwordHash)`：校验密码。

课程项目可用 SHA-256 + salt 简化实现，避免明文或简单 MD5。

---

## 2. config 配置模块

### 2.1 LoginInterceptor

职责：检查 Session 中是否存在 `LOGIN_USER`。

方法：

- `preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)`

逻辑：

1. OPTIONS 请求放行。
2. `/api/auth/login` 放行。
3. Session 中无用户返回 401。
4. 有用户放行。

### 2.2 WebMvcConfig

职责：注册拦截器、配置 CORS。

注意：前端使用 Session，CORS 必须允许 credentials。

---

## 3. auth 登录模块

### 3.1 AuthController

路径：`/api/auth`

方法：

| 方法 | 路径 | Service |
|:--|:--|:--|
| `login` | `POST /login` | `AuthService.login` |
| `logout` | `POST /logout` | 清空 Session |
| `current` | `GET /current` | 从 Session 返回当前用户 |

### 3.2 AuthService

关键方法：

```java
CurrentUserVO login(LoginRequest request, HttpSession session);
CurrentUserVO buildCurrentUser(SysUser user);
```

登录逻辑：

1. 根据 username 查询用户。
2. 判断是否存在、是否启用、密码是否正确。
3. 查询角色列表。
4. 更新最后登录时间。
5. 写入 Session。
6. 返回 CurrentUserVO。

---

## 4. user 用户模块

### 4.1 UserController

路径：`/api/admin/users`

方法：

- `page(UserQuery query)`
- `create(UserSaveRequest request)`
- `update(Long id, UserSaveRequest request)`
- `enable(Long id)`
- `disable(Long id)`
- `resetPassword(Long id)`

### 4.2 UserService

关键方法：

```java
PageResult<UserVO> pageUsers(UserQuery query);
Long createUser(UserSaveRequest request);
void updateUser(Long id, UserSaveRequest request);
void enableUser(Long id);
void disableUser(Long id, Long currentUserId);
String resetPassword(Long id);
List<RoleCode> getUserRoles(Long userId);
```

实现要点：

- 新增用户时写 `sys_user` 和 `sys_user_role`。
- 用户名唯一。
- 禁用用户不能禁用自己。
- 重置密码使用系统默认密码并返回给管理员。

### 4.3 UserMapper

建议方法：

- `SysUser selectByUsername(String username)`
- `SysUser selectById(Long id)`
- `int insert(SysUser user)`
- `int update(SysUser user)`
- `List<RoleCode> selectRoleCodesByUserId(Long userId)`
- `List<UserVO> pageUsers(UserQuery query)`
- `long countUsers(UserQuery query)`

---

## 5. profile 档案模块

### 5.1 StudentProfileService

方法：

```java
StudentProfile getByUserId(Long userId);
StudentProfile getRequiredByUserId(Long userId);
void ensureStudentProfile(Long userId, String studentNo, String realName, String phone);
```

### 5.2 StaffProfileService

方法：

```java
PageResult<StaffVO> pageStaff(StaffQuery query);
Long createStaff(StaffSaveRequest request);
void updateStaff(Long id, StaffSaveRequest request);
StaffProfile getRequired(Long staffId);
List<OptionVO> getStaffOptions(String staffType);
```

实现要点：

- 工作人员必须关联用户。
- staffType 与角色应保持一致，例如 COUNSELOR 对应 COUNSELOR 角色。
- 停用工作人员后，不应允许新增值班。

---

## 6. schedule 时间与值班模块

### 6.1 TimeSlotService

方法：

```java
PageResult<TimeSlotVO> page(TimeSlotQuery query);
Long create(TimeSlotSaveRequest request);
void update(Long id, TimeSlotSaveRequest request);
List<OptionVO> options();
TimeSlot getRequired(Long id);
```

校验：

- `endTime` 必须晚于 `startTime`。
- 已被使用的时间段不建议删除，只停用。

### 6.2 RoomService

方法：

```java
PageResult<RoomVO> page(RoomQuery query);
Long create(RoomSaveRequest request);
void update(Long id, RoomSaveRequest request);
List<OptionVO> options();
```

### 6.3 DutyScheduleService

方法：

```java
PageResult<DutyScheduleVO> page(DutyScheduleQuery query);
Long create(DutyScheduleSaveRequest request);
void update(Long id, DutyScheduleSaveRequest request);
BatchDutyResultVO batchCreate(BatchDutyRequest request);
void checkStaffDutyConflict(Long staffId, LocalDate date, Long slotId, Long excludeId);
void increaseReservedCount(Long dutyScheduleId);
void decreaseReservedCount(Long dutyScheduleId);
List<AvailableSlotVO> getAvailableSlots(LocalDate date, Long interviewerId);
```

实现要点：

- 新增/修改时校验工作人员类型和时间冲突。
- `increaseReservedCount` 必须判断不能超过 capacity。
- `decreaseReservedCount` 不能小于 0。

---

## 7. firstvisit 首访模块

### 7.1 RiskAssessmentService

方法：

```java
RiskAssessmentResult assess(FirstVisitFormRequest request);
```

返回：

- `riskScore`
- `riskLevel`

算法见 `business-rules.md`。

### 7.2 FirstVisitFormService

方法：

```java
FirstVisitFormVO getLatest(Long studentUserId);
FirstVisitFormVO submit(Long studentUserId, FirstVisitFormRequest request);
FirstVisitForm getRequired(Long formId);
void checkFormOwner(Long formId, Long studentId);
```

实现要点：

- 根据 Session 用户找到 student_profile。
- 风险分数只由后端计算。
- 提交后 form_status=SUBMITTED。

### 7.3 ConsentService

方法：

```java
ConsentVO getStatus(Long studentUserId, Long formId);
ConsentVO sign(Long studentUserId, SignConsentRequest request, String ip);
boolean hasSigned(Long formId);
```

实现要点：

- 校验 form 属于当前学生。
- 同一 formId 不重复签署。

### 7.4 FirstVisitAppointmentService

方法：

```java
List<AvailableSlotVO> getAvailableSlots(Long studentUserId, AvailableSlotQuery query);
AppointmentCreateVO createAppointment(Long studentUserId, CreateAppointmentRequest request);
PageResult<MyAppointmentVO> pageMyAppointments(Long studentUserId, MyAppointmentQuery query);
void cancelByStudent(Long studentUserId, Long appointmentId, CancelRequest request);
PageResult<AppointmentAuditVO> pageForAudit(AppointmentAuditQuery query);
AppointmentDetailVO getAuditDetail(Long appointmentId);
void approve(Long adminUserId, Long appointmentId, ApproveAppointmentRequest request);
void reject(Long adminUserId, Long appointmentId, RejectAppointmentRequest request);
void reschedule(Long adminUserId, Long appointmentId, ApproveAppointmentRequest request);
void markPriority(Long adminUserId, Long appointmentId);
```

关键事务：

- 创建预约。
- 审核通过/驳回/改约。
- 学生撤销。

业务校验：

- 是否已签同意书。
- 是否已有未完成预约。
- 值班容量是否充足。
- 撤销是否满足提前一天规则。

### 7.5 FirstVisitResultService

方法：

```java
PageResult<InterviewTaskVO> pageMyTasks(Long interviewerUserId, InterviewTaskQuery query);
InterviewTaskDetailVO getTaskDetail(Long interviewerUserId, Long appointmentId);
void submitResult(Long interviewerUserId, Long appointmentId, InterviewResultRequest request);
```

提交结果逻辑：

1. 根据 interviewerUserId 查 staff_profile。
2. 校验预约属于该初访员。
3. 插入 first_visit_result。
4. 更新预约状态 COMPLETED。
5. 若结论为 ARRANGE_CONSULTATION，调用 `ConsultationQueueService.enqueue()`。

---

## 8. consultation 咨询模块

### 8.1 ConsultationQueueService

方法：

```java
void enqueue(FirstVisitResult result);
PageResult<ConsultationQueueVO> pageQueue(ConsultationQueueQuery query);
ConsultationQueueDetailVO getDetail(Long queueId);
void suspend(Long queueId, String reason);
void markArranged(Long queueId);
```

优先级计算：

```java
int calculatePriority(RiskLevel crisisLevel, boolean priorityFlag, LocalDateTime enqueueTime);
```

### 8.2 ConsultationScheduleService

方法：

```java
List<AvailableSlotVO> getCounselorAvailableSlots(AvailableCounselorSlotQuery query);
ArrangeResultVO arrange(Long assistantUserId, ArrangeConsultationRequest request);
PageResult<ConsultationScheduleVO> pageForAssistant(ScheduleQuery query);
PageResult<MyScheduleVO> pageForCounselor(Long counselorUserId, CounselorScheduleQuery query);
ScheduleDetailVO getCounselorScheduleDetail(Long counselorUserId, Long scheduleId);
void cancel(Long assistantUserId, Long scheduleId, CancelRequest request);
void updateStatusFromRecord(Long scheduleId, ScheduleStatus status);
```

单次咨询安排核心逻辑：

```java
LocalDate date = request.getConsultationDate();
checkCounselorConflict(counselorId, date, slotId);
checkStudentConflict(studentId, date, slotId);
checkRoomConflict(roomId, date, slotId);
insertSchedule(sessionNo = nextSessionNo);
```

注意：先完成冲突校验，再插入咨询安排记录，避免产生无效占用。

### 8.3 ConsultationRecordService

方法：

```java
ConsultationRecordVO getBySchedule(Long counselorUserId, Long scheduleId);
void saveRecord(Long counselorUserId, Long scheduleId, ConsultationRecordRequest request);
```

实现要点：

- 校验 schedule 属于当前咨询师。
- 保存记录后更新 schedule 状态。
- 若状态 CLOSED，可提示或触发结案流程。

### 8.4 ExtensionRequestService

方法：

```java
PageResult<ExtensionRequestVO> pageMyRequests(Long counselorUserId, ExtensionQuery query);
Long create(Long counselorUserId, ExtensionCreateRequest request);
PageResult<ExtensionRequestVO> pageForAdmin(ExtensionAdminQuery query);
void approve(Long adminUserId, Long requestId, AuditRequest request);
void reject(Long adminUserId, Long requestId, AuditRequest request);
```

---

## 9. report 结案报告模块

### 9.1 CaseReportService

方法：

```java
PageResult<CaseReportVO> pageMyReports(Long counselorUserId, CaseReportQuery query);
CaseReportDetailVO getMyReport(Long counselorUserId, Long reportId);
Long save(Long counselorUserId, CaseReportRequest request);
void update(Long counselorUserId, Long reportId, CaseReportRequest request);
void submit(Long counselorUserId, Long reportId);
PageResult<CaseReportVO> pageForAdmin(CaseReportAdminQuery query);
CaseReportDetailVO getForAdmin(Long reportId);
CaseReportExportDTO getExportData(Long currentUserId, Long reportId, RoleCode role);
```

实现要点：

- 咨询师只能操作自己的报告。
- 管理员可以查看已提交报告。
- 草稿可修改，已提交是否允许修改需统一规则；建议允许咨询师修改但记录 update_time。

### 9.2 CaseReportExportService

方法：

```java
void exportWord(CaseReportExportDTO data, HttpServletResponse response);
```

Word 字段：

- 来访者学号
- 来访者姓名
- 来访者性别
- 来访者院系
- 来访者联系电话
- 问题类型
- 咨询总次数
- 咨询效果自评
- 个案总结
- 后续建议

---

## 10. statistics 统计模块

### 10.1 StatisticsService

方法：

```java
OverviewStatsVO overview(StatisticsQuery query);
LineChartVO monthlyTrend(StatisticsQuery query);
List<PieItemVO> problemTypeDistribution(StatisticsQuery query);
BarChartVO crisisLevelDistribution(StatisticsQuery query);
BarChartVO counselorWorkload(StatisticsQuery query);
```

### 10.2 StatisticsMapper

建议 SQL：

- 预约数量：按 `first_visit_appointment.create_time` 聚合。
- 问题类型：关联 `first_visit_result.problem_type_id`。
- 危机等级：按 `first_visit_result.crisis_level` 聚合。
- 咨询师工作量：按 `consultation_record.counselor_id` 聚合。
- 结案数：按 `case_report.report_status=SUBMITTED` 聚合。

---

## 11. log 日志模块

### 11.1 NotificationLogService

方法：

```java
void mockSend(Long receiverUserId, String phone, String notifyType, String title, String content, Long relatedId);
PageResult<NotificationLogVO> page(NotificationLogQuery query);
```

调用场景：

- 预约审核通过。
- 预约改约。
- 咨询安排成功。
- 咨询取消。
- 追加申请审核。

### 11.2 OperationLogService

方法：

```java
void record(Long operatorUserId, String roleCode, String moduleName, String operationType, String desc, boolean success, String errorMessage);
PageResult<OperationLogVO> page(OperationLogQuery query);
```

初期手动调用即可。

---

## 12. 分工建议

### 后端成员 A：基础与认证

- common、config、auth、user、profile。
- Session、角色、用户维护。

### 后端成员 B：学生预约与管理员审核

- schedule、first_visit_form、consent、appointment、audit。

### 后端成员 C：初访与咨询安排

- first_visit_result、consultation_queue、consultation_schedule、冲突检测、咨询安排追加。

### 后端成员 D：咨询记录、结案、统计

- consultation_record、extension_request、case_report、export、statistics、logs。
