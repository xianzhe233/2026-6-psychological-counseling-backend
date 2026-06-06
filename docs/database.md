# 后端数据库落地设计

> 数据库：MySQL 8.0  
> 字符集：utf8mb4  
> 引擎：InnoDB  
> 数据库名建议：`psychological_counseling`

---

## 1. 建库脚本

```sql
CREATE DATABASE IF NOT EXISTS psychological_counseling
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE psychological_counseling;
```

---

## 2. 命名与字段约定

1. 表名使用小写英文 + 下划线。
2. 主键统一为 `id bigint primary key auto_increment`。
3. 时间字段统一使用 `datetime`。
4. 状态字段使用英文编码字符串或 tinyint。
5. 逻辑删除字段使用 `is_deleted tinyint default 0`。
6. 数据库字段使用下划线，Java 实体使用驼峰，MyBatis 开启 `map-underscore-to-camel-case`。
7. 业务状态统一在 Java enum 中维护，数据库保存 enum name。

---

## 3. 核心枚举值

### 3.1 角色 RoleCode

| 值 | 说明 |
|:--:|:--|
| STUDENT | 学生 |
| ADMIN | 中心管理员 |
| INTERVIEWER | 初访员 |
| ASSISTANT | 心理助理 |
| COUNSELOR | 咨询师 |

### 3.2 风险等级 RiskLevel

| 值 | 说明 |
|:--:|:--|
| LOW | 低风险 |
| MEDIUM | 中风险 |
| HIGH | 高风险 |
| URGENT | 紧急风险 |

### 3.3 初访预约状态 AppointmentStatus

| 值 | 说明 |
|:--:|:--|
| PENDING | 待审核 |
| APPROVED | 已通过 |
| REJECTED | 已驳回 |
| CANCELED | 已撤销 |
| COMPLETED | 已完成初访 |

### 3.4 初访结论 FirstVisitConclusion

| 值 | 说明 |
|:--:|:--|
| NO_NEED | 无需咨询 |
| ARRANGE_CONSULTATION | 安排咨询 |
| TRANSFER | 转介送诊 |

### 3.5 咨询队列状态 QueueStatus

| 值 | 说明 |
|:--:|:--|
| WAITING | 等待安排 |
| ARRANGED | 已安排 |
| SUSPENDED | 暂缓 |
| CLOSED | 已关闭 |

### 3.6 咨询安排状态 ScheduleStatus

| 值 | 说明 |
|:--:|:--|
| RESERVED | 已预约 |
| COMPLETED | 完成咨询 |
| ABSENT | 旷约 |
| LEAVE | 请假 |
| DROPPED | 脱落 |
| CLOSED | 结案 |
| CANCELED | 已取消 |

### 3.7 报告状态 ReportStatus

| 值 | 说明 |
|:--:|:--|
| DRAFT | 草稿 |
| SUBMITTED | 已提交 |

---

## 4. 表结构实现清单

> 完整字段见交付文档 `project/delivery-docs/过程性材料/数据库设计说明书.md`。本文件面向后端实现，重点列出实体、关键字段、索引和实现注意事项。

### 4.1 sys_user 系统用户表

用途：登录账号和基础身份信息。

关键字段：

| 字段 | 类型 | 说明 |
|:--|:--|:--|
| id | bigint | 主键 |
| username | varchar(50) | 登录名，唯一 |
| password_hash | varchar(100) | 密码摘要 |
| real_name | varchar(50) | 真实姓名 |
| phone | varchar(20) | 手机号 |
| email | varchar(100) | 邮箱 |
| avatar_url | varchar(500) | 头像 |
| status | tinyint | 0禁用，1正常 |
| last_login_time | datetime | 最后登录时间 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| is_deleted | tinyint | 逻辑删除 |

索引：

```sql
UNIQUE KEY uk_sys_user_username (username),
KEY idx_sys_user_phone (phone)
```

实现注意：

- VO 不能返回 `password_hash`。
- 删除用户建议逻辑删除或禁用。

### 4.2 sys_role / sys_user_role

用途：角色和用户角色关联。

