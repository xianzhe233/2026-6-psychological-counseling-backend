package com.tyut.psychological.schedule.mapper;

import com.tyut.psychological.schedule.entity.CounselingRoom;
import com.tyut.psychological.schedule.vo.RoomVO;
import com.tyut.psychological.common.vo.OptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CounselingRoomMapper {

    CounselingRoom selectById(@Param("id") Long id);

    int insert(CounselingRoom room);

    int update(CounselingRoom room);

    List<RoomVO> pageRooms(@Param("keyword") String keyword, @Param("status") Integer status);

    long countRooms(@Param("keyword") String keyword, @Param("status") Integer status);

    List<OptionVO> selectOptions();
}
