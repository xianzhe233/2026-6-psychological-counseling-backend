package com.tyut.psychological.student.service;

import com.tyut.psychological.auth.vo.CurrentUserVO;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.student.dto.ConsentSignRequest;
import com.tyut.psychological.student.dto.FirstVisitFormSaveRequest;
import com.tyut.psychological.student.entity.ConsentRecord;
import com.tyut.psychological.student.entity.FirstVisitForm;
import com.tyut.psychological.student.mapper.StudentFormMapper;
import com.tyut.psychological.student.vo.ConsentStatusVO;
import com.tyut.psychological.student.vo.FirstVisitFormVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StudentFormService {
    private final StudentFormMapper studentFormMapper;

    public StudentFormService(StudentFormMapper studentFormMapper) {
        this.studentFormMapper = studentFormMapper;
    }

    public FirstVisitFormVO getLatestForm(CurrentUserVO user) {
        requireStudent(user);
        FirstVisitForm form = studentFormMapper.selectLatestByStudentId(user.getId());
        return form == null ? null : toVO(form);
    }

    @Transactional
    public FirstVisitFormVO saveForm(CurrentUserVO user, FirstVisitFormSaveRequest request) {
        requireStudent(user);
        FirstVisitForm form = new FirstVisitForm();
        form.setStudentId(user.getId());
        form.setMainProblem(trim(request.getMainProblem()));
        form.setProblemDescription(trim(request.getProblemDescription()));
        form.setExpectedHelp(trim(request.getExpectedHelp()));
        form.setMoodScore(request.getMoodScore());
        form.setSleepScore(request.getSleepScore());
        form.setStressScore(request.getStressScore());
        form.setSelfHarmFlag(request.getSelfHarmFlag());
        form.setEmergencyFlag(request.getEmergencyFlag());
        int riskScore = calculateRiskScore(request);
        form.setRiskScore(riskScore);
        form.setRiskLevel(calculateRiskLevel(riskScore));
        form.setFormStatus("SUBMITTED");
        form.setSubmitTime(LocalDateTime.now());
        studentFormMapper.insertFirstVisitForm(form);
        return toVO(studentFormMapper.selectById(form.getId()));
    }

    public ConsentStatusVO getConsentStatus(CurrentUserVO user, Long formId) {
        requireStudent(user);
        FirstVisitForm form = getOwnedForm(user, formId);
        ConsentRecord record = studentFormMapper.selectConsentByFormId(form.getId());
        ConsentStatusVO vo = new ConsentStatusVO();
        vo.setFormId(form.getId());
        if (record == null) {
            vo.setSigned(false);
            vo.setSignTime(null);
            vo.setConsentVersion(null);
        } else {
            vo.setSigned(record.getSigned() != null && record.getSigned() == 1);
            vo.setSignTime(record.getSignTime());
            vo.setConsentVersion(record.getConsentVersion());
        }
        return vo;
    }

    @Transactional
    public void signConsent(CurrentUserVO user, ConsentSignRequest request, String signIp) {
        requireStudent(user);
        FirstVisitForm form = getOwnedForm(user, request.getFormId());
        ConsentRecord existing = studentFormMapper.selectConsentByFormId(form.getId());
        if (existing != null) {
            return;
        }
        ConsentRecord record = new ConsentRecord();
        record.setFormId(form.getId());
        record.setStudentId(user.getId());
        record.setConsentVersion(trim(request.getConsentVersion()));
        record.setSigned(1);
        record.setSignTime(LocalDateTime.now());
        record.setSignIp(signIp);
        studentFormMapper.insertConsentRecord(record);
    }

    private void requireStudent(CurrentUserVO user) {
        if (user == null || user.getRoles() == null || !user.getRoles().contains(RoleCode.STUDENT)) {
            throw new BusinessException(403, "当前角色无权访问");
        }
    }

    private FirstVisitForm getOwnedForm(CurrentUserVO user, Long formId) {
        FirstVisitForm form = studentFormMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException(404, "首访登记表不存在");
        }
        if (!user.getId().equals(form.getStudentId())) {
            throw new BusinessException(403, "不能访问其他学生的登记表");
        }
        return form;
    }

    private int calculateRiskScore(FirstVisitFormSaveRequest request) {
        return request.getMoodScore() * 2
                + request.getSleepScore()
                + request.getStressScore() * 2
                + request.getSelfHarmFlag() * 40
                + request.getEmergencyFlag() * 50;
    }

    private String calculateRiskLevel(int score) {
        if (score >= 70) return "URGENT";
        if (score >= 40) return "HIGH";
        if (score >= 20) return "MEDIUM";
        return "LOW";
    }

    private FirstVisitFormVO toVO(FirstVisitForm form) {
        FirstVisitFormVO vo = new FirstVisitFormVO();
        vo.setId(form.getId());
        vo.setStudentId(form.getStudentId());
        vo.setMainProblem(form.getMainProblem());
        vo.setProblemDescription(form.getProblemDescription());
        vo.setExpectedHelp(form.getExpectedHelp());
        vo.setMoodScore(form.getMoodScore());
        vo.setSleepScore(form.getSleepScore());
        vo.setStressScore(form.getStressScore());
        vo.setSelfHarmFlag(form.getSelfHarmFlag());
        vo.setEmergencyFlag(form.getEmergencyFlag());
        vo.setRiskScore(form.getRiskScore());
        vo.setRiskLevel(form.getRiskLevel());
        vo.setFormStatus(form.getFormStatus());
        vo.setSubmitTime(form.getSubmitTime());
        vo.setCreateTime(form.getCreateTime());
        vo.setUpdateTime(form.getUpdateTime());
        return vo;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
