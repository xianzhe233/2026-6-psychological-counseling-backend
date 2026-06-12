package com.tyut.psychological.common.log.mapper;

import com.tyut.psychological.common.log.dto.OperationLogQuery;
import com.tyut.psychological.common.log.entity.OperationLog;
import com.tyut.psychological.common.log.vo.OperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    int insert(OperationLog operationLog);

    List<OperationLogVO> pageForAdmin(@Param("q") OperationLogQuery q);

    long countForAdmin(@Param("q") OperationLogQuery q);
}