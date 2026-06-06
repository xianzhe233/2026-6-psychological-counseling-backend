# 后端架构设计文档

> 项目：高校心理咨询预约与个案管理系统后端  
> 状态：开发前详细设计稿  
> 技术栈：Java + Spring Boot + Spring MVC + MyBatis + MySQL + Session  
> 目标：本文件用于指导后端工程落地，粒度应达到可直接建包、建类、写接口、写 SQL、联调前端的程度。

---

## 1. 后端职责

后端负责系统的业务规则、数据持久化、权限控制、接口服务和文件导出。核心职责包括：

1. 用户登录、退出、Session 会话维护。
2. 多角色权限控制：学生、中心管理员、初访员、心理助理、咨询师。
3. 用户、学生档案、工作人员档案、咨询室、时间段、值班安排等基础信息维护。
4. 首访登记、风险评分、知情同意、初访预约提交。
5. 初访预约审核、改约、驳回、优先排队和模拟短信通知。
6. 初访结果录入，并根据结论进入咨询队列或结束流程。
7. 咨询队列管理、正式咨询安排、按需追加、时间冲突检测。
8. 咨询记录、追加咨询申请、结案报告填写与 Word 导出。
9. 统计分析接口，为前端 ECharts 提供数据。
10. 通知日志、操作日志、异常处理、统一返回。

后端不负责前端页面渲染，不直接处理真实短信平台，不做真实在线音视频咨询。

---

## 2. 技术版本建议

| 分类 | 技术 | 建议版本 | 说明 |
|:--:|:--|:--|:--|
| JDK | Java | 17 | 推荐配合 Spring Boot 3；若课程环境限制，可整体降级到 JDK 8 + Spring Boot 2.7 |
| 框架 | Spring Boot | 3.2.x | 后端应用基础框架 |
| Web | Spring MVC | 随 Spring Boot | REST API |
| 持久层 | MyBatis Spring Boot Starter | 3.x | Mapper 与 XML SQL |
| 数据库 | MySQL | 8.0 | 业务数据持久化 |
| 驱动 | mysql-connector-j | 8.x | MySQL JDBC 驱动 |
| 构建 | Maven | 3.8+ | 依赖管理和打包 |
| 文档 | Markdown | - | docs 维护 |
| 导出 | Apache POI | 5.x | Word/Excel 文件导出 |

默认推荐 Java 17 + Spring Boot 3.2.x。若小组成员本地环境只有 JDK 8，应在开发前统一决定降级方案，避免版本不兼容。

---

## 3. 推荐项目结构

