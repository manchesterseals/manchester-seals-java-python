package com.manchesterseals.util;

import com.manchesterseals.model.CsvRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CsvLoggingUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvLoggingUtil.class);
    private static final double  a = 6378137.0;
    private static final double e2 = 6.6943799901377997e-3;

    public static void logRecords(List<CsvRecord> records, String csvFilePath) {
        logger.info("Successfully read {} records from CSV file: {}", records.size(), csvFilePath);

        if (!records.isEmpty()) {
            logger.info("Sample record: {}", records.get(0));
        }

        for (int i = 0; i < records.size(); i++) {
            logger.debug("Record {}: {}", i + 1, records.get(i));
        }
    }


    public static List<CsvRecord> convertLLAtoECEF(List<CsvRecord> records) {
        for (CsvRecord record : records) {
            double lat = record.getLatitude() * Math.PI / 180;
            double lon = record.getLongitude() * Math.PI / 180;
            double alt = record.getAltitude();
            double n = a / sqrt(1 - e2*sin(lat) * sin(lat));

            double ecefX = (n + alt) * cos(lat) * cos(lon);
            double ecefY = (n + alt) * cos(lat) * sin(lon);
            double ecefZ = (n*(1 - e2) + alt) * sin(lat);

            record.setEcefX(ecefX);
            record.setEcefY(ecefY);
            record.setEcefZ(ecefZ);
        }

        return calculateVelocity(records);
    }

    public static List<CsvRecord> calculateVelocity(List<CsvRecord> records) {
        List<CsvRecord> velocities = new ArrayList<>();
        velocities.add(new CsvRecord(records.get(0).getUnixTimestamp(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

        for (int i = 1; i < records.size(); i++) {
            CsvRecord p1 = records.get(i - 1);
            CsvRecord p2 = records.get(i);

            Instant t1 = Instant.ofEpochSecond(p1.getUnixTimestamp());
            Instant t2 = Instant.ofEpochSecond(p2.getUnixTimestamp());
            long dtSeconds = ChronoUnit.SECONDS.between(t1, t2);
            if (dtSeconds == 0) continue;

            double vx = (p2.getLatitude() - p1.getLatitude()) / dtSeconds;
            double vy = (p2.getLongitude() - p1.getLongitude()) / dtSeconds;
            double vz = (p2.getAltitude() - p1.getAltitude()) / dtSeconds;

            velocities.add(new CsvRecord(p2.getUnixTimestamp(), p2.getLatitude(), p2.getLongitude(), p2.getAltitude(), p2.getEcefX(), p2.getEcefY(), p2.getEcefZ(), vx, vy, vz));
        }
        return velocities;
    }
}
