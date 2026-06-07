package com.tyut.psychological.profile.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.common.vo.OptionVO;
import com.tyut.psychological.profile.dto.StaffQuery;
import com.tyut.psychological.profile.dto.StaffSaveRequest;
import com.tyut.psychological.profile.service.StaffProfileService;
import com.tyut.psychological.profile.vo.StaffVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/staff")
public class StaffController {
    private final StaffProfileService staffProfileService;

    public StaffController(StaffProfileService staffProfileService) {
        this.staffProfileService = staffProfileService;
    }

    // 工作人员分页查询
    @GetMapping
    public Result<PageResult<StaffVO>> page(StaffQuery query, HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(staffProfileService.pageStaff(query));
    }

    // 新增工作人员
    @PostMapping
    public Result<Long> create(@Valid @RequestBody StaffSaveRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        return Result.success(staffProfileService.createStaff(request));
    }

    // 修改工作人员
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody StaffSaveRequest request,
                               HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        staffProfileService.updateStaff(id, request);
        return Result.success();
    }

    // 工作人员选项下拉，可按类型筛选（已登录用户均可访问）
    @GetMapping("/options")
    public Result<List<OptionVO>> options(@RequestParam(required = false) String staffType,
                                          HttpServletRequest request) {
        SessionUtils.getRequiredCurrentUser(request);
        return Result.success(staffProfileService.getStaffOptions(staffType));
    }
}
