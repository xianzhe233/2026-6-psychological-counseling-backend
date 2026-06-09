package com.tyut.psychological.common.mapper;

import com.tyut.psychological.common.entity.ProblemType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问题类型Mapper
 */
@Mapper
public interface ProblemTypeMapper {

    /**
     * 查询所有启用的问题类型
     */
    List<ProblemType> selectAllEnabled();

    /**
     * 根据ID查询问题类型
     */
    ProblemType selectById(@Param("id") Long id);
}
