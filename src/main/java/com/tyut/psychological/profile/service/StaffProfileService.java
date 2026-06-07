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
        if (request.getRealName() == null || request.getRealName().isEmpty()) {
            throw new BusinessException(400, "姓名不能为空");
        }
        Long userId = request.getUserId();
        // 如果没有关联用户，自动创建
        if (userId == null) {
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                throw new BusinessException(400, "用户名不能为空");
            }
            SysUser existing = userMapper.selectByUsername(request.getUsername());
            if (existing != null) {
                throw new BusinessException(400, "用户名已存在");
            }
            SysUser user = new SysUser();
            user.setUsername(request.getUsername());
            user.setRealName(request.getRealName());
            user.setPhone(request.getPhone());
            user.setPasswordHash(PasswordUtils.hash(defaultPassword));
            user.setStatus(1);
            userMapper.insert(user);
            userId = user.getId();
            // 分配对应角色
            Long roleId = userMapper.selectRoleIdByCode(request.getStaffType());
            if (roleId != null) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
        // 创建工作人员档案
        StaffProfile profile = new StaffProfile();
        profile.setUserId(userId);
        profile.setStaffNo(request.getStaffNo());
        profile.setStaffType(request.getStaffType());
        profile.setTitle(request.getTitle());
        profile.setSpecialty(request.getSpecialty());
        profile.setIntroduction(request.getIntroduction());
        profile.setMaxDailyAppointments(request.getMaxDailyAppointments() != null ? request.getMaxDailyAppointments() : 6);
        profile.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        staffProfileMapper.insert(profile);
        return profile.getId();
    }

    // 修改工作人员档案
    public void updateStaff(Long id, StaffSaveRequest request) {
        StaffProfile existing = staffProfileMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "工作人员不存在");
        }
        StaffProfile profile = new StaffProfile();
        profile.setId(id);
        profile.setStaffNo(request.getStaffNo());
        profile.setStaffType(request.getStaffType());
        profile.setTitle(request.getTitle());
        profile.setSpecialty(request.getSpecialty());
        profile.setIntroduction(request.getIntroduction());
        profile.setMaxDailyAppointments(request.getMaxDailyAppointments());
        profile.setStatus(request.getStatus());
        staffProfileMapper.update(profile);
        // 同步更新用户姓名和手机号
        if (existing.getUserId() != null) {
            SysUser user = new SysUser();
            user.setId(existing.getUserId());
            user.setRealName(request.getRealName());
            user.setPhone(request.getPhone());
            userMapper.update(user);
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
}
