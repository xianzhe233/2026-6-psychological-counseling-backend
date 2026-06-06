# 后端业务规则与状态流转

> 本文档列出必须由后端保证的业务规则。前端可以做提示和预校验，但最终判断必须以后端为准。

---

## 1. 角色权限规则

| 角色 | 可访问数据 | 禁止行为 |
|:--:|:--|:--|
| STUDENT | 自己的登记表、同意书、预约、通知 | 查看他人预约、审核、咨询记录、报告 |
| ADMIN | 基础信息、预约审核、统计、报告、日志 | 直接篡改咨询师私密记录内容（除管理查询） |
| INTERVIEWER | 分配给自己的初访任务和结果 | 查看非本人初访任务 |
| ASSISTANT | 咨询队列、咨询安排、咨询师可用时间 | 修改咨询师咨询记录内容 |
| COUNSELOR | 自己的咨询安排、记录、报告、追加申请 | 查看其他咨询师负责的个案 |

实现要求：

- Controller 或 Service 必须校验角色。
- Service 必须校验数据归属。
- 不能只依靠前端菜单隐藏来控制权限。

---

## 2. 首访登记与风险评分

### 2.1 输入字段

| 字段 | 范围 | 含义 |
|:--:|:--:|:--|
| moodScore | 0-10 | 情绪困扰程度，越高越严重 |
| sleepScore | 0-10 | 睡眠困扰程度，越高越严重 |
| stressScore | 0-10 | 压力程度，越高越严重 |
| selfHarmFlag | 0/1 | 是否存在自伤或伤人想法 |
| emergencyFlag | 0/1 | 是否需要紧急帮助 |

### 2.2 评分算法

```text
riskScore = moodScore + sleepScore + stressScore
if selfHarmFlag == 1: riskScore += 50
if emergencyFlag == 1: riskScore += 30
```

等级：

```text
if emergencyFlag == 1 or riskScore >= 70: URGENT
else if selfHarmFlag == 1 or riskScore >= 40: HIGH
else if riskScore >= 20: MEDIUM
else LOW
```

实现类：`RiskAssessmentService`。

### 2.3 规则

1. 风险分数和等级只能由后端计算。
2. 学生端不展示刺激性“高危”字样，只做温和提醒。
3. 管理端审核列表应按风险等级和优先级排序。
4. HIGH/URGENT 进入咨询队列后优先级更高。

---

## 3. 知情同意规则

1. 学生必须先提交首访登记表。
2. 学生必须签署知情同意书后才能提交初访预约。
3. 同一 `form_id` 只能签署一次。
4. 签署记录包含版本号、签署时间、IP。

---

## 4. 初访预约规则

### 4.1 提交预约

提交初访预约必须满足：

1. 当前用户角色为 STUDENT。
2. 学生档案存在。
3. `form_id` 属于当前学生且状态为 SUBMITTED。
4. `form_id` 已签署知情同意书。
5. 学生没有 PENDING 或 APPROVED 的初访预约。
6. 值班安排存在、启用、类型为 INTERVIEWER。
7. 值班容量未满。

状态：提交后为 PENDING。

### 4.2 预约编号

建议格式：

```text
FV + yyyyMMdd + 4位序号
示例：FV202606100001
```

### 4.3 容量处理

建议在提交预约时即占用 `duty_schedule.reserved_count`，原因是防止大量待审核预约超过容量。

释放容量场景：

- 学生撤销。
- 管理员驳回。
- 管理员改约时释放旧时段、占用新时段。

---

## 5. 初访预约状态流转

```text
PENDING ──审核通过──> APPROVED ──初访完成──> COMPLETED
   │                    │
   ├──驳回────────────> REJECTED
   │                    │
   └──学生撤销────────> CANCELED

APPROVED ──提前一天撤销──> CANCELED
```

规则：

1. 只有 PENDING 可审核通过或驳回。
2. PENDING 可由学生撤销。
3. APPROVED 可由学生撤销，但必须至少提前一天。
4. COMPLETED、REJECTED、CANCELED 是终态，不允许再审核。
5. 管理员改约允许 PENDING 或 APPROVED，但必须记录操作日志。

---

## 6. 管理员审核规则

### 6.1 审核通过

必须校验：

1. 当前用户为 ADMIN。
2. 预约状态为 PENDING。
3. 新值班安排存在且可用。
4. 容量足够。

动作：

1. 更新 `appointment_status=APPROVED`。
2. 写入审核人、审核时间、备注。
3. 写入通知日志。
4. 写入操作日志。

### 6.2 驳回

必须校验：

1. 当前用户为 ADMIN。
2. 状态为 PENDING。
3. 驳回原因必填。

动作：

1. 状态改为 REJECTED。
2. 释放容量。
3. 写操作日志。

### 6.3 优先排队

动作：`priority_flag=1`。

优先标记会影响：

1. 管理端审核列表排序。
2. 初访结果进入咨询队列后的优先级分数。

---

## 7. 初访结果规则

初访员提交结果必须满足：

1. 当前角色为 INTERVIEWER。
2. 预约分配给当前初访员。
3. 预约状态为 APPROVED。
4. 危机等级、问题类型、初访时间、结论必填。

提交后：

1. 插入 `first_visit_result`。
2. 预约状态改为 COMPLETED。
3. 若结论为 ARRANGE_CONSULTATION，创建 `consultation_queue`。
4. 若结论为 NO_NEED 或 TRANSFER，不创建队列。

