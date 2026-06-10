# 后端进度记录

> 本文件记录后端开发进度。当前仓库已进入基础功能落地与联调修复阶段。

---

## 2026-06-06 文档设计阶段

### 已完成

- [x] 确定后端技术栈：Java + Spring Boot + Spring MVC + MyBatis + MySQL + Session。
- [x] 完成后端架构文档：`docs/architecture.md`。
- [x] 完成后端接口设计文档：`docs/api.md`。
- [x] 完成数据库落地设计文档：`docs/database.md`。
- [x] 完成模块与类设计文档：`docs/modules.md`。
- [x] 完成业务规则与状态流转文档：`docs/business-rules.md`。
- [x] 完成后端实施计划：`docs/implementation-plan.md`。

### 当前状态

后端仓库当前 `dev` 基线已完成：
- 最小可启动骨架；
- ltb 阶段4基础信息接口；
- qxz 对阶段4的 review 修复；
- 支撑 lcw 学生端阶段2/3所需的最小学生接口（首访登记表 / 知情同意书）。

当前已可基于 MySQL 初始化项目数据库，完成数据库认证，并支撑管理员基础管理页面与学生“首访登记表 → 知情同意书”最小链路联调。

## 2026-06-07 整理并接入 ltb 阶段5后端接口

### 完成内容

- 在当前 `dev` 基线上整理 `dev-ltb` 的有效业务提交，去掉 README/test 噪音提交，仅保留阶段5相关接口与修复。
- 新增管理员值班管理接口：分页、创建、修改、批量排班。
- 新增管理员初访预约审核接口：审核列表、详情、通过、驳回、改约、标记优先。
- 新增 `FirstVisitAppointment`、`DutySchedule` 相关实体、DTO、VO、Mapper、Service 与 XML。
- 新增通知日志与操作日志的基础实体、Mapper、Service。
- 调整 `PsychologicalApplication` 的 `@MapperScan` 为全包扫描，并补上工作人员启用状态校验。
- 将整理后的阶段5代码在 `dev-qxz` 上通过 `./mvnw test` 验证，确认可继续联调与后续开发。

### 影响文件

- `src/main/java/com/tyut/psychological/PsychologicalApplication.java`
- `src/main/java/com/tyut/psychological/appointment/**`
- `src/main/java/com/tyut/psychological/common/log/**`
- `src/main/java/com/tyut/psychological/common/notification/**`
- `src/main/java/com/tyut/psychological/schedule/controller/DutyScheduleController.java`
- `src/main/java/com/tyut/psychological/schedule/dto/BatchScheduleRequest.java`
- `src/main/java/com/tyut/psychological/schedule/dto/BatchScheduleResponse.java`
- `src/main/java/com/tyut/psychological/schedule/dto/DutyScheduleSaveRequest.java`
- `src/main/java/com/tyut/psychological/schedule/entity/DutySchedule.java`
- `src/main/java/com/tyut/psychological/schedule/mapper/DutyScheduleMapper.java`
- `src/main/java/com/tyut/psychological/schedule/service/DutyScheduleService.java`
- `src/main/java/com/tyut/psychological/schedule/vo/DutyScheduleVO.java`
- `src/main/resources/mapper/appointment/FirstVisitAppointmentMapper.xml`
- `src/main/resources/mapper/common/NotificationLogMapper.xml`
- `src/main/resources/mapper/common/OperationLogMapper.xml`
- `src/main/resources/mapper/schedule/DutyScheduleMapper.xml`
- `docs/progress.md`

### 接口变化

- 新增 `GET /api/admin/duty-schedules`
- 新增 `POST /api/admin/duty-schedules`
- 新增 `PUT /api/admin/duty-schedules/{id}`
- 新增 `POST /api/admin/duty-schedules/batch`
- 新增 `GET /api/admin/first-visit/appointments`
- 新增 `GET /api/admin/first-visit/appointments/{id}`
- 新增 `POST /api/admin/first-visit/appointments/{id}/approve`
- 新增 `POST /api/admin/first-visit/appointments/{id}/reject`
- 新增 `POST /api/admin/first-visit/appointments/{id}/reschedule`
- 新增 `POST /api/admin/first-visit/appointments/{id}/priority`

### 验证方式

- `./mvnw test`
- `git cherry-pick` 整理阶段5有效提交后，确认 5 个提交均可无冲突落到当前 `dev` 基线

### 遗留问题

- 当前仅完成管理员值班与初访审核接口，学生预约提交、我的预约、初访员结果录入、咨询队列等后续阶段接口仍待继续完成。
- 通知日志与操作日志目前提供了基础持久层与服务，日志查询接口仍未补齐。

## 2026-06-07 阶段四：后端基础信息接口

### 完成内容

