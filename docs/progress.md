# 后端进度记录

> 本文件记录后端开发进度。当前阶段为详细设计阶段，尚未写入实际后端代码。

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

后端仓库已完成最小可启动骨架初始化，包含 Spring Boot、`mvnw`、统一返回、分页对象、全局异常、角色/状态枚举、Session 工具、登录拦截器、CORS 配置、健康检查接口和临时认证接口。当前可在无数据库模式下启动并供前端联调登录与健康检查。

## 2026-06-07 阶段四：后端基础信息接口

### 完成内容

- 接入 MySQL 数据库，移除 `DataSourceAutoConfiguration` 排除。
- 创建 21 张核心数据库表（`init_schema.sql`）。
- 创建初始数据脚本（`init_data.sql`），包含五类角色、五个演示账号、学生档案、工作人员档案、时间段、咨询室、问题类型和系统配置。
- 更新 `application.yml` 数据库连接配置（密码 123456）。
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

- `./mvnw compile`
- `./mvnw package -DskipTests`
- 启动应用后测试接口（见测试文件）

### 遗留问题

- 初始数据使用英文姓名（因编码问题执行 clean SQL），中文版保留在 `init_data.sql` 中供后续重新执行。

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

- [ ] 编写 `sql/init_schema.sql`。
- [ ] 编写 `sql/init_data.sql`。
- [ ] 编写 `sql/demo_data.sql`。
- [ ] 创建 21 张核心表。
- [ ] 初始化五类角色和演示账号。
- [ ] 初始化时间段、咨询室、问题类型。
- [ ] 创建实体类和基础 Mapper。

### 阶段三：认证与用户

- [x] 登录接口。
- [x] 退出接口。
- [x] 当前用户接口。
- [ ] 用户分页、新增、修改、启用、禁用、重置密码。
- [ ] 工作人员分页、新增、修改、选项接口。
- [ ] 学生档案查询与维护。

### 阶段四：时间配置和值班

- [ ] 咨询室管理接口。
- [ ] 时间段管理接口。
- [ ] 值班分页接口。
- [ ] 值班新增/编辑接口。
- [ ] 值班冲突检测。
- [ ] 可预约时间段查询。
- [ ] 批量排班接口（可选）。

### 阶段五：学生首访预约

- [ ] 首访登记表提交接口。
- [ ] 风险评分服务。
- [ ] 知情同意状态查询。
- [ ] 知情同意签署。
- [ ] 初访预约提交。
- [ ] 我的预约分页。
- [ ] 学生撤销预约。
- [ ] 学生通知查询。

### 阶段六：管理员审核

- [ ] 初访预约审核列表。
- [ ] 预约详情接口。
- [ ] 审核通过。
- [ ] 驳回预约。
- [ ] 改约。
- [ ] 标记优先。
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

1. 多角色权限和数据归属校验较多，需在 Service 层严格控制。
2. 预约容量和时间冲突需要事务保证，不能只做前端校验。
3. 咨询安排涉及咨询师、学生和咨询室冲突，必须由后端统一校验。
4. 结案报告 Word 导出格式可能需要较多调试时间。
5. 统计接口依赖演示数据，需提前准备 `demo_data.sql`。
6. 若使用 Spring Boot 3，则团队环境需统一 JDK 17。

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
- `curl http://127.0.0.1:8080/api/health`
- `curl POST /api/auth/login`

### 遗留问题
- 目前认证使用内存临时账号，尚未接真实数据库和用户表。
- 业务模块、SQL 脚本和 Mapper 仍待继续实现。

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
```