```text
backend/
├── docs/
│   ├── architecture.md          # 后端架构设计，本文件
│   ├── api.md                   # 接口设计
│   ├── progress.md              # 进度记录
│   ├── database.md              # 数据库落地设计
│   ├── modules.md               # 模块、类、方法设计
│   ├── business-rules.md        # 业务规则和状态流转
│   └── implementation-plan.md   # 后端实施计划
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/tyut/psychological/
│   │   │       ├── PsychologicalApplication.java
│   │   │       ├── common/
│   │   │       │   ├── api/
│   │   │       │   │   ├── Result.java
│   │   │       │   │   ├── PageResult.java
│   │   │       │   │   └── PageQuery.java
│   │   │       │   ├── exception/
│   │   │       │   │   ├── BusinessException.java
│   │   │       │   │   └── GlobalExceptionHandler.java
│   │   │       │   ├── enums/
│   │   │       │   │   ├── RoleCode.java
│   │   │       │   │   ├── RiskLevel.java
│   │   │       │   │   ├── AppointmentStatus.java
│   │   │       │   │   ├── FirstVisitConclusion.java
│   │   │       │   │   ├── QueueStatus.java
│   │   │       │   │   ├── ScheduleStatus.java
│   │   │       │   │   └── ReportStatus.java
│   │   │       │   └── util/
│   │   │       │       ├── DateTimeUtils.java
│   │   │       │       ├── PasswordUtils.java
│   │   │       │       ├── SessionUtils.java
│   │   │       │       └── FileDownloadUtils.java
│   │   │       ├── config/
│   │   │       │   ├── WebMvcConfig.java
│   │   │       │   ├── LoginInterceptor.java
│   │   │       │   └── CorsConfig.java
│   │   │       ├── auth/
│   │   │       │   ├── controller/AuthController.java
│   │   │       │   ├── service/AuthService.java
│   │   │       │   ├── dto/LoginRequest.java
│   │   │       │   └── vo/CurrentUserVO.java
│   │   │       ├── user/
│   │   │       │   ├── controller/UserController.java
│   │   │       │   ├── service/UserService.java
│   │   │       │   ├── mapper/UserMapper.java
│   │   │       │   ├── entity/SysUser.java
│   │   │       │   ├── entity/SysRole.java
│   │   │       │   ├── entity/SysUserRole.java
│   │   │       │   ├── dto/UserQuery.java
│   │   │       │   ├── dto/UserSaveRequest.java
│   │   │       │   └── vo/UserVO.java
│   │   │       ├── profile/
│   │   │       │   ├── controller/StaffController.java
│   │   │       │   ├── service/StudentProfileService.java
│   │   │       │   ├── service/StaffProfileService.java
│   │   │       │   ├── mapper/StudentProfileMapper.java
│   │   │       │   ├── mapper/StaffProfileMapper.java
│   │   │       │   ├── entity/StudentProfile.java
│   │   │       │   └── entity/StaffProfile.java
│   │   │       ├── schedule/
│   │   │       │   ├── controller/TimeSlotController.java
│   │   │       │   ├── controller/DutyScheduleController.java
│   │   │       │   ├── controller/RoomController.java
│   │   │       │   ├── service/TimeSlotService.java
│   │   │       │   ├── service/DutyScheduleService.java
│   │   │       │   ├── service/RoomService.java
│   │   │       │   ├── mapper/TimeSlotMapper.java
│   │   │       │   ├── mapper/DutyScheduleMapper.java
│   │   │       │   ├── mapper/CounselingRoomMapper.java
│   │   │       │   └── entity/*.java
│   │   │       ├── firstvisit/
│   │   │       │   ├── controller/StudentFirstVisitController.java
│   │   │       │   ├── controller/AdminFirstVisitController.java
│   │   │       │   ├── controller/InterviewerTaskController.java
│   │   │       │   ├── service/FirstVisitFormService.java
│   │   │       │   ├── service/ConsentService.java
│   │   │       │   ├── service/FirstVisitAppointmentService.java
│   │   │       │   ├── service/FirstVisitResultService.java
│   │   │       │   ├── service/RiskAssessmentService.java
│   │   │       │   ├── mapper/*.java
│   │   │       │   ├── entity/*.java
│   │   │       │   ├── dto/*.java
│   │   │       │   └── vo/*.java
│   │   │       ├── consultation/
│   │   │       │   ├── controller/AssistantConsultationController.java
│   │   │       │   ├── controller/CounselorScheduleController.java
│   │   │       │   ├── service/ConsultationQueueService.java
│   │   │       │   ├── service/ConsultationScheduleService.java
│   │   │       │   ├── service/ConsultationRecordService.java
│   │   │       │   ├── service/ExtensionRequestService.java
│   │   │       │   ├── mapper/*.java
│   │   │       │   └── entity/*.java
│   │   │       ├── report/
│   │   │       │   ├── controller/CaseReportController.java
│   │   │       │   ├── service/CaseReportService.java
│   │   │       │   ├── service/CaseReportExportService.java
│   │   │       │   ├── mapper/CaseReportMapper.java
│   │   │       │   └── entity/CaseReport.java
│   │   │       ├── statistics/
│   │   │       │   ├── controller/StatisticsController.java
│   │   │       │   ├── service/StatisticsService.java
│   │   │       │   ├── mapper/StatisticsMapper.java
│   │   │       │   └── vo/*.java
│   │   │       └── log/
│   │   │           ├── controller/LogController.java
│   │   │           ├── service/NotificationLogService.java
│   │   │           ├── service/OperationLogService.java
│   │   │           ├── mapper/*.java
│   │   │           └── entity/*.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── mapper/
│   │       │   ├── user/
│   │       │   ├── schedule/
│   │       │   ├── firstvisit/
│   │       │   ├── consultation/
│   │       │   ├── report/
│   │       │   └── statistics/
│   │       └── templates/
│   │           └── case-report-template.docx
│   └── test/
│       └── java/...
├── sql/
│   ├── init_schema.sql
│   ├── init_data.sql
│   └── demo_data.sql
├── pom.xml
└── README.md
```

---

## 4. 分层职责

### 4.1 Controller 层

职责：

1. 接收 HTTP 请求。
2. 使用 DTO 接收参数。
3. 做基础参数校验。
4. 调用 Service。
5. 返回统一 `Result<T>`。

禁止：

- 不在 Controller 中写 SQL。
- 不在 Controller 中写复杂业务规则。
- 不直接操作 Session 以外的底层 Servlet 对象，除下载接口外。

### 4.2 Service 层

职责：

1. 实现业务规则。
2. 控制事务。
3. 校验角色和数据归属。
4. 调用 Mapper。
5. 调用通知日志、操作日志、导出服务等公共服务。

需要加事务的场景：

