package com.manchesterseals.util;

import com.manchesterseals.model.CsvRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpolationUtilTest {

    private List<CsvRecord> testRecords;

    @BeforeEach
    void setUp() {
        testRecords = new ArrayList<>();

        CsvRecord record1 = new CsvRecord(1000L, 40.0, -75.0, 100.0, 1000000.0, 2000000.0, 3000000.0, 0.0, 0.0, 0.0);
        CsvRecord record2 = new CsvRecord(1010L, 40.1, -75.1, 110.0, 1001000.0, 2001000.0, 3001000.0, 0.0, 0.0, 0.0);
        CsvRecord record3 = new CsvRecord(1020L, 40.2, -75.2, 120.0, 1002000.0, 2002000.0, 3002000.0, 0.0, 0.0, 0.0);

        testRecords.add(record1);
        testRecords.add(record2);
        testRecords.add(record3);
    }

    @Test
    void testLinearInterpolationBasic() {
        double result = InterpolationUtil.linearInterpolation(0, 0, 10, 10, 5);
        assertEquals(5.0, result);
    }

    @Test
    void testLinearInterpolationAtStart() {
        double result = InterpolationUtil.linearInterpolation(0, 0, 10, 10, 0);
        assertEquals(0.0, result);
    }

    @Test
    void testLinearInterpolationAtEnd() {
        double result = InterpolationUtil.linearInterpolation(0, 0, 10, 10, 10);
        assertEquals(10.0, result);
    }

    @Test
    void testLinearInterpolationDivideByZero() {
        double result = InterpolationUtil.linearInterpolation(5, 10, 5, 20, 5);
        assertEquals(10.0, result);
    }

    @Test
    void testLinearInterpolationNegativeValues() {
        double result = InterpolationUtil.linearInterpolation(-10, -10, 10, 10, 0);
        assertEquals(0.0, result);
    }

    @Test
    void testInterpolateECEFWithinRange() {
        double[] result = InterpolationUtil.interpolateECEF(testRecords, 1005);
        assertNotNull(result);
        assertEquals(3, result.length);
        assertNotEquals(0.0, result[0]);
        assertNotEquals(0.0, result[1]);
        assertNotEquals(0.0, result[2]);
    }

    @Test
    void testInterpolateECEFAtExactTime() {
        double[] result = InterpolationUtil.interpolateECEF(testRecords, 1000);
        assertNotNull(result);
        assertEquals(1000000.0, result[0], 0.01);
        assertEquals(2000000.0, result[1], 0.01);
        assertEquals(3000000.0, result[2], 0.01);
    }

    @Test
    void testInterpolateECEFOutsideRange() {
        double[] result = InterpolationUtil.interpolateECEF(testRecords, 2000);
        assertNull(result);
    }

    @Test
    void testInterpolateECEFBeforeRange() {
        double[] result = InterpolationUtil.interpolateECEF(testRecords, 500);
        assertNull(result);
    }

    @Test
    void testEuclideanDistance() {
        double[] pointA = {0, 0, 0};
        double[] pointB = {3, 4, 0};
        double distance = InterpolationUtil.euclideanDistance(pointA, pointB);
        assertEquals(5.0, distance);
    }

    @Test
    void testEuclideanDistance3D() {
        double[] pointA = {0, 0, 0};
        double[] pointB = {1, 1, 1};
        double distance = InterpolationUtil.euclideanDistance(pointA, pointB);
        assertEquals(Math.sqrt(3), distance);
    }

    @Test
    void testEuclideanDistanceSamePoint() {
        double[] pointA = {5, 5, 5};
        double[] pointB = {5, 5, 5};
        double distance = InterpolationUtil.euclideanDistance(pointA, pointB);
        assertEquals(0.0, distance);
    }

    @Test
    void testVelocityWithValidTimePoints() {
        double velocity = InterpolationUtil.velocity(testRecords, 1000, 1010);
        assertTrue(velocity > 0);
    }

    @Test
    void testVelocitySymmetric() {
        double velocity1 = InterpolationUtil.velocity(testRecords, 1000, 1010);
        double velocity2 = InterpolationUtil.velocity(testRecords, 1010, 1000);
        assertEquals(velocity1, velocity2);
    }

    @Test
    void testVelocityWithInvalidTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            InterpolationUtil.velocity(testRecords, 2000, 3000);
        });
    }

    @Test
    void testVelocityWithOutOfRangeStart() {
        assertThrows(IllegalArgumentException.class, () -> {
            InterpolationUtil.velocity(testRecords, 500, 1005);
        });
    }

    @Test
    void testVelocityWithOutOfRangeEnd() {
        assertThrows(IllegalArgumentException.class, () -> {
            InterpolationUtil.velocity(testRecords, 1005, 2000);
        });
    }

    @Test
    void testVelocityConsistency() {
        double velocity1 = InterpolationUtil.velocity(testRecords, 1000, 1010);
        double velocity2 = InterpolationUtil.velocity(testRecords, 1000, 1010);
        assertEquals(velocity1, velocity2);
    }
}

