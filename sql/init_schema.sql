-- ============================================================
-- 高校心理咨询预约与个案管理系统 - 建表脚本
-- 数据库：MySQL 8.0 | 字符集：utf8mb4 | 引擎：InnoDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS psychological_counseling
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE psychological_counseling;

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username        VARCHAR(50)  NOT NULL COMMENT '登录名',
    password_hash   VARCHAR(100) NOT NULL COMMENT '密码摘要',
    real_name       VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    phone           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar_url      VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
    last_login_time DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_phone (phone)
) ENGINE=InnoDB COMMENT='系统用户表';

-- 2. 系统角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    role_code   VARCHAR(30)  NOT NULL COMMENT '角色编码',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    description VARCHAR(200) DEFAULT NULL COMMENT '描述',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB COMMENT='系统角色表';

-- 3. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    role_id     BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_role_role (role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

-- 4. 学生档案表
CREATE TABLE IF NOT EXISTS student_profile (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id           BIGINT      NOT NULL COMMENT '关联用户ID',
    student_no        VARCHAR(30) NOT NULL COMMENT '学号',
    gender            VARCHAR(10) DEFAULT NULL COMMENT '性别',
    college           VARCHAR(100) DEFAULT NULL COMMENT '学院',
    major             VARCHAR(100) DEFAULT NULL COMMENT '专业',
    grade             VARCHAR(20) DEFAULT NULL COMMENT '年级',
    class_name        VARCHAR(50) DEFAULT NULL COMMENT '班级',
    contact_phone     VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    emergency_contact VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    emergency_phone   VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    create_time       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_profile_user (user_id),
    UNIQUE KEY uk_student_profile_no (student_no)
) ENGINE=InnoDB COMMENT='学生档案表';

-- 5. 工作人员档案表
CREATE TABLE IF NOT EXISTS staff_profile (
    id                     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id                BIGINT      NOT NULL COMMENT '关联用户ID',
    staff_no               VARCHAR(30) DEFAULT NULL COMMENT '工号',
    staff_type             VARCHAR(20) NOT NULL COMMENT 'ADMIN/INTERVIEWER/ASSISTANT/COUNSELOR',
    title                  VARCHAR(50) DEFAULT NULL COMMENT '职称',
    specialty              VARCHAR(200) DEFAULT NULL COMMENT '擅长方向',
    introduction           TEXT         DEFAULT NULL COMMENT '简介',
    max_daily_appointments INT          DEFAULT 6 COMMENT '每日最大预约量',
    status                 TINYINT      NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
    create_time            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_staff_profile_user (user_id),
    KEY idx_staff_profile_type (staff_type)
) ENGINE=InnoDB COMMENT='工作人员档案表';

-- 6. 咨询室表
CREATE TABLE IF NOT EXISTS counseling_room (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    room_name   VARCHAR(100) NOT NULL COMMENT '咨询室名称',
    location    VARCHAR(200) DEFAULT NULL COMMENT '地点',
    capacity    INT          NOT NULL DEFAULT 1 COMMENT '容量',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='咨询室表';

-- 7. 时间段表
CREATE TABLE IF NOT EXISTS time_slot (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    slot_name        VARCHAR(50)  NOT NULL COMMENT '时间段名称',
    start_time       TIME         NOT NULL COMMENT '开始时间',
    end_time         TIME         NOT NULL COMMENT '结束时间',
    interval_minutes INT          DEFAULT 10 COMMENT '间隔分钟',
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='时间段表';

-- 8. 值班安排表
CREATE TABLE IF NOT EXISTS duty_schedule (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    staff_id       BIGINT      NOT NULL COMMENT '工作人员ID',
    staff_type     VARCHAR(20) NOT NULL COMMENT 'INTERVIEWER/COUNSELOR',
    duty_date      DATE        NOT NULL COMMENT '值班日期',
    slot_id        BIGINT      NOT NULL COMMENT '时间段ID',
    room_id        BIGINT      DEFAULT NULL COMMENT '咨询室ID',
    capacity       INT         NOT NULL DEFAULT 1 COMMENT '可预约容量',
    reserved_count INT         NOT NULL DEFAULT 0 COMMENT '已预约数量',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
    create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_duty_staff_date_slot (staff_id, duty_date, slot_id),
    KEY idx_duty_date_type (duty_date, staff_type),
    KEY idx_duty_room_date_slot (room_id, duty_date, slot_id)
) ENGINE=InnoDB COMMENT='值班安排表';

-- 9. 问题类型表
CREATE TABLE IF NOT EXISTS problem_type (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    type_name   VARCHAR(50) NOT NULL COMMENT '类型名称',
    sort_order  INT         DEFAULT 0 COMMENT '排序',
    status      TINYINT     NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='问题类型表';

-- 10. 首访登记表
CREATE TABLE IF NOT EXISTS first_visit_form (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id         BIGINT      NOT NULL COMMENT '学生用户ID',
    main_problem       VARCHAR(200) DEFAULT NULL COMMENT '主要困扰',
    problem_description TEXT        DEFAULT NULL COMMENT '问题详细描述',
    expected_help      TEXT         DEFAULT NULL COMMENT '希望获得的帮助',
    mood_score         INT          DEFAULT 0 COMMENT '情绪困扰分数 0-10',
    sleep_score        INT          DEFAULT 0 COMMENT '睡眠困扰分数 0-10',
    stress_score       INT          DEFAULT 0 COMMENT '压力分数 0-10',
    self_harm_flag     TINYINT      DEFAULT 0 COMMENT '0无 1有自伤想法',
    emergency_flag     TINYINT      DEFAULT 0 COMMENT '0无 1需紧急帮助',
    risk_score         INT          DEFAULT 0 COMMENT '风险总分',
    risk_level         VARCHAR(20)  DEFAULT 'LOW' COMMENT 'LOW/MEDIUM/HIGH/URGENT',
    form_status        VARCHAR(20)  DEFAULT 'DRAFT' COMMENT 'DRAFT/SUBMITTED',
    submit_time        DATETIME     DEFAULT NULL COMMENT '提交时间',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_first_visit_form_student (student_id),
    KEY idx_first_visit_form_risk (risk_level)
) ENGINE=InnoDB COMMENT='首访登记表';

-- 11. 知情同意记录表
CREATE TABLE IF NOT EXISTS consent_record (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    form_id          BIGINT      NOT NULL COMMENT '登记表ID',
    student_id       BIGINT      NOT NULL COMMENT '学生用户ID',
    consent_version  VARCHAR(20) NOT NULL DEFAULT 'v1.0' COMMENT '同意书版本',
    signed           TINYINT     NOT NULL DEFAULT 1 COMMENT '是否签署',
    sign_time        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签署时间',
    sign_ip          VARCHAR(50) DEFAULT NULL COMMENT '签署IP',
    create_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_consent_form (form_id)
) ENGINE=InnoDB COMMENT='知情同意记录表';

-- 12. 初访预约表
CREATE TABLE IF NOT EXISTS first_visit_appointment (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    appointment_no   VARCHAR(30)  NOT NULL COMMENT '预约编号',
    student_id       BIGINT       NOT NULL COMMENT '学生用户ID',
    form_id          BIGINT       NOT NULL COMMENT '登记表ID',
    interviewer_id   BIGINT       DEFAULT NULL COMMENT '初访员staff_profile ID',
    duty_schedule_id BIGINT       DEFAULT NULL COMMENT '值班安排ID',
    appointment_date DATE         DEFAULT NULL COMMENT '预约日期',
    slot_id          BIGINT       DEFAULT NULL COMMENT '时间段ID',
    room_id          BIGINT       DEFAULT NULL COMMENT '咨询室ID',
    appointment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/CANCELED/COMPLETED',
    priority_flag    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否优先',
    audit_admin_id   BIGINT       DEFAULT NULL COMMENT '审核管理员ID',
    audit_time       DATETIME     DEFAULT NULL COMMENT '审核时间',
    audit_remark     VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    cancel_reason    VARCHAR(500) DEFAULT NULL COMMENT '撤销/驳回原因',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_fv_appointment_no (appointment_no),
    KEY idx_fv_appointment_student_status (student_id, appointment_status),
    KEY idx_fv_appointment_status_risk (appointment_status, priority_flag),
    KEY idx_fv_appointment_date_slot (appointment_date, slot_id)
) ENGINE=InnoDB COMMENT='初访预约表';

-- 13. 初访结果表
CREATE TABLE IF NOT EXISTS first_visit_result (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    appointment_id   BIGINT      NOT NULL COMMENT '预约ID',
    interviewer_id   BIGINT      NOT NULL COMMENT '初访员staff_profile ID',
    crisis_level     VARCHAR(20) NOT NULL COMMENT 'LOW/MEDIUM/HIGH/URGENT',
    problem_type_id  BIGINT      DEFAULT NULL COMMENT '问题类型ID',
    interview_time   DATETIME    DEFAULT NULL COMMENT '初访时间',
    conclusion       VARCHAR(30) NOT NULL COMMENT 'NO_NEED/ARRANGE_CONSULTATION/TRANSFER',
    summary          TEXT        DEFAULT NULL COMMENT '初访摘要',
    next_action      TEXT        DEFAULT NULL COMMENT '后续建议',
    create_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_fv_result_appointment (appointment_id),
    KEY idx_fv_result_crisis (crisis_level),
    KEY idx_fv_result_problem (problem_type_id)
) ENGINE=InnoDB COMMENT='初访结果表';

-- 14. 咨询队列表
CREATE TABLE IF NOT EXISTS consultation_queue (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id           BIGINT      NOT NULL COMMENT '学生用户ID',
    first_visit_result_id BIGINT     NOT NULL COMMENT '初访结果ID',
    problem_type_id      BIGINT      DEFAULT NULL COMMENT '问题类型ID',
    crisis_level         VARCHAR(20) NOT NULL COMMENT '危机等级',
    priority_score       INT         NOT NULL DEFAULT 0 COMMENT '优先级分数',
    queue_status         VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/ARRANGED/SUSPENDED/CLOSED',
    enqueue_time         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入队时间',
    assigned_time        DATETIME    DEFAULT NULL COMMENT '安排时间',
    create_time          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_queue_status (queue_status),
    KEY idx_queue_priority (priority_score, enqueue_time)
) ENGINE=InnoDB COMMENT='咨询队列表';

-- 15. 咨询安排表
CREATE TABLE IF NOT EXISTS consultation_schedule (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    schedule_no        VARCHAR(30)  NOT NULL COMMENT '安排编号',
    queue_id           BIGINT       DEFAULT NULL COMMENT '队列ID',
    student_id         BIGINT       NOT NULL COMMENT '学生用户ID',
    counselor_id       BIGINT       NOT NULL COMMENT '咨询师staff_profile ID',
    assistant_id       BIGINT       DEFAULT NULL COMMENT '助理staff_profile ID',
    consultation_date  DATE         NOT NULL COMMENT '咨询日期',
    slot_id            BIGINT       NOT NULL COMMENT '时间段ID',
    room_id            BIGINT       DEFAULT NULL COMMENT '咨询室ID',
    session_no         INT          NOT NULL DEFAULT 1 COMMENT '第几次',
    source_type        VARCHAR(20)  DEFAULT 'QUEUE' COMMENT '来源 QUEUE/MANUAL',
    schedule_status    VARCHAR(20)  NOT NULL DEFAULT 'RESERVED' COMMENT '状态',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_schedule_no (schedule_no),
    KEY idx_schedule_student (student_id, consultation_date),
    KEY idx_schedule_counselor_time (counselor_id, consultation_date, slot_id),
    KEY idx_schedule_room_time (room_id, consultation_date, slot_id),
    KEY idx_schedule_status (schedule_status)
) ENGINE=InnoDB COMMENT='咨询安排表';

-- 16. 咨询记录表
CREATE TABLE IF NOT EXISTS consultation_record (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    schedule_id       BIGINT      NOT NULL COMMENT '咨询安排ID',
    student_id        BIGINT      NOT NULL COMMENT '学生用户ID',
    counselor_id      BIGINT      NOT NULL COMMENT '咨询师staff_profile ID',
    session_no        INT         DEFAULT 1 COMMENT '第几次',
    consultation_time DATETIME    DEFAULT NULL COMMENT '咨询时间',
    record_status     VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' COMMENT '记录状态',
    content_summary   TEXT        DEFAULT NULL COMMENT '内容摘要',
    next_plan         TEXT        DEFAULT NULL COMMENT '下次计划',
    need_close        TINYINT     DEFAULT 0 COMMENT '是否需要结案',
    create_time       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_record_schedule (schedule_id),
    KEY idx_record_student (student_id),
    KEY idx_record_counselor (counselor_id)
) ENGINE=InnoDB COMMENT='咨询记录表';

-- 17. 追加咨询申请表
CREATE TABLE IF NOT EXISTS extension_request (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id       BIGINT      NOT NULL COMMENT '学生用户ID',
    counselor_id     BIGINT      NOT NULL COMMENT '咨询师staff_profile ID',
    request_sessions INT         NOT NULL DEFAULT 1 COMMENT '申请追加次数',
    reason           TEXT        NOT NULL COMMENT '申请原因',
    request_status   VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    audit_admin_id   BIGINT      DEFAULT NULL COMMENT '审核管理员ID',
    audit_time       DATETIME    DEFAULT NULL COMMENT '审核时间',
    audit_remark     VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    create_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='追加咨询申请表';

-- 18. 结案报告表
CREATE TABLE IF NOT EXISTS case_report (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id          BIGINT       NOT NULL COMMENT '学生用户ID',
    counselor_id        BIGINT       NOT NULL COMMENT '咨询师staff_profile ID',
    problem_type_id     BIGINT       DEFAULT NULL COMMENT '问题类型ID',
    total_sessions      INT          NOT NULL DEFAULT 1 COMMENT '咨询总次数',
    effect_self_rating  VARCHAR(50)  DEFAULT NULL COMMENT '咨询效果自评',
    case_summary        TEXT         DEFAULT NULL COMMENT '个案总结',
    counseling_effect   TEXT         DEFAULT NULL COMMENT '咨询效果',
    suggestion          TEXT         DEFAULT NULL COMMENT '后续建议',
    close_type          VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '结案类型',
    report_status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/SUBMITTED',
    report_file_path    VARCHAR(500) DEFAULT NULL COMMENT '报告文件路径',
    submit_time         DATETIME     DEFAULT NULL COMMENT '提交时间',
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_report_student (student_id),
    KEY idx_report_counselor (counselor_id),
    KEY idx_report_problem (problem_type_id),
    KEY idx_report_status (report_status)
) ENGINE=InnoDB COMMENT='结案报告表';

-- 19. 通知日志表
CREATE TABLE IF NOT EXISTS notification_log (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    receiver_user_id BIGINT       NOT NULL COMMENT '接收人用户ID',
    receiver_name    VARCHAR(50)  DEFAULT NULL COMMENT '接收人姓名',
    phone            VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    notify_type      VARCHAR(30)  NOT NULL COMMENT '通知类型',
    title            VARCHAR(100) DEFAULT NULL COMMENT '标题',
    content          TEXT         DEFAULT NULL COMMENT '内容',
    send_status      VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS/FAILED',
    send_time        DATETIME     DEFAULT NULL COMMENT '发送时间',
    related_id       BIGINT       DEFAULT NULL COMMENT '关联业务ID',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='通知日志表';

-- 20. 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    operator_user_id BIGINT      NOT NULL COMMENT '操作人用户ID',
    operator_name   VARCHAR(50)  DEFAULT NULL COMMENT '操作人姓名',
    role_code       VARCHAR(20)  DEFAULT NULL COMMENT '角色编码',
    module_name     VARCHAR(50)  NOT NULL COMMENT '模块名称',
    operation_type  VARCHAR(30)  NOT NULL COMMENT '操作类型',
    operation_desc  VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
    request_url     VARCHAR(200) DEFAULT NULL COMMENT '请求URL',
    result_status   VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS/FAILED',
    error_message   TEXT         DEFAULT NULL COMMENT '错误信息',
    ip_address      VARCHAR(50)  DEFAULT NULL COMMENT 'IP地址',
    execution_time  BIGINT       DEFAULT NULL COMMENT '执行耗时ms',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='操作日志表';

-- 21. 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    config_key   VARCHAR(50)  NOT NULL COMMENT '配置键',
    config_value VARCHAR(200) NOT NULL COMMENT '配置值',
    description  VARCHAR(200) DEFAULT NULL COMMENT '描述',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB COMMENT='系统配置表';