`sys_role.role_code` 唯一，初始数据必须包含五类角色。

初始 SQL 示例：

```sql
INSERT INTO sys_role(role_code, role_name, description, create_time)
VALUES
('STUDENT','学生','学生用户',NOW()),
('ADMIN','中心管理员','中心管理员',NOW()),
('INTERVIEWER','初访员','初访员',NOW()),
('ASSISTANT','心理助理','心理助理',NOW()),
('COUNSELOR','咨询师','咨询师',NOW());
```

### 4.3 student_profile 学生档案表

用途：学生扩展信息。

关键字段：`user_id`、`student_no`、`gender`、`college`、`major`、`grade`、`class_name`、`contact_phone`、`emergency_contact`、`emergency_phone`。

索引：

```sql
UNIQUE KEY uk_student_profile_user (user_id),
UNIQUE KEY uk_student_profile_no (student_no)
```

实现注意：

- 学生账号新增时可同步生成学生档案。
- 若没有完整档案，首访登记前应要求补全必要字段。

### 4.4 staff_profile 工作人员档案表

用途：管理员、初访员、心理助理、咨询师扩展信息。

关键字段：`user_id`、`staff_no`、`staff_type`、`title`、`specialty`、`introduction`、`max_daily_appointments`、`status`。

索引：

```sql
UNIQUE KEY uk_staff_profile_user (user_id),
KEY idx_staff_profile_type (staff_type)
```

实现注意：

- `staff_type=INTERVIEWER` 才能排初访值班。
- `staff_type=COUNSELOR` 才能排正式咨询值班。
- `staff_type=ASSISTANT` 负责安排咨询，但一般不参与值班。

### 4.5 counseling_room 咨询室表

用途：咨询地点。

关键字段：`room_name`、`location`、`capacity`、`status`。

实现注意：咨询安排冲突检测需要按 `room_id + date + slot_id` 查询。

### 4.6 time_slot 基础时间段表

用途：统一维护可预约时间段。

关键字段：`slot_name`、`start_time`、`end_time`、`interval_minutes`、`status`。

实现注意：

- 校验 `end_time > start_time`。
- 不建议物理删除已被预约使用的时间段，只停用。

### 4.7 duty_schedule 值班安排表

用途：初访员/咨询师可预约时间。

关键字段：

| 字段 | 说明 |
|:--|:--|
| staff_id | 工作人员ID |
| staff_type | INTERVIEWER 或 COUNSELOR |
| duty_date | 值班日期 |
| slot_id | 时间段 |
| room_id | 咨询室 |
| capacity | 可预约容量 |
| reserved_count | 已预约数量 |
| status | 是否启用 |

索引：

```sql
UNIQUE KEY uk_duty_staff_date_slot (staff_id, duty_date, slot_id),
KEY idx_duty_date_type (duty_date, staff_type),
KEY idx_duty_room_date_slot (room_id, duty_date, slot_id)
```

实现注意：

- 新增/修改值班必须校验工作人员类型。
- `reserved_count` 修改需要事务保护。
- 若值班已被预约，不允许把 `capacity` 改到小于 `reserved_count`。

### 4.8 problem_type 问题类型表

用途：问题分类字典。

建议初始数据：

- 学业压力
- 人际关系
- 情绪困扰
- 恋爱问题
- 家庭关系
- 职业规划
- 适应问题
- 其他

### 4.9 first_visit_form 首访登记表

用途：学生预约前填写的信息和风险评分。

关键字段：

- `student_id`
- `main_problem`
- `problem_description`
- `expected_help`
- `mood_score`
- `sleep_score`
- `stress_score`
- `self_harm_flag`
- `emergency_flag`
- `risk_score`
- `risk_level`
- `form_status`
- `submit_time`

索引：

```sql
KEY idx_first_visit_form_student (student_id),
KEY idx_first_visit_form_risk (risk_level)
```

实现注意：

- 风险评分由后端计算，前端不能传 `risk_score` 和 `risk_level` 作为可信值。
- 学生可多次填写，但预约时应绑定本次使用的 `form_id`。

### 4.10 consent_record 知情同意记录表

