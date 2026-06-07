-- 心理咨询系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS psychological_counseling DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE psychological_counseling;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    last_login_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

-- 学生档案表
CREATE TABLE IF NOT EXISTS student_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    student_no VARCHAR(50),
    gender TINYINT COMMENT '0-女 1-男',
    age INT,
    college VARCHAR(100),
    major VARCHAR(100),
    grade VARCHAR(20),
    class_name VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 工作人员表
CREATE TABLE IF NOT EXISTS staff (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    staff_no VARCHAR(50),
    staff_type VARCHAR(20) NOT NULL COMMENT 'INTERVIEWER-初访员 COUNSELOR-咨询师 ASSISTANT-心理助理',
    title VARCHAR(50),
    specialty VARCHAR(200),
    introduction TEXT,
    max_daily_appointments INT DEFAULT 6,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 咨询室表
CREATE TABLE IF NOT EXISTS consultation_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    capacity INT DEFAULT 1,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 时间段表
CREATE TABLE IF NOT EXISTS time_slot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    slot_name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    interval_minutes INT DEFAULT 10,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 值班安排表
CREATE TABLE IF NOT EXISTS duty_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    staff_id BIGINT NOT NULL,
    staff_type VARCHAR(20) NOT NULL,
    duty_date DATE NOT NULL,
    slot_id BIGINT NOT NULL,
    slot_name VARCHAR(50),
    start_time TIME,
    end_time TIME,
    room_id BIGINT,
    room_name VARCHAR(100),
    capacity INT DEFAULT 1,
    reserved_count INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_staff_date_slot (staff_id, duty_date, slot_id)
);

-- 首访登记表
CREATE TABLE IF NOT EXISTS first_visit_form (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    main_problem VARCHAR(200) NOT NULL,
    problem_description TEXT NOT NULL,
    expected_help TEXT NOT NULL,
    mood_score INT NOT NULL,
    sleep_score INT NOT NULL,
    stress_score INT NOT NULL,
    self_harm_flag TINYINT DEFAULT 0 COMMENT '0-无 1-有',
    emergency_flag TINYINT DEFAULT 0 COMMENT '0-否 1-是',
    risk_score INT,
    risk_level VARCHAR(20) COMMENT 'LOW MEDIUM HIGH URGENT',
    form_status VARCHAR(20) DEFAULT 'DRAFT' COMMENT 'DRAFT SUBMITTED',
    submit_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 知情同意书记录表
CREATE TABLE IF NOT EXISTS consent_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    consent_version VARCHAR(20) NOT NULL,
    sign_time DATETIME NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_form_id (form_id)
);

-- 初访预约表
CREATE TABLE IF NOT EXISTS first_visit_appointment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_no VARCHAR(50) NOT NULL UNIQUE,
    form_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    duty_schedule_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    slot_id BIGINT NOT NULL,
    slot_name VARCHAR(50),
    start_time TIME,
    end_time TIME,
    interviewer_id BIGINT,
    interviewer_name VARCHAR(50),
    room_id BIGINT,
    room_name VARCHAR(100),
    appointment_status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING APPROVED REJECTED CANCELED COMPLETED',
    audit_remark VARCHAR(500),
    reject_reason VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 通知日志表
CREATE TABLE IF NOT EXISTS notification_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    notify_type VARCHAR(50) COMMENT 'SYSTEM APPOINTMENT RESULT',
    read_status TINYINT DEFAULT 0 COMMENT '0-未读 1-已读',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 插入初始数据

-- 插入角色
INSERT INTO sys_role (role_code, role_name, description) VALUES
('ADMIN', '管理员', '系统管理员'),
('STUDENT', '学生', '心理咨询学生'),
('INTERVIEWER', '初访员', '初访接待人员'),
('COUNSELOR', '咨询师', '心理咨询师'),
('ASSISTANT', '心理助理', '心理助理人员');

-- 插入管理员用户（密码：123456）
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '中心管理员', '13800000000', 1);

-- 插入管理员角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 插入测试学生用户（密码：123456）
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '13800000001', 1);

-- 插入学生角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);

-- 插入学生档案
INSERT INTO student_profile (user_id, student_no, gender, age, college, major, grade, class_name) VALUES
(2, '20230001', 1, 20, '计算机科学与技术学院', '软件工程', '2023级', '软件2301');

-- 插入时间段
INSERT INTO time_slot (slot_name, start_time, end_time, interval_minutes) VALUES
('上午第一段', '08:30:00', '09:20:00', 10),
('上午第二段', '09:30:00', '10:20:00', 10),
('上午第三段', '10:30:00', '11:20:00', 10),
('下午第一段', '14:00:00', '14:50:00', 10),
('下午第二段', '15:00:00', '15:50:00', 10),
('下午第三段', '16:00:00', '16:50:00', 10);

-- 插入咨询室
INSERT INTO consultation_room (room_name, location, capacity, status) VALUES
('心理咨询室A', '心理中心三层301', 1, 1),
('心理咨询室B', '心理中心三层302', 1, 1),
('心理咨询室C', '心理中心三层303', 1, 1);

-- 插入初访员
INSERT INTO staff (user_id, staff_no, staff_type, title, specialty, max_daily_appointments, status) VALUES
(1, 'IV001', 'INTERVIEWER', '初访员', '情绪压力、人际关系', 6, 1);