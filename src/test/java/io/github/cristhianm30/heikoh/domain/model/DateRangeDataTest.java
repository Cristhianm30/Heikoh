package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeDataTest {

    @Test
    void testDateRangeData() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        DateRangeData dateRangeData = new DateRangeData(startDate, endDate);

        assertEquals(startDate, dateRangeData.getStartDate());
        assertEquals(endDate, dateRangeData.getEndDate());
    }
}
