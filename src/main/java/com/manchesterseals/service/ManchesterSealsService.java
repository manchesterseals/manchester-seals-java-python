package com.manchesterseals.service;

import com.manchesterseals.model.CsvRecord;
import com.manchesterseals.model.CountryData;
import com.manchesterseals.util.CsvLoggingUtil;
import com.manchesterseals.util.InterpolationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManchesterSealsService {
    @Value("${csv.file.path:config/csvFiles/SciTec_code_problem_data.csv}")
    private String csvFilePath;

    private static final int UNIX_TIMESTAMP_INDEX = 0;
    private static final int LATITUDE_INDEX = 1;
    private static final int LONGITUDE_INDEX = 2;
    private static final int ALTITUDE_INDEX = 3;

    public List<CsvRecord> readCsv() throws IOException {
        List<CsvRecord> records = new ArrayList<>();
        Path path = Paths.get(csvFilePath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = parseCSVLine(line);
                CsvRecord record = new CsvRecord();

                if (UNIX_TIMESTAMP_INDEX < values.length) {
                    try {
                        long timestamp = (long) Double.parseDouble(values[UNIX_TIMESTAMP_INDEX].trim());
                        record.setUnixTimestamp(timestamp);
                    } catch (NumberFormatException e) {
                    }
                }

                if (LATITUDE_INDEX < values.length) {
                    try {
                        record.setLatitude(Double.parseDouble(values[LATITUDE_INDEX].trim()));
                    } catch (NumberFormatException e) {
                    }
                }

                if (LONGITUDE_INDEX < values.length) {
                    try {
                        record.setLongitude(Double.parseDouble(values[LONGITUDE_INDEX].trim()));
                    } catch (NumberFormatException e) {
                    }
                }

                if (ALTITUDE_INDEX < values.length) {
                    try {
                        record.setAltitude(Double.parseDouble(values[ALTITUDE_INDEX].trim()));
                    } catch (NumberFormatException e) {
                    }
                }

                records.add(record);
            }
        }

        CsvLoggingUtil.logRecords(records, csvFilePath);
        return CsvLoggingUtil.convertLLAtoECEF(records);
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    public double calculateVelocity(double time1, double time2) throws IOException {
        List<CsvRecord> records = readCsv();
        return InterpolationUtil.velocity(records, time1, time2);
    }

    public long[] getTimeRange() throws IOException {
        List<CsvRecord> records = readCsv();
        if (records.isEmpty()) {
            return new long[]{0, 0};
        }
        long minTime = records.get(0).getUnixTimestamp();
        long maxTime = records.get(records.size() - 1).getUnixTimestamp();
        return new long[]{minTime, maxTime};
    }

    public List<CountryData> filterAndSortCountries(List<CountryData> data) {
        return data.stream()
                .filter(item -> item.getId() != -1)
                .sorted(Comparator.comparing(CountryData::getCountry))
                .collect(Collectors.toList());
    }
}