- 创建初访预约并更新值班已预约数。
- 审核通过预约并写通知日志。
- 初访结果为安排咨询时创建咨询队列。
- 创建正式咨询安排。
- 保存咨询记录并更新咨询安排状态。
- 提交结案报告并更新个案状态。

### 4.3 Mapper 层

职责：

1. 单表增删改查。
2. 分页查询。
3. 多表列表视图查询。
4. 统计聚合查询。

建议复杂列表和统计写 XML SQL，简单单表操作可用注解或 XML。

### 4.4 Entity / DTO / VO

- Entity：与数据库表字段对应。
- DTO：前端请求对象。
- VO：后端响应给前端的视图对象。

禁止直接把 Entity 全量返回给前端，尤其是包含密码、敏感备注等字段的表。

---

## 5. 统一响应与异常

### 5.1 Result

```java
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> fail(Integer code, String message) { ... }
}
```

成功：`code=200`。

### 5.2 错误码

| code | 含义 | 示例 |
|:--:|:--|:--|
| 400 | 参数错误 | 必填项缺失 |
| 401 | 未登录 | Session 不存在 |
| 403 | 无权限 | 学生访问管理员接口 |
| 404 | 数据不存在 | 预约记录不存在 |
| 409 | 业务冲突 | 时间段已被占用、重复预约 |
| 500 | 系统异常 | 未捕获异常 |

### 5.3 BusinessException

业务异常统一抛出：

```java
throw new BusinessException(409, "该咨询师在该时间段已有咨询安排");
```

`GlobalExceptionHandler` 负责转换为 `Result.fail()`。

---

## 6. 登录与权限架构

### 6.1 Session 内容

登录成功后向 Session 写入：

```java
session.setAttribute("LOGIN_USER", currentUserVO);
```

`CurrentUserVO` 字段：

- `id`
- `username`
- `realName`
- `phone`
- `roles`
- `primaryRole`

### 6.2 登录拦截器

`LoginInterceptor`：

1. 放行 `/api/auth/login`。
2. 放行静态资源和健康检查接口。
3. 其他 `/api/**` 请求必须存在 `LOGIN_USER`。
4. 不存在时返回 401。

### 6.3 角色权限

推荐轻量方式：

1. 在 Controller 方法中调用 `SessionUtils.requireRole(RoleCode.ADMIN)`。
2. 或自定义注解 `@RequireRole({RoleCode.ADMIN})` + 拦截器/AOP。

初期为了开发速度，可以先使用工具方法：

```java
CurrentUserVO user = SessionUtils.getRequiredCurrentUser(request);
SessionUtils.requireAnyRole(user, RoleCode.ADMIN);
```

后期若时间充足再抽成注解。

### 6.4 数据归属校验

必须在 Service 层做：

- 学生只能操作自己的登记表、预约、通知。
- 初访员只能处理分配给自己的预约任务。
- 咨询师只能处理自己的咨询安排、记录和结案报告。
- 管理员可访问管理范围内的全部记录。

---

## 7. 核心业务流

### 7.1 学生初访预约流

```text
学生提交首访登记表
  ↓ RiskAssessmentService 计算风险分数和等级
签署知情同意书
  ↓
查询可预约值班时间
  ↓
提交预约申请
  ↓ 校验是否已有未完成预约
创建 first_visit_appointment，状态 PENDING
```

### 7.2 管理员审核流

```text
管理员查看待审核列表
  ↓ 高风险排序/高亮由查询字段支持
管理员通过/驳回/改约/优先
  ↓
通过：校验值班容量和冲突，状态 APPROVED，写通知日志
驳回：状态 REJECTED，写驳回原因
优先：priority_flag=1
```

### 7.3 初访结果流

```text
初访员查看已通过预约
  ↓
录入危机等级、问题类型、初访结论
  ↓
若结论 ARRANGE_CONSULTATION：创建 consultation_queue
若 NO_NEED/TRANSFER：流程结束
```

### 7.4 正式咨询安排流

```text
助理选择队列学生
  ↓
选择咨询师、起始日期、时间段、咨询室
  ↓
生成候选咨询时段
  ↓
逐条校验咨询师/学生/咨询室冲突
  ↓
无冲突：插入 consultation_schedule，队列状态 ARRANGED，写通知日志
有冲突：返回 409 和冲突明细
```

### 7.5 咨询记录与结案流

```text
咨询师查看日程
  ↓
录入每次咨询记录
  ↓
更新 schedule_status
  ↓
如结案：填写 case_report
  ↓
提交报告，管理员可下载 Word
  ↓
若结案，更新当前个案和咨询安排状态
```

---

## 8. 重要业务规则实现点

### 8.1 风险评分

建议实现类：`RiskAssessmentService`。

输入：`FirstVisitFormRequest`。

建议算法：

