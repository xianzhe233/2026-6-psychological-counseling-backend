-- ============================================================
-- 高校心理咨询预约与个案管理系统 - 初始数据
-- ============================================================

USE psychological_counseling;

-- 1. 五类角色
INSERT INTO sys_role(role_code, role_name, description, create_time) VALUES
('STUDENT', '学生', '学生用户', NOW()),
('ADMIN', '中心管理员', '中心管理员', NOW()),
('INTERVIEWER', '初访员', '初访员', NOW()),
('ASSISTANT', '心理助理', '心理助理', NOW()),
('COUNSELOR', '咨询师', '咨询师', NOW());

-- 2. 管理员账号 (密码: 123456, SHA-256+salt 哈希)
INSERT INTO sys_user(username, password_hash, real_name, phone, status, create_time) VALUES
('admin', 'CDnYVT1weLjgrnBlR8f3D/6PyDmBb8ZnJB7fQuSsGRY=', '中心管理员', '13800000000', 1, NOW());

-- 3. 学生账号
INSERT INTO sys_user(username, password_hash, real_name, phone, status, create_time) VALUES
('20230001', 'CDnYVT1weLjgrnBlR8f3D/6PyDmBb8ZnJB7fQuSsGRY=', '学生示例', '13800000001', 1, NOW());

-- 4. 初访员账号
INSERT INTO sys_user(username, password_hash, real_name, phone, status, create_time) VALUES
('interviewer', 'CDnYVT1weLjgrnBlR8f3D/6PyDmBb8ZnJB7fQuSsGRY=', '初访员示例', '13800000002', 1, NOW());

-- 5. 心理助理账号
INSERT INTO sys_user(username, password_hash, real_name, phone, status, create_time) VALUES
('assistant', 'CDnYVT1weLjgrnBlR8f3D/6PyDmBb8ZnJB7fQuSsGRY=', '心理助理示例', '13800000003', 1, NOW());

-- 6. 咨询师账号
INSERT INTO sys_user(username, password_hash, real_name, phone, status, create_time) VALUES
('counselor', 'CDnYVT1weLjgrnBlR8f3D/6PyDmBb8ZnJB7fQuSsGRY=', '咨询师示例', '13800000004', 1, NOW());

-- 7. 分配角色 (user_id 对应上面插入的顺序: admin=1, student=2, interviewer=3, assistant=4, counselor=5)
INSERT INTO sys_user_role(user_id, role_id, create_time) VALUES
(1, (SELECT id FROM sys_role WHERE role_code='ADMIN'), NOW()),
(2, (SELECT id FROM sys_role WHERE role_code='STUDENT'), NOW()),
(3, (SELECT id FROM sys_role WHERE role_code='INTERVIEWER'), NOW()),
(4, (SELECT id FROM sys_role WHERE role_code='ASSISTANT'), NOW()),
(5, (SELECT id FROM sys_role WHERE role_code='COUNSELOR'), NOW());

-- 8. 学生档案
INSERT INTO student_profile(user_id, student_no, gender, college, major, grade, class_name, contact_phone, create_time) VALUES
(2, '20230001', '男', '计算机科学与技术学院', '软件工程', '2023级', '软件2301班', '13800000001', NOW());

-- 9. 工作人员档案
INSERT INTO staff_profile(user_id, staff_no, staff_type, title, specialty, max_daily_appointments, status, create_time) VALUES
(1, 'A001', 'ADMIN', '中心主任', '心理危机干预', 10, 1, NOW()),
(3, 'I001', 'INTERVIEWER', '初访员', '情绪压力、人际关系', 8, 1, NOW()),
(4, 'AS001', 'ASSISTANT', '心理助理', '咨询安排协调', 10, 1, NOW()),
(5, 'C001', 'COUNSELOR', '讲师', '学业压力、人际关系、情绪困扰', 6, 1, NOW());

-- 10. 基础时间段
INSERT INTO time_slot(slot_name, start_time, end_time, interval_minutes, status, create_time) VALUES
('上午第一段', '08:30:00', '09:20:00', 10, 1, NOW()),
('上午第二段', '09:30:00', '10:20:00', 10, 1, NOW()),
('上午第三段', '10:30:00', '11:20:00', 10, 1, NOW()),
('下午第一段', '14:00:00', '14:50:00', 10, 1, NOW()),
('下午第二段', '15:00:00', '15:50:00', 10, 1, NOW()),
('下午第三段', '16:00:00', '16:50:00', 10, 1, NOW());

-- 11. 咨询室
INSERT INTO counseling_room(room_name, location, capacity, status, remark, create_time) VALUES
('心理咨询室A', '心理中心三层301', 1, 1, '安静独立房间', NOW()),
('心理咨询室B', '心理中心三层302', 1, 1, '安静独立房间', NOW()),
('团体咨询室', '心理中心三层305', 8, 1, '团体活动使用', NOW());

-- 12. 问题类型
INSERT INTO problem_type(type_name, sort_order, status, create_time) VALUES
('学业压力', 1, 1, NOW()),
('人际关系', 2, 1, NOW()),
('情绪困扰', 3, 1, NOW()),
('恋爱问题', 4, 1, NOW()),
('家庭关系', 5, 1, NOW()),
('职业规划', 6, 1, NOW()),
('适应问题', 7, 1, NOW()),
('其他', 99, 1, NOW());

-- 13. 系统配置
INSERT INTO system_config(config_key, config_value, description, create_time) VALUES
('default.password', '123456', '默认密码', NOW()),
('consent.version', 'v1.0', '知情同意书版本', NOW()),
('risk.medium.threshold', '20', '中风险阈值', NOW()),
('risk.high.threshold', '40', '高风险阈值', NOW()),
('risk.urgent.threshold', '70', '紧急风险阈值', NOW());