用途：记录学生是否签署知情同意。

索引：

```sql
UNIQUE KEY uk_consent_form (form_id)
```

实现注意：同一登记表只允许签署一次。

### 4.11 first_visit_appointment 初访预约表

用途：学生初访预约申请和审核结果。

关键字段：

- `appointment_no`
- `student_id`
- `form_id`
- `interviewer_id`
- `duty_schedule_id`
- `appointment_date`
- `slot_id`
- `room_id`
- `appointment_status`
- `priority_flag`
- `audit_admin_id`
- `audit_time`
- `audit_remark`
- `cancel_reason`

索引：

```sql
UNIQUE KEY uk_first_visit_appointment_no (appointment_no),
KEY idx_fv_appointment_student_status (student_id, appointment_status),
KEY idx_fv_appointment_status_risk (appointment_status, priority_flag),
KEY idx_fv_appointment_date_slot (appointment_date, slot_id)
```

实现注意：

- 预约编号可生成：`FV + yyyyMMdd + 4位序号`。
- 学生不能存在多个 PENDING/APPROVED 预约。
- 审核通过、驳回、撤销均需要维护容量。

### 4.12 first_visit_result 初访结果表

用途：初访员录入线下初访结果。

关键字段：`appointment_id`、`interviewer_id`、`crisis_level`、`problem_type_id`、`interview_time`、`conclusion`、`summary`、`next_action`。

索引：

```sql
UNIQUE KEY uk_first_visit_result_appointment (appointment_id),
KEY idx_first_visit_result_crisis (crisis_level),
KEY idx_first_visit_result_problem (problem_type_id)
```

实现注意：

- 结论为 `ARRANGE_CONSULTATION` 时创建咨询队列。
- 结论为 `TRANSFER` 时 `next_action` 建议必填。

### 4.13 consultation_queue 咨询排队表

用途：正式咨询等待队列。

关键字段：`student_id`、`first_visit_result_id`、`problem_type_id`、`crisis_level`、`priority_score`、`queue_status`、`enqueue_time`、`assigned_time`。

索引：

```sql
KEY idx_consultation_queue_status (queue_status),
KEY idx_consultation_queue_priority (priority_score, enqueue_time)
```

优先级建议：

```text
URGENT = 1000
HIGH = 800
MEDIUM = 500
LOW = 100
priority_flag 可额外 +100
```

### 4.14 consultation_schedule 正式咨询安排表

用途：正式咨询排期。

关键字段：`schedule_no`、`queue_id`、`student_id`、`counselor_id`、`assistant_id`、`consultation_date`、`slot_id`、`room_id`、`session_no`、`source_type`、`schedule_status`。

索引：

```sql
UNIQUE KEY uk_consultation_schedule_no (schedule_no),
KEY idx_schedule_student (student_id, consultation_date),
KEY idx_schedule_counselor_time (counselor_id, consultation_date, slot_id),
KEY idx_schedule_room_time (room_id, consultation_date, slot_id),
KEY idx_schedule_status (schedule_status)
```

实现注意：

- 不建议对 canceled 状态做物理删除。
- 时间冲突查询应排除 CANCELED 状态。
- 创建咨询安排应在事务内完成，并在写入前完成冲突检测。

### 4.15 consultation_record 咨询记录表

用途：咨询师维护每次咨询结果。

关键字段：`schedule_id`、`student_id`、`counselor_id`、`session_no`、`consultation_time`、`record_status`、`content_summary`、`next_plan`、`need_close`。

索引：

```sql
UNIQUE KEY uk_consultation_record_schedule (schedule_id),
KEY idx_consultation_record_student (student_id),
KEY idx_consultation_record_counselor (counselor_id)
```

实现注意：

- 保存记录后同步更新咨询安排状态。
- 列表页只显示摘要，不显示完整隐私内容。

### 4.16 extension_request 追加咨询申请表

用途：咨询师申请追加咨询次数。

关键字段：`student_id`、`counselor_id`、`request_sessions`、`reason`、`request_status`、`audit_admin_id`、`audit_time`、`audit_remark`。

