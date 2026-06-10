USE psychological_counseling;

INSERT INTO first_visit_form(student_id, main_problem, problem_description, expected_help,
    mood_score, sleep_score, stress_score, self_harm_flag, emergency_flag,
    risk_score, risk_level, form_status, submit_time, create_time) VALUES
(2, 'study pressure', 'exam stress and sleep issues', 'need stress relief',
    6, 5, 8, 0, 0, 19, 'LOW', 'SUBMITTED', NOW(), NOW());

INSERT INTO consent_record(form_id, student_id, consent_version, sign_time, sign_ip, create_time) VALUES
(1, 2, 'v1.0', NOW(), '127.0.0.1', NOW());

INSERT INTO first_visit_appointment(appointment_no, student_id, form_id, interviewer_id, duty_schedule_id,
    appointment_date, slot_id, room_id, appointment_status, priority_flag, create_time) VALUES
('FV202606080001', 2, 1, 2, NULL, DATE_ADD(CURDATE(), INTERVAL -7 DAY), 1, 1, 'COMPLETED', 0, NOW());

INSERT INTO first_visit_result(appointment_id, interviewer_id, crisis_level, problem_type_id, interview_time,
    conclusion, summary, next_action, create_time) VALUES
(1, 2, 'MEDIUM', 1, DATE_ADD(CURDATE(), INTERVAL -7 DAY), 'ARRANGE_CONSULTATION',
    'exam pressure and sleep issues', 'arrange formal consultation', NOW());

INSERT INTO consultation_queue(student_id, first_visit_result_id, problem_type_id, crisis_level,
    priority_score, queue_status, enqueue_time, create_time) VALUES
(2, 1, 1, 'MEDIUM', 500, 'WAITING', NOW(), NOW());

INSERT INTO first_visit_form(student_id, main_problem, problem_description, expected_help,
    mood_score, sleep_score, stress_score, self_harm_flag, emergency_flag,
    risk_score, risk_level, form_status, submit_time, create_time) VALUES
(2, 'relationship issue', 'roommate conflict', 'improve relationships',
    7, 6, 7, 0, 0, 20, 'MEDIUM', 'SUBMITTED', NOW(), NOW());

INSERT INTO first_visit_appointment(appointment_no, student_id, form_id, interviewer_id,
    appointment_date, slot_id, room_id, appointment_status, priority_flag, create_time) VALUES
('FV202606080002', 2, 2, 2, DATE_ADD(CURDATE(), INTERVAL -5 DAY), 2, 1, 'COMPLETED', 1, NOW());

INSERT INTO first_visit_result(appointment_id, interviewer_id, crisis_level, problem_type_id, interview_time,
    conclusion, summary, next_action, create_time) VALUES
(2, 2, 'HIGH', 2, DATE_ADD(CURDATE(), INTERVAL -5 DAY), 'ARRANGE_CONSULTATION',
    'relationship tension', 'arrange consultation soon', NOW());

INSERT INTO consultation_queue(student_id, first_visit_result_id, problem_type_id, crisis_level,
    priority_score, queue_status, enqueue_time, create_time) VALUES
(2, 2, 2, 'HIGH', 900, 'WAITING', NOW(), NOW());

INSERT INTO first_visit_form(student_id, main_problem, problem_description, expected_help,
    mood_score, sleep_score, stress_score, self_harm_flag, emergency_flag,
    risk_score, risk_level, form_status, submit_time, create_time) VALUES
(2, 'emotional distress', 'ongoing anxiety', 'professional support',
    8, 7, 9, 1, 0, 49, 'HIGH', 'SUBMITTED', NOW(), NOW());

INSERT INTO first_visit_appointment(appointment_no, student_id, form_id, interviewer_id,
    appointment_date, slot_id, room_id, appointment_status, priority_flag, create_time) VALUES
('FV202606080003', 2, 3, 2, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 3, 2, 'COMPLETED', 0, NOW());

INSERT INTO first_visit_result(appointment_id, interviewer_id, crisis_level, problem_type_id, interview_time,
    conclusion, summary, next_action, create_time) VALUES
(3, 2, 'URGENT', 3, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 'ARRANGE_CONSULTATION',
    'urgent emotional distress', 'priority consultation', NOW());

INSERT INTO consultation_queue(student_id, first_visit_result_id, problem_type_id, crisis_level,
    priority_score, queue_status, enqueue_time, create_time) VALUES
(2, 3, 3, 'URGENT', 1000, 'WAITING', NOW(), NOW());
