# 后端接口设计文档

> 基础路径：`/api`  
> 返回格式：`Result<T>`  
> 认证方式：Session Cookie  
> 时间格式：`yyyy-MM-dd HH:mm:ss`，日期格式：`yyyy-MM-dd`  
> 枚举值：后端返回英文编码，前端映射中文。

---

## 1. 通用返回与分页

### 1.1 Result

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 1.2 PageResult

```json
{
  "records": [],
  "total": 0,
  "pageNum": 1,
  "pageSize": 10,
  "pages": 0
}
```

### 1.3 错误码

| code | 含义 | 示例 |
|:--:|:--|:--|
| 200 | 成功 | success |
| 400 | 参数错误 | 手机号格式不正确 |
| 401 | 未登录 | 请先登录 |
| 403 | 无权限 | 当前角色无权访问 |
| 404 | 不存在 | 预约记录不存在 |
| 409 | 业务冲突 | 该咨询师该时间段已有安排 |
| 500 | 系统异常 | 系统繁忙，请稍后重试 |

---

## 2. 认证接口

### 2.1 登录

```http
POST /api/auth/login
```

角色：匿名。

请求：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "中心管理员",
    "phone": "13800000000",
    "roles": ["ADMIN"],
    "primaryRole": "ADMIN"
  }
}
```

业务规则：

1. 用户不存在返回 400。
2. 密码错误返回 400。
3. 用户禁用返回 403。
4. 登录成功写入 Session。
5. 更新 `sys_user.last_login_time`。

### 2.2 退出登录

```http
POST /api/auth/logout
```

角色：已登录用户。

响应：`Result<Void>`。

业务规则：清空 Session。

### 2.3 当前用户

```http
GET /api/auth/current
```

角色：已登录用户。

响应同登录响应 data。

---

## 3. 公共字典接口

### 3.1 问题类型选项

```http
GET /api/common/problem-types/options
```

角色：已登录用户。

响应：

```json
[
  { "label": "学业压力", "value": 1 },
  { "label": "人际关系", "value": 2 }
]
```

### 3.2 当前用户菜单

```http
GET /api/common/menus
```

角色：已登录用户。

说明：可选接口。若前端本地维护菜单，则不必实现。

---

## 4. 管理员：用户与角色

### 4.1 用户分页

```http
GET /api/admin/users
```

角色：ADMIN。

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|:--:|:--:|:--:|:--|
| pageNum | int | 是 | 页码 |
| pageSize | int | 是 | 每页数量 |
| keyword | string | 否 | 用户名/姓名/手机号 |
| roleCode | string | 否 | 角色编码 |
| status | int | 否 | 0禁用，1正常 |

响应记录：

```json
{
  "id": 1,
  "username": "admin",
  "realName": "中心管理员",
  "phone": "13800000000",
  "email": "admin@example.com",
  "roles": ["ADMIN"],
  "status": 1,
  "lastLoginTime": "2026-06-06 10:00:00",
  "createTime": "2026-06-01 09:00:00"
}
```

### 4.2 新增用户

```http
POST /api/admin/users
```

角色：ADMIN。

请求：

```json
{
  "username": "20230001",
  "realName": "张三",
  "phone": "13800000001",
  "email": "student@example.com",
  "password": "123456",
  "roleCodes": ["STUDENT"],
  "status": 1
}
```

业务规则：

1. `username` 唯一。
2. `password` 为空时使用系统默认密码。
3. 新增学生角色时，建议同步创建 `student_profile` 基础记录或由学生首次登录补全。

### 4.3 修改用户

```http
PUT /api/admin/users/{id}
```

角色：ADMIN。

请求字段同新增，密码为空时不修改密码。

### 4.4 启用/禁用用户

```http
POST /api/admin/users/{id}/enable
POST /api/admin/users/{id}/disable
```

角色：ADMIN。

业务规则：不能禁用当前登录管理员自己。

### 4.5 重置密码

```http
POST /api/admin/users/{id}/reset-password
```

角色：ADMIN。

响应：

```json
{ "newPassword": "123456" }
```

---

## 5. 管理员：工作人员、咨询室、时间段、值班

### 5.1 工作人员分页

```http
GET /api/admin/staff
```

角色：ADMIN。

参数：`pageNum`、`pageSize`、`keyword`、`staffType`、`status`。

记录：

```json
{
  "id": 1,
  "userId": 10,
  "staffNo": "T001",
  "realName": "李老师",
  "phone": "13800000002",
  "staffType": "COUNSELOR",
  "title": "讲师",
  "specialty": "情绪压力、人际关系",
  "maxDailyAppointments": 6,
  "status": 1
}
```

### 5.2 新增/修改工作人员

```http
POST /api/admin/staff
PUT /api/admin/staff/{id}
```

角色：ADMIN。

请求：

```json
{
  "username": "teacher001",
  "realName": "李老师",
  "phone": "13800000002",
  "staffNo": "T001",
  "staffType": "COUNSELOR",
  "title": "讲师",
  "specialty": "情绪压力、人际关系",
  "introduction": "长期从事高校心理咨询工作",
  "maxDailyAppointments": 6,
  "status": 1
}
```

业务规则：

1. 工作人员必须关联一个 `sys_user`。
2. `staffType=COUNSELOR` 才能安排正式咨询。
3. `staffType=INTERVIEWER` 才能安排初访。

### 5.3 工作人员选项

```http
GET /api/admin/staff/options?staffType=COUNSELOR
```

角色：已登录用户。

响应：

```json
[
  { "label": "李老师", "value": 1, "staffType": "COUNSELOR" }
]
```

### 5.4 咨询室接口

```http
GET /api/admin/rooms
POST /api/admin/rooms
PUT /api/admin/rooms/{id}
GET /api/admin/rooms/options
```

角色：ADMIN；`options` 可允许 ASSISTANT 使用。

请求：

```json
{
  "roomName": "心理咨询室A",
  "location": "心理中心三层301",
  "capacity": 1,
  "status": 1,
  "remark": "安静房间"
}
```

### 5.5 时间段接口

```http
GET /api/admin/time-slots
POST /api/admin/time-slots
PUT /api/admin/time-slots/{id}
GET /api/admin/time-slots/options
```

请求：

```json
{
  "slotName": "上午第一段",
  "startTime": "08:30:00",
  "endTime": "09:20:00",
  "intervalMinutes": 10,
  "status": 1
}
```

业务规则：`endTime` 必须晚于 `startTime`。

### 5.6 值班分页

```http
GET /api/admin/duty-schedules
```

参数：`pageNum`、`pageSize`、`staffType`、`staffId`、`startDate`、`endDate`、`status`。

记录：

```json
{
  "id": 1,
  "staffId": 2,
  "staffName": "王老师",
  "staffType": "INTERVIEWER",
  "dutyDate": "2026-06-10",
  "slotId": 1,
  "slotName": "上午第一段",
  "startTime": "08:30:00",
  "endTime": "09:20:00",
  "roomId": 1,
  "roomName": "心理咨询室A",
  "capacity": 2,
  "reservedCount": 0,
  "remaining": 2,
  "status": 1
}
```

### 5.7 新增/修改值班

```http
POST /api/admin/duty-schedules
PUT /api/admin/duty-schedules/{id}
```

请求：

```json
{
  "staffId": 2,
  "staffType": "INTERVIEWER",
  "dutyDate": "2026-06-10",
  "slotId": 1,
  "roomId": 1,
  "capacity": 2,
  "status": 1
}
```

业务规则：

1. 同一 `staffId + dutyDate + slotId` 不能重复。
2. `capacity` 不能小于 `reservedCount`。
3. 只有启用工作人员才能排班。

冲突返回：`409`。

### 5.8 批量排班

```http
POST /api/admin/duty-schedules/batch
```

请求：

```json
{
  "staffId": 2,
  "staffType": "INTERVIEWER",
  "startDate": "2026-06-10",
  "endDate": "2026-06-30",
  "weekdays": [1, 3, 5],
  "slotIds": [1, 2],
  "roomId": 1,
  "capacity": 2
}
```

响应：

```json
{
  "createdCount": 10,
  "skippedCount": 2,
  "conflicts": [
    { "date": "2026-06-12", "slotId": 1, "reason": "该老师该时间段已有值班" }
  ]
}
```

---

## 6. 学生端：首访登记、同意书、预约

### 6.1 最新首访登记表

```http
GET /api/student/first-visit/forms/latest
```

角色：STUDENT。

响应：`FirstVisitFormVO` 或 `null`。

### 6.2 提交首访登记表

```http
POST /api/student/first-visit/forms
```

角色：STUDENT。

请求：

```json
{
  "mainProblem": "近期学习压力较大",
  "problemDescription": "最近考试和课程压力较大，睡眠不好。",
  "expectedHelp": "希望能获得压力调节建议。",
  "moodScore": 6,
  "sleepScore": 5,
  "stressScore": 8,
  "selfHarmFlag": 0,
  "emergencyFlag": 0
}
```

响应：

```json
{
  "id": 1,
  "studentId": 1,
  "riskScore": 19,
  "riskLevel": "LOW",
  "formStatus": "SUBMITTED",
  "submitTime": "2026-06-06 12:00:00"
}
```

业务规则：

1. 计算风险分数和等级。
2. 保存表单状态为 `SUBMITTED`。
3. 每次提交生成新表单或更新草稿，具体实现可简化为每次创建一条记录。

### 6.3 查询同意书状态

```http
GET /api/student/consents/status?formId=1
```

角色：STUDENT。

### 6.4 签署知情同意书

```http
POST /api/student/consents/sign
```

请求：

```json
{
  "formId": 1,
  "consentVersion": "v1.0"
}
```

业务规则：同一 `formId` 只能有一条有效签署记录。

### 6.5 查询可预约初访时段

```http
GET /api/student/appointments/available-slots?date=2026-06-10&interviewerId=2
```

角色：STUDENT。

响应记录：

```json
{
  "dutyScheduleId": 1,
  "interviewerId": 2,
  "interviewerName": "王老师",
  "appointmentDate": "2026-06-10",
  "slotId": 1,
  "slotName": "上午第一段",
  "startTime": "08:30:00",
  "endTime": "09:20:00",
  "roomId": 1,
  "roomName": "心理咨询室A",
  "capacity": 2,
  "reservedCount": 0,
  "remaining": 2,
  "available": true,
  "disabledReason": null
}
```

业务规则：只返回 `staffType=INTERVIEWER` 且启用、剩余容量大于 0 的值班记录；不可用记录可选择返回并标记 `available=false`。

### 6.6 提交初访预约

```http
POST /api/student/appointments
```

请求：

```json
{
  "formId": 1,
  "dutyScheduleId": 1,
  "appointmentDate": "2026-06-10",
  "slotId": 1,
  "interviewerId": 2,
  "roomId": 1
}
```

响应：

```json
{
  "id": 1,
  "appointmentNo": "FV202606100001",
  "appointmentStatus": "PENDING"
}
```

业务规则：

1. 必须存在已提交首访登记表。
2. 必须已签署知情同意书。
3. 学生不能存在 PENDING 或 APPROVED 的未完成初访预约。
4. 值班必须启用且剩余容量充足。
5. 创建预约状态为 PENDING。
6. 可不立即增加 `reserved_count`，也可提交时占用容量；建议提交时占用，撤销/驳回时释放。

### 6.7 我的预约

```http
GET /api/student/appointments?pageNum=1&pageSize=10&status=PENDING
```

角色：STUDENT。

响应记录：见前端 `MyAppointmentVO`。

### 6.8 撤销预约

```http
POST /api/student/appointments/{id}/cancel
```

请求：

```json
{ "reason": "临时有课，无法参加" }
```

业务规则：

1. PENDING 可撤销。
2. APPROVED 需至少提前一天。
3. CANCELED/COMPLETED/REJECTED 不可撤销。
4. 撤销后释放值班容量。

---

## 7. 管理员：初访预约审核

### 7.1 审核列表

```http
GET /api/admin/first-visit/appointments
```

角色：ADMIN。

参数：`pageNum`、`pageSize`、`keyword`、`status`、`riskLevel`、`startDate`、`endDate`、`priorityFlag`。

排序建议：`URGENT/HIGH` 风险优先，其次 `priority_flag`，再按创建时间。

### 7.2 预约详情

```http
GET /api/admin/first-visit/appointments/{id}
```

角色：ADMIN。

响应包含：

- 学生档案。
- 首访登记表。
- 知情同意状态。
- 预约信息。
- 风险评分。
- 通知日志摘要。

### 7.3 审核通过

```http
POST /api/admin/first-visit/appointments/{id}/approve
```

请求：

```json
{
  "dutyScheduleId": 1,
  "interviewerId": 2,
  "appointmentDate": "2026-06-10",
  "slotId": 1,
  "roomId": 1,
  "auditRemark": "请按时到心理中心三层。"
}
```

业务规则：

1. 只有 PENDING 可通过。
2. 校验值班和容量。
3. 状态改为 APPROVED。
4. 写审核人和审核时间。
5. 写通知日志。

### 7.4 驳回

```http
POST /api/admin/first-visit/appointments/{id}/reject
```

请求：

```json
{ "reason": "所选时间暂不可用，请重新预约。" }
```

业务规则：

1. 只有 PENDING 可驳回。
2. 驳回原因必填。
3. 状态改为 REJECTED。
4. 释放已占用容量。

### 7.5 改约

```http
POST /api/admin/first-visit/appointments/{id}/reschedule
```

请求同通过接口。

业务规则：

1. PENDING/APPROVED 可改约。
2. 释放原时间容量，占用新时间容量。
3. 写通知日志。

### 7.6 标记优先

```http
POST /api/admin/first-visit/appointments/{id}/priority
```

业务规则：设置 `priority_flag=1`。

---

## 8. 初访员：初访任务与结果

### 8.1 我的初访任务

```http
GET /api/interviewer/tasks
```

角色：INTERVIEWER。

参数：`pageNum`、`pageSize`、`startDate`、`endDate`、`status`、`riskLevel`。

记录：

```json
{
  "appointmentId": 1,
  "appointmentNo": "FV202606100001",
  "studentName": "张三",
  "studentNo": "20230001",
  "appointmentDate": "2026-06-10",
  "slotName": "上午第一段",
  "roomName": "心理咨询室A",
  "riskLevel": "MEDIUM",
  "appointmentStatus": "APPROVED"
}
```

### 8.2 任务详情

```http
GET /api/interviewer/tasks/{appointmentId}
```

响应包含学生信息、登记表摘要、预约信息。

### 8.3 提交初访结果

```http
POST /api/interviewer/tasks/{appointmentId}/result
```

请求：

```json
{
  "crisisLevel": "MEDIUM",
  "problemTypeId": 1,
  "interviewTime": "2026-06-10 09:00:00",
  "conclusion": "ARRANGE_CONSULTATION",
  "summary": "学生主要表现为考试压力和睡眠问题。",
  "nextAction": "建议安排正式咨询。"
}
```

业务规则：

1. 只能处理分配给自己的 APPROVED 预约。
2. 同一预约只能提交一条结果，可允许编辑但需谨慎。
3. 提交后预约状态改为 COMPLETED。
4. 结论为 ARRANGE_CONSULTATION 时创建咨询队列。
5. 结论为 NO_NEED/TRANSFER 时不创建队列。

---

## 9. 心理助理：咨询队列与咨询安排

### 9.1 咨询队列

```http
GET /api/assistant/consultation/queue
```

角色：ASSISTANT。

参数：`pageNum`、`pageSize`、`keyword`、`crisisLevel`、`problemTypeId`、`status`。

排序：`priority_score desc, enqueue_time asc`。

### 9.2 队列详情

```http
GET /api/assistant/consultation/queue/{id}
```

响应包含学生信息、初访结果、风险信息、历史安排。

### 9.3 咨询师可用时间

```http
GET /api/assistant/counselors/available-slots?counselorId=3&startDate=2026-06-12
```

角色：ASSISTANT。

说明：返回咨询师值班且无冲突的时间段，也可返回不可用原因。

### 9.4 安排正式咨询

```http
POST /api/assistant/consultation/schedules
```

请求：

```json
{
  "queueId": 1,
  "studentId": 1,
  "counselorId": 3,
  "consultationDate": "2026-06-12",
  "slotId": 2,
  "roomId": 1,
  "remark": "本次正式咨询安排"
}
```

成功响应：

```json
{
  "id": 1,
  "scheduleNo": "CS202606120001"
}
```

冲突响应：`409`

```json
{
  "code": 409,
  "message": "该咨询时间存在冲突",
  "data": {
    "conflicts": [
      { "date": "2026-06-12", "slotId": 2, "reason": "咨询师该时间已有安排" }
    ]
  }
}
```

业务规则：

1. 队列状态必须为 WAITING。
2. 校验咨询师、学生、咨询室时间冲突。
3. 无冲突时插入 `consultation_schedule`。
4. 队列状态改为 ARRANGED。
5. 写通知日志。

### 9.5 咨询安排列表

```http
GET /api/assistant/consultation/schedules
```

参数：`pageNum`、`pageSize`、`studentKeyword`、`counselorId`、`startDate`、`endDate`、`status`。

### 9.6 取消单次咨询安排

```http
POST /api/assistant/consultation/schedules/{id}/cancel
```

请求：

```json
{ "reason": "咨询师临时调整" }
```

业务规则：只有未开始、未完成的安排可取消。

---

## 10. 咨询师：日程、记录、追加、结案

### 10.1 我的咨询日程

```http
GET /api/counselor/schedules
```

角色：COUNSELOR。

参数：`pageNum`、`pageSize`、`startDate`、`endDate`、`status`、`studentKeyword`。

### 10.2 日程详情

```http
GET /api/counselor/schedules/{id}
```

业务规则：只能查看自己的日程。

### 10.3 查询某次咨询记录

```http
GET /api/counselor/schedules/{scheduleId}/record
```

### 10.4 保存咨询记录

```http
POST /api/counselor/schedules/{scheduleId}/record
```

请求：

```json
{
  "recordStatus": "COMPLETED",
  "consultationTime": "2026-06-12 10:00:00",
  "contentSummary": "本次主要围绕考试压力进行讨论。",
  "nextPlan": "下次继续讨论睡眠改善计划。",
  "needClose": 0
}
```

业务规则：

1. 咨询师只能保存自己的日程记录。
2. 保存记录后更新 `consultation_schedule.schedule_status`。
3. `recordStatus=CLOSED` 时可提示填写结案报告。

### 10.5 我的追加申请

```http
GET /api/counselor/extension-requests
POST /api/counselor/extension-requests
```

新增请求：

```json
{
  "studentId": 1,
  "requestSessions": 2,
  "reason": "既有咨询安排完成后仍需继续支持。"
}
```

状态：PENDING、APPROVED、REJECTED。

### 10.6 管理员审核追加申请

```http
GET /api/admin/extension-requests
POST /api/admin/extension-requests/{id}/approve
POST /api/admin/extension-requests/{id}/reject
```

角色：ADMIN。

驳回请求：

```json
{ "reason": "暂不满足追加条件" }
```

### 10.7 咨询师结案报告

```http
GET /api/counselor/case-reports
GET /api/counselor/case-reports/{id}
POST /api/counselor/case-reports
PUT /api/counselor/case-reports/{id}
POST /api/counselor/case-reports/{id}/submit
GET /api/counselor/case-reports/{id}/export-word
```

保存请求：

```json
{
  "studentId": 1,
  "problemTypeId": 1,
  "totalSessions": 8,
  "effectSelfRating": "良好",
  "caseSummary": "学生主要问题为学业压力，经咨询后情绪状态有所改善。",
  "counselingEffect": "能够使用放松训练和时间管理方法。",
  "suggestion": "建议继续保持规律作息。",
  "closeType": "NORMAL",
  "reportStatus": "DRAFT"
}
```

提交业务规则：

1. 必填字段完整。
2. 提交后状态为 SUBMITTED。
3. 管理员可查看和下载。
4. 若结案，后端更新当前个案和相关咨询安排状态。

---

## 11. 管理员：结案报告

```http
GET /api/admin/case-reports
GET /api/admin/case-reports/{id}
GET /api/admin/case-reports/{id}/export-word
GET /api/admin/case-reports/export-batch
```

查询参数：`pageNum`、`pageSize`、`studentKeyword`、`counselorId`、`problemTypeId`、`closeType`、`startDate`、`endDate`。

批量导出可作为增强功能，若时间不足，可只实现单个 Word 下载。

---

## 12. 统计分析

### 12.1 通用查询参数

统计接口均要求 ADMIN 角色，并通过日期范围查询：

| 参数 | 类型 | 必填 | 说明 |
|:--:|:--:|:--:|:--|
| startDate | date | 是 | 开始日期，格式 `yyyy-MM-dd` |
| endDate | date | 是 | 结束日期，格式 `yyyy-MM-dd` |

### 12.2 总览指标

```http
GET /api/admin/statistics/overview?startDate=2026-06-01&endDate=2026-06-30
```

响应：

```json
{
  "totalConsultations": 80,
  "totalStudents": 35,
  "completedReports": 10,
  "activeCounselors": 4
}
```

### 12.3 咨询量趋势

```http
GET /api/admin/statistics/consultation-trend?startDate=2026-01-01&endDate=2026-06-30
```

响应格式：

```json
{
  "xAxis": ["2026-01", "2026-02"],
  "series": [
    { "name": "咨询量", "data": [10, 28] }
  ]
}
```

### 12.4 结案量趋势

```http
GET /api/admin/statistics/completion-trend?startDate=2026-01-01&endDate=2026-06-30
```

响应格式同 `ChartVO`。

### 12.5 新增学生趋势

```http
GET /api/admin/statistics/new-student-trend?startDate=2026-01-01&endDate=2026-06-30
```

响应格式同 `ChartVO`。

### 12.6 咨询分布

```http
GET /api/admin/statistics/consultation-distribution?startDate=2026-06-01&endDate=2026-06-30
```

响应格式：

```json
[
  { "name": "软件学院", "value": 30 },
  { "name": "经济管理学院", "value": 18 }
]
```

### 12.7 问题类型分布

```http
GET /api/admin/statistics/problem-type-distribution?startDate=2026-06-01&endDate=2026-06-30
```

响应格式同饼图数组。

### 12.8 咨询师工作量图表

```http
GET /api/admin/statistics/workload-chart?startDate=2026-06-01&endDate=2026-06-30
```

响应格式：

```json
{
  "xAxis": ["李老师", "王老师"],
  "series": [
    { "name": "咨询量", "data": [25, 20] },
    { "name": "结案量", "data": [8, 6] }
  ]
}
```

### 12.9 咨询师工作量表格

```http
GET /api/admin/statistics/workload-table?startDate=2026-06-01&endDate=2026-06-30
```

响应：

```json
[
  {
    "counselorId": 1,
    "counselorName": "李老师",
    "consultationCount": 25,
    "studentCount": 16,
    "reportCount": 8
  }
]
```

### 12.10 统计导出

当前后端未暴露统计 Excel 导出接口；如需作为增强功能，需另行补充接口和实现。

---

## 13. 日志接口

### 13.1 通知日志

```http
GET /api/admin/logs/notifications
```

角色：ADMIN。

参数：`pageNum`、`pageSize`、`keyword`、`notifyType`、`sendStatus`、`startTime`、`endTime`。

### 13.2 操作日志

```http
GET /api/admin/logs/operations
```

角色：ADMIN。

参数：`pageNum`、`pageSize`、`keyword`、`operationType`、`resultStatus`、`startTime`、`endTime`。

---

## 14. 接口实现优先级

### P0：必须完成，支撑主流程

1. 登录、退出、当前用户。
2. 学生首访登记、知情同意、初访预约、我的预约。
3. 管理员时间段、值班、预约审核。
4. 初访员任务和初访结果。
5. 心理助理咨询队列和正式咨询安排。
6. 咨询师日程、咨询记录、结案报告。

### P1：高分增强

1. 风险预警排序。
2. 咨询安排冲突检测。
3. Word 导出。
4. ECharts 统计接口。
5. 通知日志和操作日志。

### P2：时间充足再做

1. 批量排班。
2. 批量下载结案报告。
3. Excel 导出。
4. 追加咨询申请完整审核流。
