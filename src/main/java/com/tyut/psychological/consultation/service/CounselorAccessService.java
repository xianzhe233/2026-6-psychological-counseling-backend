package com.tyut.psychological.consultation.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import org.springframework.stereotype.Service;

@Service
public class CounselorAccessService {
    private final StaffProfileMapper staffProfileMapper;

    public CounselorAccessService(StaffProfileMapper staffProfileMapper) {
        this.staffProfileMapper = staffProfileMapper;
    }

    public StaffProfile requireCounselorStaff(Long counselorUserId) {
        StaffProfile counselor = staffProfileMapper.selectByUserId(counselorUserId);
        if (counselor == null) {
            throw new BusinessException(400, "咨询师信息无效");
        }
        if (!"COUNSELOR".equals(counselor.getStaffType())) {
            throw new BusinessException(400, "当前用户不是咨询师");
        }
        if (counselor.getStatus() != 1) {
            throw new BusinessException(400, "咨询师未启用");
        }
        return counselor;
    }
}
