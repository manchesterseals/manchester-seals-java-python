package com.manchesterseals.controller;

import com.manchesterseals.model.CsvRecord;
import com.manchesterseals.model.CountryData;
import com.manchesterseals.model.VelocityResponse;
import com.manchesterseals.service.ManchesterSealsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "ManchesterSeals API", description = "APIs for CSV data processing and interpolation calculations")
public class ManchesterSealsController {

    @Autowired
    private ManchesterSealsService manchesterSealsService;

    @GetMapping("/api/csv-data")
    @Operation(
            summary = "Get CSV Data",
            description = "Retrieves all CSV records from the data file and converts them to ECEF coordinates",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved CSV data",
                            content = @Content(schema = @Schema(implementation = CsvRecord.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error reading CSV file"
                    )
            }
    )
    public List<CsvRecord> getCsvData() throws IOException {
        return manchesterSealsService.readCsv();
    }

    @GetMapping("/api/time-range")
    @Operation(
            summary = "Get Dataset Time Range",
            description = "Retrieves the minimum and maximum timestamps available in the dataset",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved time range"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error reading CSV file"
                    )
            }
    )
    public ResponseEntity<Map<String, Long>> getTimeRange() throws IOException {
        long[] timeRange = manchesterSealsService.getTimeRange();
        Map<String, Long> response = new HashMap<>();
        response.put("minTime", timeRange[0]);
        response.put("maxTime", timeRange[1]);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/velocity")
    @Operation(
            summary = "Calculate Velocity",
            description = "Calculates velocity between two time points using interpolation of ECEF coordinates. " +
                    "Both time points must be within the dataset's time range.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully calculated velocity"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Time points are outside the dataset range"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error calculating velocity"
                    )
            }
    )
    public ResponseEntity<?> getVelocity(
            @RequestParam
            @Parameter(description = "First time point (Unix timestamp in seconds)", example = "1532332859")
            double time1,
            @RequestParam
            @Parameter(description = "Second time point (Unix timestamp in seconds)", example = "1532332900")
            double time2) throws IOException {
        try {
            double velocity = manchesterSealsService.calculateVelocity(time1, time2);
            long[] timeRange = manchesterSealsService.getTimeRange();
            VelocityResponse response = new VelocityResponse(velocity, timeRange[0], timeRange[1]);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            long[] timeRange = manchesterSealsService.getTimeRange();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("minTime", timeRange[0]);
            errorResponse.put("maxTime", timeRange[1]);
            errorResponse.put("providedTime1", time1);
            errorResponse.put("providedTime2", time2);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/api/filter-countries")
    @Operation(
            summary = "Filter and Sort Countries",
            description = "Filters out entries with ID -1 and sorts the remaining entries alphabetically by country name",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully filtered and sorted countries",
                            content = @Content(schema = @Schema(implementation = CountryData.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    )
            }
    )
    public ResponseEntity<List<CountryData>> filterCountries(
            @RequestBody
            @Parameter(description = "List of country data to filter and sort")
            List<CountryData> data) {
        List<CountryData> filtered = manchesterSealsService.filterAndSortCountries(data);
        return ResponseEntity.ok(filtered);
    }
}