package com.tyut.psychological.appointment.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstVisitAppointmentMapperXmlTest {

    @Test
    void appointmentQueriesShouldPreferStudentContactPhoneWhenPresent() throws IOException {
        String xml = Files.readString(Path.of("src/main/resources/mapper/appointment/FirstVisitAppointmentMapper.xml"));

        assertTrue(xml.contains("COALESCE(sp.contact_phone, su.phone) AS phone"), "预约相关查询应优先返回 student_profile.contact_phone");
    }
}
