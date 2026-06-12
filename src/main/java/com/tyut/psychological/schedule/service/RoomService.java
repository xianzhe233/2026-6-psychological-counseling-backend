package com.tyut.psychological.schedule.service;

import com.tyut.psychological.common.api.PageResult;
import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.vo.OptionVO;
import com.tyut.psychological.schedule.dto.RoomSaveRequest;
import com.tyut.psychological.schedule.entity.CounselingRoom;
import com.tyut.psychological.schedule.mapper.CounselingRoomMapper;
import com.tyut.psychological.schedule.vo.RoomVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final CounselingRoomMapper roomMapper;

    public RoomService(CounselingRoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    // 分页查询咨询室，支持关键词和状态筛选
    public PageResult<RoomVO> pageRooms(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        List<RoomVO> records = roomMapper.pageRooms(keyword, status);
        long total = roomMapper.countRooms(keyword, status);
        // 内存分页（数据量小）
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, records.size());
        List<RoomVO> page = from < records.size() ? records.subList(from, to) : List.of();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(page, total, pageNum, pageSize, pages);
    }

    // 新增咨询室
    public Long createRoom(RoomSaveRequest request) {
        CounselingRoom room = new CounselingRoom();
        room.setRoomName(request.getRoomName());
        room.setLocation(request.getLocation());
        room.setCapacity(request.getCapacity() != null ? request.getCapacity() : 1);
        room.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        room.setRemark(request.getRemark());
        roomMapper.insert(room);
        return room.getId();
    }

    // 修改咨询室
    public void updateRoom(Long id, RoomSaveRequest request) {
        CounselingRoom existing = roomMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "咨询室不存在");
        }
        CounselingRoom room = new CounselingRoom();
        room.setId(id);
        room.setRoomName(request.getRoomName());
        room.setLocation(request.getLocation());
        room.setCapacity(request.getCapacity());
        room.setStatus(request.getStatus());
        room.setRemark(request.getRemark());
        roomMapper.update(room);
    }

    // 咨询室下拉选项
    public List<OptionVO> getRoomOptions() {
        return roomMapper.selectOptions();
    }
}
