USE psychological_counseling;

-- 咨询师值班（staff_profile 咨询师 id=4）
INSERT INTO duty_schedule(staff_id, staff_type, duty_date, slot_id, room_id, capacity, reserved_count, status, create_time) VALUES
(4, 'COUNSELOR', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 1, 2, 0, 1, NOW()),
(4, 'COUNSELOR', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 1, 2, 0, 1, NOW()),
(4, 'COUNSELOR', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 4, 2, 2, 0, 1, NOW());

-- 首访登记与预约演示数据
INSERT INTO first_visit_form(student_id, main_problem, problem_description, expected_help,
    mood_score, sleep_score, stress_score, self_harm_flag, emergency_flag,
    risk_score, risk_level, form_status, submit_time, create_time) VALUES
(2, '近期学习压力较大', '最近考试和课程压力较大，睡眠不好。', '希望能获得压力调节建议。',
    6, 5, 8, 0, 0, 19, 'LOW', 'SUBMITTED', NOW(), NOW());

INSERT INTO consent_record(form_id, student_id, consent_version, sign_time, sign_ip, create_time) VALUES
(1, 2, 'v1.0', NOW(), '127.0.0.1', NOW());

INSERT INTO first_visit_appointment(appointment_no, student_id, form_id, interviewer_id, duty_schedule_id,
    appointment_date, slot_id, room_id, appointment_status, priority_flag, create_time) VALUES
('FV202606080001', 2, 1, 2, NULL, DATE_ADD(CURDATE(), INTERVAL -7 DAY), 1, 1, 'COMPLETED', 0, NOW());

INSERT INTO first_visit_result(appointment_id, interviewer_id, crisis_level, problem_type_id, interview_time,
    conclusion, summary, next_action, create_time) VALUES
(1, 2, 'MEDIUM', 1, DATE_ADD(CURDATE(), INTERVAL -7 DAY), 'ARRANGE_CONSULTATION',
    '学生主要表现为考试压力和睡眠问题。', '建议安排正式咨询。', NOW());

INSERT INTO consultation_queue(student_id, first_visit_result_id, problem_type_id, crisis_level,
    priority_score, queue_status, enqueue_time, create_time) VALUES
(2, 1, 1, 'MEDIUM', 500, 'WAITING', NOW(), NOW());

INSERT INTO first_visit_form(student_id, main_problem, problem_description, expected_help,
    mood_score, sleep_score, stress_score, self_harm_flag, emergency_flag,
    risk_score, risk_level, form_status, submit_time, create_time) VALUES
(2, '人际关系困扰', '与室友相处不融洽，情绪低落。', '希望改善人际关系。',
    7, 6, 7, 0, 0, 20, 'MEDIUM', 'SUBMITTED', NOW(), NOW());

INSERT INTO first_visit_appointment(appointment_no, student_id, form_id, interviewer_id,
    appointment_date, slot_id, room_id, appointment_status, priority_flag, create_time) VALUES
('FV202606080002', 2, 2, 2, DATE_ADD(CURDATE(), INTERVAL -5 DAY), 2, 1, 'COMPLETED', 1, NOW());

INSERT INTO first_visit_result(appointment_id, interviewer_id, crisis_level, problem_type_id, interview_time,
    conclusion, summary, next_action, create_time) VALUES
(2, 2, 'HIGH', 2, DATE_ADD(CURDATE(), INTERVAL -5 DAY), 'ARRANGE_CONSULTATION',
    '学生近期人际关系紧张，情绪波动明显。', '建议尽快安排正式咨询。', NOW());

INSERT INTO consultation_queue(student_id, first_visit_result_id, problem_type_id, crisis_level,
    priority_score, queue_status, enqueue_time, create_time) VALUES
(2, 2, 2, 'HIGH', 900, 'WAITING', NOW(), NOW());

INSERT INTO first_visit_form(student_id, main_problem, problem_description, expected_help,
    mood_score, sleep_score, stress_score, self_harm_flag, emergency_flag,
    risk_score, risk_level, form_status, submit_time, create_time) VALUES
(2, '情绪困扰严重', '近期持续焦虑，影响日常生活。', '需要专业心理支持。',
    8, 7, 9, 1, 0, 49, 'HIGH', 'SUBMITTED', NOW(), NOW());

INSERT INTO first_visit_appointment(appointment_no, student_id, form_id, interviewer_id,
    appointment_date, slot_id, room_id, appointment_status, priority_flag, create_time) VALUES
('FV202606080003', 2, 3, 2, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 3, 2, 'COMPLETED', 0, NOW());

INSERT INTO first_visit_result(appointment_id, interviewer_id, crisis_level, problem_type_id, interview_time,
    conclusion, summary, next_action, create_time) VALUES
(3, 2, 'URGENT', 3, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 'ARRANGE_CONSULTATION',
    '学生存在明显情绪困扰，需优先安排咨询。', '建议尽快安排正式咨询。', NOW());

INSERT INTO consultation_queue(student_id, first_visit_result_id, problem_type_id, crisis_level,
    priority_score, queue_status, enqueue_time, create_time) VALUES
(2, 3, 3, 'URGENT', 1000, 'WAITING', NOW(), NOW());

-- 演示咨询安排（供咨询师阶段5接口测试，咨询师 staff_profile id=4）
INSERT INTO consultation_schedule(
    schedule_no, queue_id, student_id, counselor_id, assistant_id,
    consultation_date, slot_id, room_id, session_no, source_type, schedule_status, create_time
) VALUES
('CS20260615001', 1, 2, 4, 3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 1, 1, 'QUEUE', 'RESERVED', NOW()),
('CS20260618001', 2, 2, 4, 3, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 4, 2, 1, 'QUEUE', 'RESERVED', NOW());
