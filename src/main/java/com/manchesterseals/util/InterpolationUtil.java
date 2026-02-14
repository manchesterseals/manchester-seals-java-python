package com.manchesterseals.util;

import com.manchesterseals.model.CsvRecord;

import java.util.List;

public class InterpolationUtil {

    public static double linearInterpolation(double x1, double y1, double x2, double y2, double x) {
        if (x2 == x1) {
            return y1;
        }
        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    private static CsvRecord[] findBracketingRecords(List<CsvRecord> records, double targetTime) {
        for (int i = 0; i < records.size() - 1; i++) {
            double time1 = records.get(i).getUnixTimestamp();
            double time2 = records.get(i + 1).getUnixTimestamp();

            if (targetTime >= time1 && targetTime <= time2) {
                return new CsvRecord[]{records.get(i), records.get(i + 1)};
            }
        }
        return null;
    }

    public static double[] interpolateECEF(List<CsvRecord> records, double targetTime) {
        CsvRecord[] bracketing = findBracketingRecords(records, targetTime);

        if (bracketing == null) {
            return null;
        }

        CsvRecord record1 = bracketing[0];
        CsvRecord record2 = bracketing[1];

        double time1 = record1.getUnixTimestamp();
        double time2 = record2.getUnixTimestamp();

        double interpolatedX = linearInterpolation(
                time1, record1.getEcefX(),
                time2, record2.getEcefX(),
                targetTime
        );

        double interpolatedY = linearInterpolation(
                time1, record1.getEcefY(),
                time2, record2.getEcefY(),
                targetTime
        );

        double interpolatedZ = linearInterpolation(
                time1, record1.getEcefZ(),
                time2, record2.getEcefZ(),
                targetTime
        );

        return new double[]{interpolatedX, interpolatedY, interpolatedZ};
    }

    public static double euclideanDistance(double[] pointA, double[] pointB) {
        double dx = pointB[0] - pointA[0];
        double dy = pointB[1] - pointA[1];
        double dz = pointB[2] - pointA[2];

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double velocity(List<CsvRecord> records, double time1, double time2) {
        double[] posA = interpolateECEF(records, time1);

        double[] posB = interpolateECEF(records, time2);

        if (posA == null || posB == null) {
            throw new IllegalArgumentException("Time points must be within the dataset range");
        }

        double distance = euclideanDistance(posA, posB);

        double timeDelta = Math.abs(time2 - time1);

        if (timeDelta == 0) {
            return 0.0;
        }

        return distance / timeDelta;
    }
}

