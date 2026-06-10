package com.tyut.psychological.common.log.dto;

import com.tyut.psychological.common.notification.dto.NotificationLogQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminLogQueryTest {

    @Test
    void notificationLogQueryShouldNormalizeInvalidPagination() {
        NotificationLogQuery query = new NotificationLogQuery();

        query.setPageNum(0);
        query.setPageSize(-1);

        assertEquals(1, query.getPageNum());
        assertEquals(10, query.getPageSize());
        assertEquals(0, query.offset());
    }

    @Test
    void operationLogQueryShouldNormalizeInvalidPagination() {
        OperationLogQuery query = new OperationLogQuery();

        query.setPageNum(null);
        query.setPageSize(0);

        assertEquals(1, query.getPageNum());
        assertEquals(10, query.getPageSize());
        assertEquals(0, query.offset());
    }
}