```text
riskScore = moodScore + sleepScore + stressScore
if selfHarmFlag == 1: riskScore += 50
if emergencyFlag == 1: riskScore += 30

if emergencyFlag == 1 or riskScore >= 70: URGENT
else if selfHarmFlag == 1 or riskScore >= 40: HIGH
else if riskScore >= 20: MEDIUM
else LOW
```

此算法便于实现和答辩讲解，后续可扩展为配置化量表。

### 8.2 预约撤销规则

- PENDING：学生可撤销。
- APPROVED：只有当前时间距离预约开始时间至少 1 天才允许学生撤销。
- COMPLETED/REJECTED/CANCELED：不可撤销。

### 8.3 时间冲突规则

由 `ConsultationScheduleService` 和 `DutyScheduleService` 后端统一校验，不能只依赖前端。

校验范围：

1. 同一工作人员同一日期同一时间段不能重复值班。
2. 同一咨询师同一日期同一时间段不能重复咨询。
3. 同一学生同一日期同一时间段不能重复安排。
4. 同一咨询室同一日期同一时间段不能重复占用。

### 8.4 状态更新规则

状态必须单向流转，禁止随意回退。特殊情况如改约、取消必须记录原因。

---

## 9. 数据库访问策略

1. 所有表主键使用 `bigint`。
2. 列表查询必须分页。
3. 常用筛选字段建立索引。
4. 删除优先使用软删除或状态停用。
5. 密码字段不允许出现在 VO 中。
6. 心理咨询敏感内容列表中只展示摘要，详情接口再返回完整内容。

---

## 10. 文件导出架构

### 10.1 结案报告 Word 导出

服务：`CaseReportExportService`。

接口：

- 管理员：`GET /api/admin/case-reports/{id}/export-word`
- 咨询师：`GET /api/counselor/case-reports/{id}/export-word`

导出数据来源：

- `case_report`
- `student_profile`
- `sys_user` 学生姓名/电话
- `staff_profile` 咨询师信息
- `problem_type`

实现方式：

1. 查询报告详情。
2. 校验权限。
3. 使用 Apache POI 创建 Word 表格。
4. 设置 A4 页面、表格边框和字段。
5. 写入 `HttpServletResponse` 输出流。

### 10.2 Excel 导出

统计 Excel 可作为增强功能。服务：`StatisticsExportService` 或在 `StatisticsService` 中实现。

---

## 11. 日志架构

### 11.1 通知日志

服务：`NotificationLogService`。

业务节点：

1. 初访预约审核通过。
2. 初访预约改约。
3. 正式咨询安排成功。
4. 咨询安排取消。
5. 追加咨询申请审核结果。

模拟短信内容写入 `notification_log`，不接真实短信平台。

### 11.2 操作日志

服务：`OperationLogService`。

记录场景：

1. 管理员新增/修改用户。
2. 管理员配置值班。
3. 管理员审核预约。
4. 初访员提交初访结果。
5. 助理安排咨询。
6. 咨询师提交结案报告。
7. 管理员下载报告。

初期可在 Service 中手动调用记录，后期可扩展 AOP。

---

## 12. 配置文件建议

`application.yml`：

```yaml
server:
  port: 8080
  servlet:
    session:
      timeout: 2h

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/psychological_counseling?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jackson:
    time-zone: Asia/Shanghai
    date-format: yyyy-MM-dd HH:mm:ss

mybatis:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.tyut.psychological.*.entity
  configuration:
    map-underscore-to-camel-case: true

app:
  upload-dir: ./uploads
  report-dir: ./reports
  default-password: 123456
```

---

## 13. 开发顺序建议

1. 初始化 Spring Boot 工程和数据库连接。
2. 建立 common、config、auth、user 基础模块。
3. 完成 Session 登录和角色权限。
4. 完成数据库表和初始数据。
5. 完成学生首访登记、知情同意和预约接口。
6. 完成管理员值班、审核、风险预警接口。
7. 完成初访员结果录入接口。
8. 完成心理助理咨询队列和咨询安排接口。
9. 完成咨询师咨询记录、追加申请和结案报告接口。
10. 完成统计分析、日志查询和导出接口。
11. 联调前端、补充测试数据和文档截图。

---

## 14. 与前端协作约定

1. 所有接口返回 `Result<T>`。
2. 所有分页返回 `PageResult<T>`。
3. 所有枚举返回英文编码，由前端映射中文。
4. 所有时间统一使用 `yyyy-MM-dd HH:mm:ss` 或日期字段 `yyyy-MM-dd`。
5. 所有业务冲突返回 409 和明确 message。
6. Session 鉴权需要支持前端跨域携带 Cookie。
7. 文件下载接口返回二进制流，并设置 `Content-Disposition` 文件名。