- 接入 MySQL 数据库，移除 `DataSourceAutoConfiguration` 排除。
- 创建 21 张核心数据库表（`init_schema.sql`）。
- 创建初始数据脚本（`init_data.sql`），包含五类角色、五个演示账号、学生档案、工作人员档案、时间段、咨询室、问题类型和系统配置。
- 更新 `application.yml` 数据库连接配置，支持通过环境变量覆盖数据库连接信息。
- 创建实体类：`SysUser`、`SysRole`、`SysUserRole`、`StaffProfile`、`CounselingRoom`、`TimeSlot`。
- 创建 Mapper 接口和 MyBatis XML：`UserMapper`、`StaffProfileMapper`、`CounselingRoomMapper`、`TimeSlotMapper`。
- 创建 DTO：`UserQuery`、`UserSaveRequest`、`StaffQuery`、`StaffSaveRequest`、`RoomSaveRequest`、`TimeSlotSaveRequest`。
- 创建 VO：`UserVO`、`StaffVO`、`RoomVO`、`TimeSlotVO`、`OptionVO`。
- 实现用户管理接口：分页查询 `GET /api/admin/users`、新增 `POST /api/admin/users`、修改 `PUT /api/admin/users/{id}`、启用 `POST /api/admin/users/{id}/enable`、禁用 `POST /api/admin/users/{id}/disable`、重置密码 `POST /api/admin/users/{id}/reset-password`。
- 实现工作人员管理接口：分页查询 `GET /api/admin/staff`、新增 `POST /api/admin/staff`、修改 `PUT /api/admin/staff/{id}`、选项 `GET /api/admin/staff/options`。
- 实现咨询室管理接口：分页查询 `GET /api/admin/rooms`、新增 `POST /api/admin/rooms`、修改 `PUT /api/admin/rooms/{id}`、选项 `GET /api/admin/rooms/options`。
- 实现时间段管理接口：分页查询 `GET /api/admin/time-slots`、新增 `POST /api/admin/time-slots`、修改 `PUT /api/admin/time-slots/{id}`、选项 `GET /api/admin/time-slots/options`。
- 修改 `AuthService` 接入数据库认证（替代内存临时账号）。
- 修改 `PsychologicalApplication` 添加 `@MapperScan` 并移除数据源排除。
- 添加 `PageQuery.getOffset()` 方法支持 MyBatis 分页偏移量。

### 影响文件

- `pom.xml`（无变更，已有 MyBatis 和 MySQL 依赖）
- `src/main/resources/application.yml`（添加 datasource 配置）
- `src/main/java/com/tyut/psychological/PsychologicalApplication.java`（移除排除，加 MapperScan）
- `src/main/java/com/tyut/psychological/common/api/PageQuery.java`（添加 getOffset）
- `src/main/java/com/tyut/psychological/common/vo/OptionVO.java`（新增）
- `src/main/java/com/tyut/psychological/user/entity/SysUser.java`（新增）
- `src/main/java/com/tyut/psychological/user/entity/SysRole.java`（新增）
- `src/main/java/com/tyut/psychological/user/entity/SysUserRole.java`（新增）
- `src/main/java/com/tyut/psychological/user/dto/UserQuery.java`（新增）
- `src/main/java/com/tyut/psychological/user/dto/UserSaveRequest.java`（新增）
- `src/main/java/com/tyut/psychological/user/vo/UserVO.java`（新增）
- `src/main/java/com/tyut/psychological/user/mapper/UserMapper.java`（新增）
- `src/main/java/com/tyut/psychological/user/service/UserService.java`（新增）
- `src/main/java/com/tyut/psychological/user/controller/UserController.java`（新增）
- `src/main/java/com/tyut/psychological/profile/entity/StaffProfile.java`（新增）
- `src/main/java/com/tyut/psychological/profile/dto/StaffQuery.java`（新增）
- `src/main/java/com/tyut/psychological/profile/dto/StaffSaveRequest.java`（新增）
- `src/main/java/com/tyut/psychological/profile/vo/StaffVO.java`（新增）
- `src/main/java/com/tyut/psychological/profile/mapper/StaffProfileMapper.java`（新增）
- `src/main/java/com/tyut/psychological/profile/service/StaffProfileService.java`（新增）
- `src/main/java/com/tyut/psychological/profile/controller/StaffController.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/entity/CounselingRoom.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/entity/TimeSlot.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/dto/RoomSaveRequest.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/dto/TimeSlotSaveRequest.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/vo/RoomVO.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/vo/TimeSlotVO.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/mapper/CounselingRoomMapper.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/mapper/TimeSlotMapper.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/service/RoomService.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/service/TimeSlotService.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/controller/RoomController.java`（新增）
- `src/main/java/com/tyut/psychological/schedule/controller/TimeSlotController.java`（新增）
- `src/main/java/com/tyut/psychological/auth/service/AuthService.java`（改为数据库认证）
- `src/main/resources/mapper/user/UserMapper.xml`（新增）
- `src/main/resources/mapper/profile/StaffProfileMapper.xml`（新增）
- `src/main/resources/mapper/schedule/CounselingRoomMapper.xml`（新增）
- `src/main/resources/mapper/schedule/TimeSlotMapper.xml`（新增）
- `sql/init_schema.sql`（新增，21 张表建表脚本）
- `sql/init_data.sql`（新增，初始数据）

### 接口变化

