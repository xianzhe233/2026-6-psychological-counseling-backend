package com.tyut.psychological.schedule.controller;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.enums.RoleCode;
import com.tyut.psychological.common.util.SessionUtils;
import com.tyut.psychological.common.vo.OptionVO;
import com.tyut.psychological.schedule.dto.RoomSaveRequest;
import com.tyut.psychological.schedule.service.RoomService;
import com.tyut.psychological.schedule.vo.RoomVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 咨询室分页查询
    @GetMapping
    public Result<PageResult<RoomVO>> page(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           HttpServletRequest request) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(request), RoleCode.ADMIN);
        return Result.success(roomService.pageRooms(keyword, status, pageNum, pageSize));
    }

    // 新增咨询室
    @PostMapping
    public Result<Long> create(@Valid @RequestBody RoomSaveRequest request, HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        return Result.success(roomService.createRoom(request));
    }

    // 修改咨询室
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoomSaveRequest request,
                               HttpServletRequest httpRequest) {
        SessionUtils.requireAnyRole(SessionUtils.getRequiredCurrentUser(httpRequest), RoleCode.ADMIN);
        roomService.updateRoom(id, request);
        return Result.success();
    }

    // 咨询室下拉选项（已登录用户均可访问）
    @GetMapping("/options")
    public Result<List<OptionVO>> options(HttpServletRequest request) {
        SessionUtils.getRequiredCurrentUser(request);
        return Result.success(roomService.getRoomOptions());
    }
}
