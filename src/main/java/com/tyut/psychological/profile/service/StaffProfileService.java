package com.tyut.psychological.profile.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.util.PasswordUtils;
import com.tyut.psychological.common.vo.OptionVO;
import com.tyut.psychological.profile.dto.StaffQuery;
import com.tyut.psychological.profile.dto.StaffSaveRequest;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.mapper.StaffProfileMapper;
import com.tyut.psychological.profile.vo.StaffVO;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffProfileService {
    private final StaffProfileMapper staffProfileMapper;
    private final UserMapper userMapper;
    private final String defaultPassword;

    public StaffProfileService(StaffProfileMapper staffProfileMapper, UserMapper userMapper,
                               @Value("${app.default-password:123456}") String defaultPassword) {
        this.staffProfileMapper = staffProfileMapper;
        this.userMapper = userMapper;
        this.defaultPassword = defaultPassword;
    }

    // 分页查询工作人员，关联用户表获取姓名和手机号
    public PageResult<StaffVO> pageStaff(StaffQuery query) {
        List<StaffVO> records = staffProfileMapper.pageStaff(query);
        long total = staffProfileMapper.countStaff(query);
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageResult<>(records, total, query.getPageNum(), query.getPageSize(), pages);
    }

    // 新增工作人员，自动创建关联用户
    @Transactional
    public Long createStaff(StaffSaveRequest request) {
        String realName = trimToNull(request.getRealName());
        if (realName == null) {
            throw new BusinessException(400, "姓名不能为空");
        }

        String username = trimToNull(request.getUsername());
        String phone = trimToNull(request.getPhone());
        String staffType = trimToNull(request.getStaffType());
        Long userId = request.getUserId();

        if (userId == null) {
            if (username == null) {
                throw new BusinessException(400, "用户名不能为空");
            }
            SysUser existing = userMapper.selectByUsername(username);
            if (existing != null) {
                throw new BusinessException(400, "用户名已存在");
            }
            SysUser user = new SysUser();
            user.setUsername(username);
            user.setRealName(realName);
            user.setPhone(phone);
            user.setPasswordHash(PasswordUtils.hash(defaultPassword));
            user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
            userMapper.insert(user);
            userId = user.getId();
        } else {
            SysUser linkedUser = userMapper.selectById(userId);
            if (linkedUser == null) {
                throw new BusinessException(404, "关联用户不存在");
            }
            syncLinkedUser(userId, realName, phone, request.getStatus());
        }

        syncStaffUserRole(userId, null, staffType);

        StaffProfile profile = new StaffProfile();
        profile.setUserId(userId);
        profile.setStaffNo(trimToNull(request.getStaffNo()));
        profile.setStaffType(staffType);
        profile.setTitle(trimToNull(request.getTitle()));
        profile.setSpecialty(trimToNull(request.getSpecialty()));
        profile.setIntroduction(trimToNull(request.getIntroduction()));
        profile.setMaxDailyAppointments(request.getMaxDailyAppointments() != null ? request.getMaxDailyAppointments() : 6);
        profile.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        staffProfileMapper.insert(profile);
        return profile.getId();
    }

    // 修改工作人员档案
    @Transactional
    public void updateStaff(Long id, StaffSaveRequest request) {
        StaffProfile existing = staffProfileMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "工作人员不存在");
        }

        String realName = trimToNull(request.getRealName());
        if (realName == null) {
            throw new BusinessException(400, "姓名不能为空");
        }
        String phone = trimToNull(request.getPhone());
        String staffType = trimToNull(request.getStaffType());

        StaffProfile profile = new StaffProfile();
        profile.setId(id);
        profile.setStaffNo(trimToNull(request.getStaffNo()));
        profile.setStaffType(staffType);
        profile.setTitle(trimToNull(request.getTitle()));
        profile.setSpecialty(trimToNull(request.getSpecialty()));
        profile.setIntroduction(trimToNull(request.getIntroduction()));
        profile.setMaxDailyAppointments(request.getMaxDailyAppointments());
        profile.setStatus(request.getStatus());
        staffProfileMapper.update(profile);

        if (existing.getUserId() != null) {
            syncLinkedUser(existing.getUserId(), realName, phone, request.getStatus());
            syncStaffUserRole(existing.getUserId(), existing.getStaffType(), staffType);
        }
    }

    // 获取工作人员下拉选项，可按类型筛选
    public List<OptionVO> getStaffOptions(String staffType) {
        return staffProfileMapper.selectOptions(staffType);
    }

    // 根据ID获取工作人员（必须存在）
    public StaffProfile getRequired(Long id) {
        StaffProfile profile = staffProfileMapper.selectById(id);
        if (profile == null) {
            throw new BusinessException(404, "工作人员不存在");
        }
        return profile;
    }

    private void syncLinkedUser(Long userId, String realName, String phone, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setRealName(realName);
        user.setPhone(phone);
        if (status != null) {
            user.setStatus(status);
        }
        userMapper.update(user);
    }

    private void syncStaffUserRole(Long userId, String oldStaffType, String newStaffType) {
        if (userId == null) {
            return;
        }
        if (oldStaffType != null && !oldStaffType.equals(newStaffType)) {
            userMapper.deleteUserRoleByRoleCode(userId, oldStaffType);
        }
        if (newStaffType == null) {
            return;
        }
        List<String> roleCodes = userMapper.selectRoleCodesByUserId(userId);
        if (!roleCodes.contains(newStaffType)) {
            Long roleId = userMapper.selectRoleIdByCode(newStaffType);
            if (roleId != null) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