- 新增 `GET /api/admin/users` 用户分页
- 新增 `POST /api/admin/users` 新增用户
- 新增 `PUT /api/admin/users/{id}` 修改用户
- 新增 `POST /api/admin/users/{id}/enable` 启用用户
- 新增 `POST /api/admin/users/{id}/disable` 禁用用户
- 新增 `POST /api/admin/users/{id}/reset-password` 重置密码
- 新增 `GET /api/admin/staff` 工作人员分页
- 新增 `POST /api/admin/staff` 新增工作人员
- 新增 `PUT /api/admin/staff/{id}` 修改工作人员
- 新增 `GET /api/admin/staff/options` 工作人员选项
- 新增 `GET /api/admin/rooms` 咨询室分页
- 新增 `POST /api/admin/rooms` 新增咨询室
- 新增 `PUT /api/admin/rooms/{id}` 修改咨询室
- 新增 `GET /api/admin/rooms/options` 咨询室选项
- 新增 `GET /api/admin/time-slots` 时间段分页
- 新增 `POST /api/admin/time-slots` 新增时间段
- 新增 `PUT /api/admin/time-slots/{id}` 修改时间段
- 新增 `GET /api/admin/time-slots/options` 时间段选项

### 数据库变化

- 创建数据库 `psychological_counseling`。
- 创建 21 张核心表。
- 初始数据：5 个角色、5 个用户、1 个学生档案、4 个工作人员档案、6 个时间段、3 个咨询室、8 个问题类型、5 个系统配置。

### 验证方式

- `./mvnw test`
- 启动应用后测试：`/api/auth/login`、`/api/auth/current`
- 启动应用后测试：`/api/admin/users`、`/api/admin/staff`、`/api/admin/rooms`、`/api/admin/time-slots`
- 使用学生账号测试：`/api/student/first-visit/forms/latest`、`/api/student/first-visit/forms`、`/api/student/consents/status`、`/api/student/consents/sign`

### 遗留问题

- 学生端预约提交、我的预约、我的通知等接口仍未完成。
- 值班、审核、初访结果、咨询队列等后续业务接口仍待继续实现。

---

## 待开发任务总览

### 阶段一：公共基础

- [x] 初始化 Spring Boot 工程。
- [x] 配置 Maven 依赖。
- [x] 配置 `application.yml`。
- [x] 创建 `Result`、`PageResult`、`PageQuery`。
- [x] 创建 `BusinessException` 和全局异常处理。
- [x] 创建角色、状态、风险等级等枚举。
- [x] 创建密码工具、Session 工具、时间工具。
- [x] 创建登录拦截器和 CORS 配置。

### 阶段二：数据库

- [x] 编写 `sql/init_schema.sql`。
- [x] 编写 `sql/init_data.sql`。
- [ ] 编写 `sql/demo_data.sql`。
- [x] 创建 21 张核心表。
- [x] 初始化五类角色和演示账号。
- [x] 初始化时间段、咨询室、问题类型。
- [x] 创建实体类和基础 Mapper。

### 阶段三：认证与用户

- [x] 登录接口。
- [x] 退出接口。
- [x] 当前用户接口。
- [x] 用户分页、新增、修改、启用、禁用、重置密码。
- [x] 工作人员分页、新增、修改、选项接口。
- [ ] 学生档案查询与维护。

### 阶段四：时间配置和值班

- [x] 咨询室管理接口。
- [x] 时间段管理接口。
- [x] 值班分页接口。
- [x] 值班新增/编辑接口。
- [x] 值班冲突检测。
- [ ] 可预约时间段查询。
- [x] 批量排班接口（可选）。

### 阶段五：学生首访预约

- [x] 首访登记表提交接口。
- [x] 风险评分服务。
- [x] 知情同意状态查询。
- [x] 知情同意签署。
- [x] 初访预约提交。
- [x] 我的预约分页。
- [x] 学生撤销预约。
- [x] 学生通知查询.

### 阶段六：管理员审核

- [x] 初访预约审核列表。
- [x] 预约详情接口。
- [x] 审核通过。
- [x] 驳回预约。
- [x] 改约。
- [x] 标记优先。
- [ ] 审核通知日志。
- [ ] 审核操作日志。

### 阶段七：初访员与咨询队列

- [ ] 初访员任务列表。
- [ ] 初访任务详情。
- [ ] 初访结果提交。
- [ ] 自动创建咨询队列。
- [ ] 咨询队列分页。
- [ ] 队列暂缓。

### 阶段八：心理助理咨询安排

- [ ] 咨询师可用时间查询。
- [ ] 正式咨询安排创建。
- [ ] 咨询师冲突检测。
- [ ] 学生冲突检测。
- [ ] 咨询室冲突检测。
- [ ] 咨询安排分页。
- [ ] 取消咨询安排。
- [ ] 结案或取消时更新咨询安排状态。

### 阶段九：咨询师端

- [ ] 我的咨询日程。
- [ ] 日程详情。
- [ ] 查询咨询记录。
- [ ] 保存咨询记录。
- [ ] 追加咨询申请。
- [ ] 管理员审核追加申请。
- [ ] 结案报告保存草稿。
- [ ] 结案报告提交。

