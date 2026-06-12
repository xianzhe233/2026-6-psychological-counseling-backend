package com.tyut.psychological.consultation.mapper;

import com.tyut.psychological.consultation.dto.ExtensionAdminQuery;
import com.tyut.psychological.consultation.dto.ExtensionQuery;
import com.tyut.psychological.consultation.entity.ExtensionRequest;
import com.tyut.psychological.consultation.vo.ExtensionRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtensionRequestMapper {

    ExtensionRequest selectById(@Param("id") Long id);

    int insert(ExtensionRequest request);

    int updateAudit(@Param("id") Long id,
                    @Param("requestStatus") String requestStatus,
                    @Param("auditAdminId") Long auditAdminId,
                    @Param("auditRemark") String auditRemark);

    List<ExtensionRequestVO> pageForCounselor(@Param("query") ExtensionQuery query);

    long countForCounselor(@Param("query") ExtensionQuery query);

    List<ExtensionRequestVO> pageForAdmin(@Param("query") ExtensionAdminQuery query);

    long countForAdmin(@Param("query") ExtensionAdminQuery query);

    long countCounselorStudentRelation(@Param("counselorId") Long counselorId,
                                       @Param("studentId") Long studentId);
}