实现注意：

- 初始可只实现咨询师提交和管理员审核。
- 审核通过后由助理继续安排咨询。

### 4.17 case_report 结案报告表

用途：咨询师填写结案报告，并供管理员下载。

关键字段：`student_id`、`counselor_id`、`problem_type_id`、`total_sessions`、`effect_self_rating`、`case_summary`、`counseling_effect`、`suggestion`、`close_type`、`report_status`、`report_file_path`、`submit_time`。

索引：

```sql
KEY idx_case_report_student (student_id),
KEY idx_case_report_counselor (counselor_id),
KEY idx_case_report_problem (problem_type_id),
KEY idx_case_report_status (report_status)
```

实现注意：

- 报告保存草稿和提交分开。
- 提交后管理员可查看。
- Word 文件可实时生成，也可生成后保存路径。

### 4.18 notification_log 通知日志表

用途：模拟短信通知。

关键字段：`receiver_user_id`、`receiver_name`、`phone`、`notify_type`、`title`、`content`、`send_status`、`send_time`、`related_id`。

实现注意：

- 不接真实短信平台。
- 业务节点调用 `NotificationLogService.mockSend()`。

### 4.19 operation_log 操作日志表

用途：记录关键操作。

关键字段：`operator_user_id`、`operator_name`、`role_code`、`module_name`、`operation_type`、`operation_desc`、`request_url`、`result_status`、`error_message`、`ip_address`、`execution_time`、`create_time`。

实现注意：

- 初期可手动调用记录。
- 若时间充足，可用 AOP 自动记录。

### 4.20 system_config 系统配置表

用途：保存系统配置，例如默认密码、同意书版本、风险评分阈值等。

建议配置：

| config_key | config_value | 说明 |
|:--|:--|:--|
| default.password | 123456 | 默认密码 |
| consent.version | v1.0 | 知情同意书版本 |
| risk.medium.threshold | 20 | 中风险阈值 |
| risk.high.threshold | 40 | 高风险阈值 |
| risk.urgent.threshold | 70 | 紧急风险阈值 |

---

## 5. 初始数据要求

`init_data.sql` 至少包含：

1. 五类角色。
2. 一个管理员账号。
3. 一个学生账号。
4. 一个初访员账号。
5. 一个心理助理账号。
6. 一个咨询师账号。
7. 3-5 个基础时间段。
8. 2 个咨询室。
9. 常见问题类型。

建议演示账号：

| 角色 | 用户名 | 密码 |
|:--:|:--:|:--:|
| 管理员 | admin | 123456 |
| 学生 | 20230001 | 123456 |
| 初访员 | interviewer | 123456 |
| 心理助理 | assistant | 123456 |
| 咨询师 | counselor | 123456 |

---

## 6. 演示数据要求

`demo_data.sql` 建议包含：

1. 待审核初访预约 3 条，其中 HIGH 或 URGENT 至少 1 条。
2. 已通过预约 2 条。
3. 初访结果 2 条，其中一条进入咨询队列。
4. 咨询队列 WAITING 数据 3 条。
5. 咨询安排若干条，用于展示正常安排、取消和冲突检测。
6. 咨询记录若干。
7. 结案报告 1-2 条。
8. 通知日志和操作日志若干。

这样前端可完整展示审核、风险预警、排队、咨询安排、结案报告和统计看板。

---

## 7. 建表脚本落地顺序

推荐 `init_schema.sql` 中按以下顺序建表：

1. `sys_user`
2. `sys_role`
3. `sys_user_role`
4. `student_profile`
5. `staff_profile`
6. `counseling_room`
7. `time_slot`
8. `duty_schedule`
9. `problem_type`
10. `first_visit_form`
11. `consent_record`
12. `first_visit_appointment`
13. `first_visit_result`
14. `consultation_queue`
15. `consultation_schedule`
16. `consultation_record`
17. `extension_request`
18. `case_report`
19. `notification_log`
20. `operation_log`
21. `system_config`

如果不建立数据库外键，则建表顺序影响较小；但仍建议按业务依赖顺序组织，便于阅读。