### 阶段十：报告、统计、日志

- [ ] 结案报告 Word 导出。
- [ ] 管理员结案报告分页。
- [ ] 管理员下载报告。
- [ ] 统计总览。
- [ ] 月度趋势。
- [ ] 问题类型分布。
- [ ] 危机等级分布。
- [ ] 咨询师工作量。
- [ ] 通知日志分页。
- [ ] 操作日志分页。
- [ ] 统计 Excel 导出（可选）。

---

## 当前风险

1. 预约容量和时间冲突需要事务保证，不能只做前端校验。
2. 值班、审核、初访结果、咨询队列等主流程接口尚未补齐，后续联调仍有工作量。
3. 咨询安排涉及咨询师、学生和咨询室冲突，必须由后端统一校验。
4. 结案报告 Word 导出格式可能需要较多调试时间。
5. 统计接口依赖演示数据，需提前准备 `demo_data.sql`。
6. 当前环境已统一使用 JDK 21；如组员本地环境不一致需再次核对。

---

## 2026-06-07 收尾 review、修复并合入 `dev`

### 完成内容
- 将 qxz 对 ltb 阶段4后端的 review 修复通过 PR #8 合入 `dev`。
- 创建并初始化本地项目数据库，验证数据库认证与管理员基础管理接口可用。
- 为支撑 lcw 已合入前端阶段2/3页面，补齐学生端最小接口：首访登记表 latest/save、知情同意状态查询/签署。
- 浏览器与接口双重验证管理员基础管理接口、学生首访登记与知情同意链路，确认当前 `dev` 基线满足已合 PR 宣称进度。

### 影响文件
- `src/main/java/com/tyut/psychological/student/**`
- `src/main/resources/mapper/student/StudentFormMapper.xml`
- `src/main/resources/application.yml`
- `sql/init_schema.sql`
- `sql/init_data.sql`
- `docs/progress.md`

### 接口变化
- 新增 `GET /api/student/first-visit/forms/latest`
- 新增 `POST /api/student/first-visit/forms`
- 新增 `GET /api/student/consents/status`
- 新增 `POST /api/student/consents/sign`

### 数据库变化
- 本地创建项目库 `psychological_counseling` 并导入初始化脚本。
- 新增本地专用数据库账号 `project_psy` 用于联调测试。

### 验证方式
- `./mvnw test`
- 启动应用后测试：`/api/auth/login`、`/api/auth/current`
- 启动应用后测试：`/api/admin/users`、`/api/admin/staff`、`/api/admin/rooms`、`/api/admin/time-slots`
- 启动应用后测试：`/api/student/first-visit/forms/latest`、`/api/student/first-visit/forms`、`/api/student/consents/status`、`/api/student/consents/sign`
- 浏览器实测：学生首访登记表提交、知情同意签署、跳转预约骨架页

### 遗留问题
- 学生端预约提交、我的预约、我的通知等接口仍未完成。
- 值班、审核、初访结果、咨询队列等后续业务接口仍待继续实现。

---

## 后续更新规则

## 2026-06-06 后端最小骨架

### 完成内容
- 初始化 Spring Boot 工程并加入 `mvnw`。
- 配置 `pom.xml`，接入 Spring Web、Validation、MyBatis、MySQL 驱动。
- 创建 `PsychologicalApplication` 与 `application.yml`。
- 创建 `Result`、`PageResult`、`PageQuery`、`BusinessException`、`GlobalExceptionHandler`。
- 创建角色与核心状态枚举。
- 创建 `PasswordUtils`、`SessionUtils`、`LoginInterceptor`、`WebMvcConfig`。
- 创建 `/api/health`、`POST /api/auth/login`、`POST /api/auth/logout`、`GET /api/auth/current`。
- 内置五类临时账号供前端联调。

### 影响文件
- `pom.xml`
- `src/main/java/com/tyut/psychological/**`
- `src/main/resources/application.yml`

### 接口变化
- 新增健康检查和临时认证相关接口。

### 数据库变化
- 当前阶段未接入真实 MySQL，先排除 `DataSourceAutoConfiguration`，后续接数据库时恢复。

### 验证方式
- `./mvnw test`
- `./mvnw package -DskipTests`
- `java -jar target/psychological-counseling-backend-0.0.1-SNAPSHOT.jar`
- `curl http://127.0.0.1:24681/api/health`
- `curl POST /api/auth/login`

### 遗留问题
- 目前认证使用内存临时账号，尚未接真实数据库和用户表。
- 业务模块、SQL 脚本和 Mapper 仍待继续实现。

## 2026-06-08 阶段四：咨询队列与咨询安排（dev-zyt）

### 完成内容

- 新增 `consultation` 模块：咨询队列分页/详情/暂缓、正式咨询安排创建/列表/取消。
- 实现咨询师可用时间查询，基于值班安排并标记冲突不可用原因。
- 实现咨询师、学生、咨询室三方时间冲突检测，409 响应携带 `conflicts` 明细。
- 安排成功后更新队列状态为 `ARRANGED`，并写入通知日志与操作日志。
- 扩展 `BusinessException` 支持附带 `data` 载荷，供冲突响应使用。
- 补充 `init_data.sql` 演示数据：咨询师值班、初访结果、3 条 WAITING 队列。

