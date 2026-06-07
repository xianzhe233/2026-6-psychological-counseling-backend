package com.tyut.psychological.common.log.mapper;

import com.tyut.psychological.common.log.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

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
}