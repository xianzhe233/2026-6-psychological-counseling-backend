package com.tyut.psychological.student.mapper;

import com.tyut.psychological.student.entity.ConsentRecord;
import com.tyut.psychological.student.entity.FirstVisitForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentFormMapper {
    FirstVisitForm selectLatestByStudentId(@Param("studentId") Long studentId);

    FirstVisitForm selectById(@Param("id") Long id);

    int insertFirstVisitForm(FirstVisitForm form);

    ConsentRecord selectConsentByFormId(@Param("formId") Long formId);

    int insertConsentRecord(ConsentRecord record);
}