### 影响文件

- `src/main/java/com/tyut/psychological/consultation/**`
- `src/main/resources/mapper/consultation/**`
- `src/main/java/com/tyut/psychological/common/exception/BusinessException.java`
- `src/main/java/com/tyut/psychological/common/exception/GlobalExceptionHandler.java`
- `src/main/java/com/tyut/psychological/common/notification/service/NotificationLogService.java`
- `sql/init_data.sql`
- `docs/progress.md`

### 接口变化

- 新增 `GET /api/assistant/consultation/queue`
- 新增 `GET /api/assistant/consultation/queue/{id}`
- 新增 `POST /api/assistant/consultation/queue/{id}/suspend`
- 新增 `GET /api/assistant/counselors/available-slots`
- 新增 `POST /api/assistant/consultation/schedules`
- 新增 `GET /api/assistant/consultation/schedules`
- 新增 `POST /api/assistant/consultation/schedules/{id}/cancel`

### 验证方式

- `./mvnw compile -DskipTests` 通过
- 使用心理助理账号 `assistant/123456` 调用上述接口

### 遗留问题

- 前端仍使用 mock，阶段七联调时再切换真实接口
- 初访结果入队（`enqueue`）仍依赖 ltb 初访员接口，当前靠演示数据支撑

## 2026-06-08 qxz review 修复：咨询安排模块

### 完成内容

- review 刚合入的咨询队列/咨询安排代码后，修复分页 SQL 中直接拼接 `OFFSET` 的写法，改为参数化 offset，避免注入与分页边界问题。
- 补强心理助理身份校验：除“存在且启用”外，进一步要求 `staffType=ASSISTANT`，避免其他工作人员越权安排或取消咨询。
- 为咨询安排服务补充单元测试，覆盖“非助理账号不可安排咨询”和“助理校验通过后继续做时间段存在性校验”两类场景。

### 影响文件

- `src/main/java/com/tyut/psychological/consultation/dto/ConsultationQueueQuery.java`
- `src/main/java/com/tyut/psychological/consultation/dto/ScheduleQuery.java`
- `src/main/java/com/tyut/psychological/consultation/service/ConsultationScheduleService.java`
- `src/main/resources/mapper/consultation/ConsultationQueueMapper.xml`
- `src/main/resources/mapper/consultation/ConsultationScheduleMapper.xml`
- `src/test/java/com/tyut/psychological/consultation/service/ConsultationScheduleServiceTest.java`
- `docs/progress.md`

### 验证方式

- `./mvnw test`

### 遗留问题

- `GET /api/assistant/counselors/available-slots` 当前只按单日 `startDate` 查询；若后续前端需要“从起始日向后看多日可约时间”，需再扩展接口语义与实现。

---

## 2026-06-08 阶段五：学生首访预约接口

### 完成内容

- 实现学生预约列表查询接口 `GET /api/student/appointments`，支持按状态筛选和分页。
- 实现学生撤销预约接口 `POST /api/student/appointments/{id}/cancel`，支持 PENDING 状态直接撤销和 APPROVED 状态提前一天撤销规则。
- 实现学生通知查询接口 `GET /api/student/notifications`，支持按通知类型筛选和分页。
- 新增 `MyAppointmentVO` 和 `MyNotificationVO` 用于返回学生预约和通知数据。
- 新增 `AppointmentCancelRequest` DTO 用于撤销预约请求参数校验。
- 在 `StudentAppointmentMapper` 中新增查询学生预约列表、统计学生预约数量、查询学生通知列表、统计学生通知数量的方法。
- 在 `StudentAppointmentMapper.xml` 中新增对应的 SQL 实现，支持多表联查和动态条件筛选。

### 影响文件

- `src/main/java/com/tyut/psychological/student/controller/StudentAppointmentController.java`
- `src/main/java/com/tyut/psychological/student/service/StudentAppointmentService.java`
- `src/main/java/com/tyut/psychological/student/mapper/StudentAppointmentMapper.java`
- `src/main/java/com/tyut/psychological/student/vo/MyAppointmentVO.java`
- `src/main/java/com/tyut/psychological/student/vo/MyNotificationVO.java`
- `src/main/java/com/tyut/psychological/student/dto/AppointmentCancelRequest.java`
- `src/main/resources/mapper/student/StudentAppointmentMapper.xml`

### 接口变化

- 新增 `GET /api/student/appointments?pageNum=1&pageSize=10&status=PENDING` 学生预约列表
- 新增 `POST /api/student/appointments/{id}/cancel` 学生撤销预约
- 新增 `GET /api/student/notifications?pageNum=1&pageSize=10&notifyType=APPOINTMENT_APPROVED` 学生通知列表

### 数据库变化

- 无新增表，使用现有 `first_visit_appointment` 和 `notification_log` 表

