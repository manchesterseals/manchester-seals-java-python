package com.manchesterseals.service;

import com.manchesterseals.model.CsvRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "csv.file.path=config/csvFiles/SciTec_code_problem_data.csv")
class ManchesterSealsServiceTest {

    @Autowired
    private ManchesterSealsService manchesterSealsService;

    private List<CsvRecord> records;

    @BeforeEach
    void setUp() throws IOException {
        records = manchesterSealsService.readCsv();
    }

    @Test
    void testReadCsv() throws IOException {
        assertNotNull(records);
        assertFalse(records.isEmpty());
        assertTrue(records.size() > 0);
    }

    @Test
    void testReadCsvRecordsHaveEcefCoordinates() throws IOException {
        List<CsvRecord> csvRecords = manchesterSealsService.readCsv();
        for (CsvRecord record : csvRecords) {
            assertTrue(record.getEcefX() != null || record.getEcefY() != null || record.getEcefZ() != null);
        }
    }

    @Test
    void testReadCsvRecordsHaveTimestamps() throws IOException {
        List<CsvRecord> csvRecords = manchesterSealsService.readCsv();
        for (CsvRecord record : csvRecords) {
            assertNotNull(record.getUnixTimestamp());
            assertTrue(record.getUnixTimestamp() > 0);
        }
    }

    @Test
    void testGetTimeRange() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        assertNotNull(timeRange);
        assertEquals(2, timeRange.length);
        assertTrue(timeRange[0] > 0);
        assertTrue(timeRange[1] > 0);
        assertTrue(timeRange[0] < timeRange[1]);
    }

    @Test
    void testGetTimeRangeMinIsSmaller() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        assertTrue(timeRange[0] <= timeRange[1]);
    }

    @Test
    void testCalculateVelocityWithValidTimePoints() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 10;

        double velocity = manchesterSealsService.calculateVelocity(time1, time2);

        assertNotNull(velocity);
        assertTrue(velocity >= 0);
    }

    @Test
    void testCalculateVelocityWithinRange() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long midTime = (timeRange[0] + timeRange[1]) / 2;
        long time1 = midTime - 5;
        long time2 = midTime + 5;

        double velocity = manchesterSealsService.calculateVelocity(time1, time2);

        assertTrue(velocity >= 0);
    }

    @Test
    void testCalculateVelocityOutsideRangeThrowsException() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long invalidTime1 = timeRange[1] + 1000;
        long invalidTime2 = timeRange[1] + 2000;

        assertThrows(IllegalArgumentException.class, () -> {
            manchesterSealsService.calculateVelocity(invalidTime1, invalidTime2);
        });
    }

    @Test
    void testCalculateVelocityFirstTimeOutsideRange() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();

        assertThrows(IllegalArgumentException.class, () -> {
            manchesterSealsService.calculateVelocity(timeRange[1] + 100, timeRange[1] - 100);
        });
    }

    @Test
    void testCalculateVelocityConsistency() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 20;

        double velocity1 = manchesterSealsService.calculateVelocity(time1, time2);
        double velocity2 = manchesterSealsService.calculateVelocity(time1, time2);

        assertEquals(velocity1, velocity2);
    }

    @Test
    void testCalculateVelocityReverseOrder() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 20;

        double velocityForward = manchesterSealsService.calculateVelocity(time1, time2);
        double velocityReverse = manchesterSealsService.calculateVelocity(time2, time1);

        assertEquals(velocityForward, velocityReverse);
    }

    @Test
    void testRecordsAreSorted() throws IOException {
        List<CsvRecord> csvRecords = manchesterSealsService.readCsv();
        for (int i = 1; i < csvRecords.size(); i++) {
            assertTrue(csvRecords.get(i - 1).getUnixTimestamp() <= csvRecords.get(i).getUnixTimestamp());
        }
    }

    @Test
    void testRecordsHaveLatitudeAndLongitude() throws IOException {
        List<CsvRecord> csvRecords = manchesterSealsService.readCsv();
        for (CsvRecord record : csvRecords) {
            assertNotNull(record.getLatitude());
            assertNotNull(record.getLongitude());
            assertNotNull(record.getAltitude());
        }
    }

    @Test
    void testReadCsvReturnsConsistentData() throws IOException {
        List<CsvRecord> records1 = manchesterSealsService.readCsv();
        List<CsvRecord> records2 = manchesterSealsService.readCsv();

        assertEquals(records1.size(), records2.size());
    }
}


