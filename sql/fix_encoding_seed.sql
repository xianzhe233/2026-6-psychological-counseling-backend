-- 修复 Windows 下用 GBK 客户端导入导致的问号乱码（仅修复种子数据）
USE psychological_counseling;

SET NAMES utf8mb4;

UPDATE sys_role SET role_name = '学生', description = '学生用户' WHERE role_code = 'STUDENT';
UPDATE sys_role SET role_name = '中心管理员', description = '中心管理员' WHERE role_code = 'ADMIN';
UPDATE sys_role SET role_name = '初访员', description = '初访员' WHERE role_code = 'INTERVIEWER';
UPDATE sys_role SET role_name = '心理助理', description = '心理助理' WHERE role_code = 'ASSISTANT';
UPDATE sys_role SET role_name = '咨询师', description = '咨询师' WHERE role_code = 'COUNSELOR';

UPDATE sys_user SET real_name = '中心管理员' WHERE username = 'admin';
UPDATE sys_user SET real_name = '学生示例' WHERE username = '20230001';
UPDATE sys_user SET real_name = '初访员示例' WHERE username = 'interviewer';
UPDATE sys_user SET real_name = '心理助理示例' WHERE username = 'assistant';
UPDATE sys_user SET real_name = '咨询师示例' WHERE username = 'counselor';

UPDATE student_profile SET
  gender = '男',
  college = '计算机科学与技术学院',
  major = '软件工程',
  grade = '2023级',
  class_name = '软件2301班'
WHERE student_no = '20230001';

UPDATE staff_profile SET title = '中心主任', specialty = '心理危机干预' WHERE staff_no = 'A001';
UPDATE staff_profile SET title = '初访员', specialty = '情绪压力、人际关系' WHERE staff_no = 'I001';
UPDATE staff_profile SET title = '心理助理', specialty = '咨询安排协调' WHERE staff_no = 'AS001';
UPDATE staff_profile SET title = '讲师', specialty = '学业压力、人际关系、情绪困扰' WHERE staff_no = 'C001';

UPDATE time_slot SET slot_name = '上午第一段' WHERE id = 1;
UPDATE time_slot SET slot_name = '上午第二段' WHERE id = 2;
UPDATE time_slot SET slot_name = '上午第三段' WHERE id = 3;
UPDATE time_slot SET slot_name = '下午第一段' WHERE id = 4;
UPDATE time_slot SET slot_name = '下午第二段' WHERE id = 5;
UPDATE time_slot SET slot_name = '下午第三段' WHERE id = 6;

UPDATE counseling_room SET room_name = '心理咨询室A', location = '心理中心三层301', remark = '安静独立房间' WHERE id = 1;
UPDATE counseling_room SET room_name = '心理咨询室B', location = '心理中心三层302', remark = '安静独立房间' WHERE id = 2;
UPDATE counseling_room SET room_name = '团体咨询室', location = '心理中心三层305', remark = '团体活动使用' WHERE id = 3;

UPDATE problem_type SET type_name = '学业压力' WHERE sort_order = 1;
UPDATE problem_type SET type_name = '人际关系' WHERE sort_order = 2;
UPDATE problem_type SET type_name = '情绪困扰' WHERE sort_order = 3;
UPDATE problem_type SET type_name = '恋爱问题' WHERE sort_order = 4;
UPDATE problem_type SET type_name = '家庭关系' WHERE sort_order = 5;
UPDATE problem_type SET type_name = '职业规划' WHERE sort_order = 6;
UPDATE problem_type SET type_name = '适应问题' WHERE sort_order = 7;
UPDATE problem_type SET type_name = '其他' WHERE sort_order = 99;

UPDATE system_config SET description = '默认密码' WHERE config_key = 'default.password';
UPDATE system_config SET description = '知情同意书版本' WHERE config_key = 'consent.version';
UPDATE system_config SET description = '中风险阈值' WHERE config_key = 'risk.medium.threshold';
UPDATE system_config SET description = '高风险阈值' WHERE config_key = 'risk.high.threshold';
UPDATE system_config SET description = '紧急风险阈值' WHERE config_key = 'risk.urgent.threshold';