### 验证方式

- `./mvnw compile -DskipTests`
- 使用学生账号 `student/123456` 调用上述接口
- 测试预约列表查询、撤销预约、通知列表查询功能

### 遗留问题

- 前端仍使用 mock，需等待前端联调时切换真实接口
- 撤销预约后通知日志未记录，后续可扩展

---

## 2026-06-08 review 修正 lcw 阶段5 接口整合冲突

### 完成内容

- 在 `dev-qxz` 上复核 lcw 学生预约/通知接口合入结果，并处理与既有代码的整合冲突。
- 停用旧 `appointment` 包下重复暴露的学生预约控制器，消除同名 Bean 冲突。
- 停用旧通知控制器，消除 `/api/student/notifications` 的重复路由映射，保留支持 `notifyType` 筛选的新实现。
- 复跑 Spring Boot 测试，确认应用上下文可正常启动，学生预约/通知接口不再阻塞集成。

### 影响文件

- `src/main/java/com/tyut/psychological/appointment/controller/StudentAppointmentController.java`
- `src/main/java/com/tyut/psychological/common/notification/controller/StudentNotificationController.java`
- `docs/progress.md`

### 验证方式

- `./mvnw test`

### 遗留问题

- 当前学生预约与通知查询仍使用内存分页；若后续演示数据增多，建议改为 SQL 分页。
- 历史模块中仍保留部分旧服务/DTO/VO 代码，当前已不对外暴露，后续如继续重构可再统一收敛。

---

## 2026-06-09 阶段六：后端初访员接口

### 完成内容

- 实现初访员任务分页接口 `GET /api/interviewer/tasks`，支持按日期范围、状态、风险等级筛选。
- 实现初访任务详情接口 `GET /api/interviewer/tasks/{appointmentId}`，包含学生信息、登记表摘要、预约信息。
- 实现初访结果提交接口 `POST /api/interviewer/tasks/{appointmentId}/result`，支持完整的业务规则校验。
- 实现业务规则校验：预约状态必须为 APPROVED、初访员归属校验、转介送诊时后续建议必填。
- 实现咨询队列创建逻辑：当初访结论为 ARRANGE_CONSULTATION 时自动创建咨询队列，并根据危机等级和优先标记计算优先级分数。
- 实现操作日志记录：提交初访结果时记录操作日志。
- 新增 `FirstVisitResult` 实体类、`FirstVisitResultMapper` 接口和 XML 映射文件。
- 新增 `InterviewerController` 控制器，提供初访员相关接口。
- 新增 `InterviewerService` 服务类，实现完整的业务逻辑。
- 新增 `InterviewResultRequest` DTO 用于初访结果提交请求参数校验。
- 新增 `InterviewTaskVO` 和 `InterviewTaskDetailVO` 用于返回初访任务数据。
- 更新 `FirstVisitAppointmentMapper` 接口和 XML，添加初访任务相关的查询方法。
- 创建单元测试 `InterviewerServiceTest`，验证业务规则校验和核心功能。

### 影响文件

- `src/main/java/com/tyut/psychological/interviewer/**`（新增）
- `src/main/java/com/tyut/psychological/appointment/mapper/FirstVisitAppointmentMapper.java`（更新）
- `src/main/resources/mapper/appointment/FirstVisitAppointmentMapper.xml`（更新）
- `src/main/resources/mapper/interviewer/FirstVisitResultMapper.xml`（新增）
- `src/test/java/com/tyut/psychological/interviewer/service/InterviewerServiceTest.java`（新增）
- `docs/progress.md`（更新）

### 接口变化

- 新增 `GET /api/interviewer/tasks` 初访员任务分页列表
- 新增 `GET /api/interviewer/tasks/{appointmentId}` 初访任务详情
- 新增 `POST /api/interviewer/tasks/{appointmentId}/result` 提交初访结果

### 数据库变化

- 无新增表，使用现有 `first_visit_result` 和 `consultation_queue` 表

### 验证方式

- `./mvnw test -Dtest=InterviewerServiceTest`
- `./mvnw compile -DskipTests`
- 使用初访员账号 `interviewer/123456` 调用上述接口
- 测试初访任务列表查询、任务详情查看、初访结果提交功能

### 遗留问题

- 前端仍使用 mock，需等待前端联调时切换真实接口
- 初访结果提交后通知日志未记录，后续可扩展
- 初访结果编辑功能未实现，当前只能提交一次，后续可根据需求扩展

---

## 2026-06-09 qxz review 修复：初访员模块

### 完成内容

- 复核 `ltb` 合入的初访员模块后，补强初访员身份校验：除“存在且角色为 INTERVIEWER”外，进一步要求工作人员状态为启用，避免停用账号继续访问任务或提交结果。
- 为初访任务分页增加页码与页大小的兜底处理，避免非法分页参数触发内存分页边界异常。
- 为 `InterviewerService` 补充单元测试，覆盖“停用初访员不可访问任务列表”和“非法分页参数自动归一化”两个场景，并同步修正既有测试数据使其符合启用校验。

