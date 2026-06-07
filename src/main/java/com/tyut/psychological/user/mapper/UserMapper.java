package com.tyut.psychological.user.mapper;

import com.tyut.psychological.user.dto.UserQuery;
import com.tyut.psychological.user.entity.SysUser;
import com.tyut.psychological.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    SysUser selectByUsername(@Param("username") String username);

    SysUser selectById(@Param("id") Long id);

    int insert(SysUser user);

    int update(SysUser user);

    List<UserVO> pageUsers(@Param("query") UserQuery query);

    long countUsers(@Param("query") UserQuery query);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int deleteUserRoles(@Param("userId") Long userId);

    Long selectRoleIdByCode(@Param("roleCode") String roleCode);

    int updateLastLoginTime(@Param("id") Long id);
}