结论为 TRANSFER 时，`next_action` 建议必填。

---

## 8. 咨询队列规则

### 8.1 入队

入队来源：初访结论为 ARRANGE_CONSULTATION。

队列字段：

- `student_id`
- `first_visit_result_id`
- `problem_type_id`
- `crisis_level`
- `priority_score`
- `queue_status=WAITING`
- `enqueue_time`

### 8.2 优先级分数

建议：

```text
URGENT: 1000
HIGH: 800
MEDIUM: 500
LOW: 100
若 priority_flag=1，额外 +100
```

排序：

```sql
ORDER BY priority_score DESC, enqueue_time ASC
```

### 8.3 队列状态

```text
WAITING ──安排咨询──> ARRANGED
WAITING ──暂缓──────> SUSPENDED
WAITING/ARRANGED ──关闭──> CLOSED
```

---

## 9. 正式咨询安排规则

### 9.1 生成规则

心理助理选择咨询日期、咨询师、时间段和咨询室后，系统生成本次正式咨询安排。

```text
本次咨询：consultationDate + slotId + counselorId + roomId
```

如后续仍需继续咨询，由心理助理按实际情况新增后续咨询安排。

### 9.2 冲突校验

每次新增咨询安排都必须校验：

1. 咨询师冲突：同一 counselor_id + date + slot_id 无非 CANCELED 安排。
2. 学生冲突：同一 student_id + date + slot_id 无非 CANCELED 安排。
3. 咨询室冲突：同一 room_id + date + slot_id 无非 CANCELED 安排。
4. 咨询师值班：咨询师在该 date + slot_id 有启用值班。

### 9.3 事务规则

安排咨询必须先完成冲突校验，再插入咨询安排记录。若存在冲突，应返回 409 和明确原因。

成功后：

1. 插入多条 `consultation_schedule`。
2. 队列状态改为 ARRANGED。
3. 写通知日志。
4. 写操作日志。

---

## 10. 咨询安排状态流转

```text
RESERVED ──咨询师记录完成──> COMPLETED
RESERVED ──记录旷约──────> ABSENT
RESERVED ──记录请假──────> LEAVE
RESERVED ──记录脱落──────> DROPPED
RESERVED ──结案──────────> CLOSED
RESERVED ──助理取消──────> CANCELED
```

规则：

1. CANCELED 不参与冲突判断。
2. COMPLETED、ABSENT、DROPPED、CLOSED 视为已处理，不建议随意修改。
3. 如需修改，必须记录操作日志。

---

## 11. 咨询记录规则

咨询师保存咨询记录必须满足：

1. 当前角色为 COUNSELOR。
2. 该 schedule 属于当前咨询师。
3. schedule 状态不是 CANCELED。
4. 记录状态必填。

保存后：

1. 插入或更新 `consultation_record`。
2. 同步更新 `consultation_schedule.schedule_status`。
3. 若记录状态为 CLOSED 或 `need_close=1`，提示填写结案报告。

---

## 12. 追加咨询申请规则

1. 只有咨询师可提交追加咨询申请。
2. 追加次数必须大于 0。
3. 申请原因必填。
4. 初始状态 PENDING。
5. 管理员可 APPROVED 或 REJECTED。
6. 审核通过后，由心理助理继续安排后续咨询。

---

## 13. 结案报告规则

### 13.1 保存草稿

咨询师可保存草稿，状态 DRAFT。

必填建议：学生、问题类型、咨询总次数、结案类型。

### 13.2 提交报告

提交必须满足：

1. 学生属于当前咨询师负责过的个案。
2. 问题类型必填。
3. 咨询总次数大于 0。
4. 咨询效果自评必填。
5. 结案类型必填。

提交后：`report_status=SUBMITTED`。

### 13.3 导出报告

管理员可以导出已提交报告。咨询师可以导出自己负责的报告。

导出字段：

- 学号
- 姓名
- 性别
- 院系
- 联系电话
- 问题类型
- 咨询总次数
- 咨询效果自评
- 个案总结
- 后续建议

---

## 14. 通知日志规则

模拟通知场景：

| 场景 | notify_type | 接收人 |
|:--:|:--:|:--|
| 初访预约审核通过 | APPOINTMENT_APPROVED | 学生 |
| 初访预约改约 | APPOINTMENT_RESCHEDULED | 学生 |
| 正式咨询安排成功 | CONSULTATION_ARRANGED | 学生 |
| 咨询安排取消 | CONSULTATION_CANCELED | 学生 |
| 追加咨询审核结果 | EXTENSION_AUDITED | 咨询师 |

通知日志不保证真实短信发送，只表示系统已生成通知。

---

## 15. 操作日志规则

必须记录的操作：

1. 管理员新增/修改/禁用用户。
2. 管理员新增/修改值班。
3. 管理员审核、驳回、改约预约。
4. 初访员提交初访结果。
5. 心理助理安排或取消咨询。
6. 咨询师保存咨询记录。
7. 咨询师提交结案报告。
8. 管理员导出结案报告。

日志至少包含：操作人、角色、模块、操作类型、描述、结果、时间。

---

## 16. 隐私与展示规则

1. 学生心理登记、初访摘要、咨询记录和结案报告属于敏感信息。
2. 列表页只显示摘要，不展示完整隐私内容。
3. 非授权角色不得访问敏感详情。
4. 操作日志中请求参数需要脱敏，例如密码、手机号可部分隐藏。
5. 导出的结案报告仅管理员和负责咨询师可下载。