### 影响文件

- `src/main/java/com/tyut/psychological/interviewer/service/InterviewerService.java`
- `src/test/java/com/tyut/psychological/interviewer/service/InterviewerServiceTest.java`
- `docs/progress.md`

### 接口变化

- 无新增接口，补强既有 `GET /api/interviewer/tasks`、`GET /api/interviewer/tasks/{appointmentId}`、`POST /api/interviewer/tasks/{appointmentId}/result` 的鉴权与参数健壮性。

### 验证方式

- `./mvnw test`

### 遗留问题

- 初访任务列表当前仍采用“先查全量再内存分页”的实现；当前数据量下可用，若后续演示数据增多，建议下沉到 SQL 分页。
- 初访结果提交后通知日志未记录，后续可按业务需要补充。

---

## 2026-06-09 阶段5：咨询师接口

### 完成内容

- 新增 `CounselorConsultationController`：咨询师日程分页/详情、咨询记录查询与保存、追加申请分页与新增。
- 新增 `AdminExtensionController`：管理员审核追加咨询申请（通过/驳回）。
- 新增 `CounselorCaseReportController`：结案报告列表、详情、保存草稿、更新、提交。
- 新增 `AdminCaseReportController`：管理员查看已提交结案报告。
- 新增实体/Mapper/Service：`ConsultationRecord`、`ExtensionRequest`、`CaseReport` 全套。
- 保存咨询记录后同步更新 `consultation_schedule.schedule_status`。
- 提交结案报告后关闭相关咨询安排状态。
- 补充演示咨询安排数据与 `scripts/test-counselor-api.ps1` 测试脚本。

### 影响文件

- `src/main/java/com/tyut/psychological/consultation/controller/CounselorConsultationController.java`
- `src/main/java/com/tyut/psychological/consultation/controller/AdminExtensionController.java`
- `src/main/java/com/tyut/psychological/consultation/service/ConsultationRecordService.java`
- `src/main/java/com/tyut/psychological/consultation/service/ExtensionRequestService.java`
- `src/main/java/com/tyut/psychological/consultation/service/CounselorAccessService.java`
- `src/main/java/com/tyut/psychological/report/**`
- `src/main/resources/mapper/consultation/ConsultationRecordMapper.xml`
- `src/main/resources/mapper/consultation/ExtensionRequestMapper.xml`
- `src/main/resources/mapper/report/CaseReportMapper.xml`
- `sql/demo_consultation.sql`
- `scripts/test-counselor-api.ps1`

### 接口变化

- 新增 `GET /api/counselor/schedules`
- 新增 `GET /api/counselor/schedules/{id}`
- 新增 `GET /api/counselor/schedules/{scheduleId}/record`
- 新增 `POST /api/counselor/schedules/{scheduleId}/record`
- 新增 `GET /api/counselor/extension-requests`
- 新增 `POST /api/counselor/extension-requests`
- 新增 `GET /api/admin/extension-requests`
- 新增 `POST /api/admin/extension-requests/{id}/approve`
- 新增 `POST /api/admin/extension-requests/{id}/reject`
- 新增 `GET /api/counselor/case-reports`
- 新增 `GET /api/counselor/case-reports/{id}`
- 新增 `POST /api/counselor/case-reports`
- 新增 `PUT /api/counselor/case-reports/{id}`
- 新增 `POST /api/counselor/case-reports/{id}/submit`
- 新增 `GET /api/admin/case-reports`
- 新增 `GET /api/admin/case-reports/{id}`

### 验证方式

- 使用咨询师账号 `counselor/123456` 调用上述接口
- 使用管理员账号 `admin/123456` 审核追加申请、查看已提交报告
- 运行 `scripts/test-counselor-api.ps1`

### 遗留问题

- Word 导出接口留待阶段6实现
- 前端咨询师页面仍使用 mock，阶段七联调时切换

---

## 2026-06-10 qxz review 修复：咨询师接口分页与审核规则

### 完成内容

- 复核 zyt 阶段5后端代码后，修复咨询师日程、追加申请、结案报告分页 SQL 中重新出现的 `${...}` 偏移量拼接，统一改回参数化 `offset`，避免注入风险与非法分页边界问题回归。
- 补强追加咨询申请审核规则：管理员驳回时要求 `reason` 必填，避免出现“驳回成功但无原因”的无效审核记录。
- 为 `ExtensionRequestService` 补充单元测试，覆盖“驳回原因必填”和“非法分页参数自动归一化”两个场景。

### 影响文件

- `src/main/java/com/tyut/psychological/consultation/dto/CounselorScheduleQuery.java`
- `src/main/java/com/tyut/psychological/consultation/dto/ExtensionQuery.java`
- `src/main/java/com/tyut/psychological/consultation/dto/ExtensionAdminQuery.java`
- `src/main/java/com/tyut/psychological/report/dto/CaseReportQuery.java`
- `src/main/java/com/tyut/psychological/report/dto/CaseReportAdminQuery.java`
- `src/main/java/com/tyut/psychological/consultation/service/ExtensionRequestService.java`
- `src/main/resources/mapper/consultation/ConsultationScheduleMapper.xml`
- `src/main/resources/mapper/consultation/ExtensionRequestMapper.xml`
- `src/main/resources/mapper/report/CaseReportMapper.xml`
- `src/test/java/com/tyut/psychological/consultation/service/ExtensionRequestServiceTest.java`
- `docs/progress.md`

