package com.tyut.psychological.consultation.mapper;

import com.tyut.psychological.consultation.dto.ConsultationQueueQuery;
import com.tyut.psychological.consultation.entity.ConsultationQueue;
import com.tyut.psychological.consultation.vo.ConsultationQueueDetailVO;
import com.tyut.psychological.consultation.vo.ConsultationQueueVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultationQueueMapper {

    ConsultationQueue selectById(@Param("id") Long id);

    int insert(ConsultationQueue queue);

    int updateStatus(@Param("id") Long id,
                     @Param("queueStatus") String queueStatus,
                     @Param("assignedTime") java.time.LocalDateTime assignedTime);

    List<ConsultationQueueVO> pageQueue(@Param("query") ConsultationQueueQuery query);

    long countQueue(@Param("query") ConsultationQueueQuery query);

    ConsultationQueueDetailVO selectDetailById(@Param("id") Long id);
}
