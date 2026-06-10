package com.tyut.psychological.common.log.mapper;

import com.tyut.psychological.common.log.dto.OperationLogQuery;
import com.tyut.psychological.common.log.entity.OperationLog;
import com.tyut.psychological.common.log.vo.OperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface OperationLogMapper {

    /**
     * 插入操作日志
     * @param operationLog 操作日志
     * @return 影响行数
     */
    int insert(OperationLog operationLog);

    List<OperationLogVO> pageForAdmin(@Param("query") OperationLogQuery query);

    long countForAdmin(@Param("query") OperationLogQuery query);
}