### 接口变化

- 无新增接口，补强既有咨询师/管理员分页查询与管理员驳回追加申请的参数安全性和业务约束。

### 验证方式

- `./mvnw test`

### 遗留问题

- 当前新增的咨询师接口仍以单元测试和脚本验证为主，完整前后端联调仍待阶段七统一完成。
- 结案报告仍未实现 Word 导出与下载链路。

---

每完成一个模块，应追加记录：

```text
## YYYY-MM-DD 模块名称

### 完成内容
- ...

### 影响文件
- ...

### 接口变化
- ...

### 数据库变化
- ...

### 验证方式
- mvn package
- Postman/HTTP Client 接口测试
- 前端联调结果

### 遗留问题
- ...

---

## 2026-06-10 修复学生通知查询SQL兼容性问题

### 完成内容

- 调整学生通知查询 SQL，修复“我的通知”页面查询异常
- 同步更新后端进度记录

### 影响文件

- `src/main/resources/mapper/student/StudentAppointmentMapper.xml`

### 验证方式

- `./mvnw compile -DskipTests`
- 重启后端服务后测试"我的通知"页面

### 遗留问题

- 通知查询字段需继续与仓库统一初始化表结构保持一致

---

## 2026-06-10 qxz review 修复：学生通知查询SQL字段回退

### 完成内容

- 复核 `lcw` 最新 PR 合入结果后，确认通知查询错误回退到本地旧库字段，和仓库统一使用的 `notification_log` 表结构不一致。
- 将学生通知查询恢复为 `receiver_user_id`、`send_status`、`send_time`、`related_id` 这一套仓库已落地字段，保持与 `sql/init_schema.sql`、`NotificationLogMapper.xml`、`MyNotificationVO` 一致。
- 补充资源级回归测试，防止后续再次把学生通知 SQL 改回不存在的 `user_id` 字段。

### 影响文件

- `src/main/resources/mapper/student/StudentAppointmentMapper.xml`
- `src/test/java/com/tyut/psychological/student/mapper/StudentAppointmentMapperXmlTest.java`
- `docs/progress.md`

### 接口变化

- 无新增接口，修复既有 `GET /api/student/notifications` 的查询字段与仓库标准表结构一致性。

### 验证方式

- `./mvnw test -Dtest=StudentAppointmentMapperXmlTest`
- `./mvnw test`

### 遗留问题

- 若本地数据库仍保留早期非标准表结构，需要重新执行 `sql/init_schema.sql` / `sql/init_data.sql` 或手动对齐 `notification_log` 表字段。

---

## 2026-06-10 qxz review 修复：回退 lcw 阶段7 的旧库兼容改动

### 完成内容

- 在 `dev-qxz` 上同步 `dev` 后复核 lcw 最新 PR #29，确认其中两处通知相关 XML 将仓库统一使用的 `notification_log` 字段错误回退为本地旧库的 `user_id` 写法。
- 将 `StudentAppointmentMapper.xml` 与 `NotificationLogMapper.xml` 恢复到仓库标准字段：`receiver_user_id`、`send_status`、`send_time`、`related_id`，保持与 `sql/init_schema.sql`、`NotificationLog` 实体和通知服务一致。
- 将 `FirstVisitAppointmentMapper.xml` 恢复为 `COALESCE(sp.contact_phone, su.phone)`，保证预约审核与初访任务详情优先返回学生档案联系电话而不是仅返回用户表手机号。
- 新增资源级回归测试，防止后续再次把通知日志与学生通知查询改回旧库字段，或丢失学生档案联系电话回退逻辑。

### 影响文件

- `src/main/resources/mapper/student/StudentAppointmentMapper.xml`
- `src/main/resources/mapper/common/NotificationLogMapper.xml`
- `src/main/resources/mapper/appointment/FirstVisitAppointmentMapper.xml`
- `src/test/java/com/tyut/psychological/student/mapper/StudentAppointmentMapperXmlTest.java`
- `src/test/java/com/tyut/psychological/common/notification/mapper/NotificationLogMapperXmlTest.java`
- `src/test/java/com/tyut/psychological/appointment/mapper/FirstVisitAppointmentMapperXmlTest.java`
- `docs/progress.md`

### 接口变化

- 无新增接口，修复既有学生通知、通知日志写入与预约审核详情查询对仓库标准表结构/字段语义的一致性。

### 验证方式

- `./mvnw test -Dtest=StudentAppointmentMapperXmlTest,NotificationLogMapperXmlTest,FirstVisitAppointmentMapperXmlTest`
- `./mvnw test`

### 遗留问题

- 若本地数据库仍保留早期手工调整过的旧 `notification_log` 结构，仍需按仓库脚本重新对齐后再联调。
