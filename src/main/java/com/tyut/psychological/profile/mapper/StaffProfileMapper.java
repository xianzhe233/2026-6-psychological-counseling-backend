package com.tyut.psychological.profile.mapper;

import com.tyut.psychological.profile.dto.StaffQuery;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.profile.vo.StaffVO;
import com.tyut.psychological.common.vo.OptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StaffProfileMapper {

    StaffProfile selectById(@Param("id") Long id);

    StaffProfile selectByUserId(@Param("userId") Long userId);

    int insert(StaffProfile profile);

    int update(StaffProfile profile);

    List<StaffVO> pageStaff(@Param("query") StaffQuery query);

    long countStaff(@Param("query") StaffQuery query);

    List<OptionVO> selectOptions(@Param("staffType") String staffType);
}
