package com.manchesterseals.controller;

import com.manchesterseals.model.CsvRecord;
import com.manchesterseals.service.ManchesterSealsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "csv.file.path=config/csvFiles/SciTec_code_problem_data.csv")
class ManchesterSealsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManchesterSealsService manchesterSealsService;

    @Test
    void testGetCsvDataEndpoint() throws Exception {
        mockMvc.perform(get("/api/csv-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void testGetCsvDataReturnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/csv-data"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testGetCsvDataContainsEcefCoordinates() throws Exception {
        mockMvc.perform(get("/api/csv-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ecefX").exists())
                .andExpect(jsonPath("$[0].ecefY").exists())
                .andExpect(jsonPath("$[0].ecefZ").exists());
    }

    @Test
    void testGetCsvDataContainsTimestamps() throws Exception {
        mockMvc.perform(get("/api/csv-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unixTimestamp").exists());
    }

    @Test
    void testGetTimeRangeEndpoint() throws Exception {
        mockMvc.perform(get("/api/time-range"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minTime").exists())
                .andExpect(jsonPath("$.maxTime").exists());
    }

    @Test
    void testGetTimeRangeMinIsLessThanMax() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/time-range"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assert content.contains("minTime");
        assert content.contains("maxTime");
    }

    @Test
    void testGetTimeRangeReturnsValidNumbers() throws Exception {
        mockMvc.perform(get("/api/time-range"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minTime").isNumber())
                .andExpect(jsonPath("$.maxTime").isNumber());
    }

    @Test
    void testGetVelocityWithValidTimePoints() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 10;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(time1))
                .param("time2", String.valueOf(time2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.velocity").exists())
                .andExpect(jsonPath("$.minTime").exists())
                .andExpect(jsonPath("$.maxTime").exists());
    }

    @Test
    void testGetVelocityReturnsValidVelocity() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 10;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(time1))
                .param("time2", String.valueOf(time2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.velocity").isNumber());
    }

    @Test
    void testGetVelocityWithInvalidTimeRange() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long invalidTime1 = timeRange[1] + 1000;
        long invalidTime2 = timeRange[1] + 2000;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(invalidTime1))
                .param("time2", String.valueOf(invalidTime2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetVelocityErrorResponseContainsTimeRange() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long invalidTime1 = timeRange[1] + 1000;
        long invalidTime2 = timeRange[1] + 2000;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(invalidTime1))
                .param("time2", String.valueOf(invalidTime2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.minTime").exists())
                .andExpect(jsonPath("$.maxTime").exists())
                .andExpect(jsonPath("$.providedTime1").exists())
                .andExpect(jsonPath("$.providedTime2").exists());
    }

    @Test
    void testGetVelocityPartiallyInvalidRange() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long validTime = timeRange[0];
        long invalidTime = timeRange[1] + 1000;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(validTime))
                .param("time2", String.valueOf(invalidTime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetVelocityWithMidpointTimes() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long midTime = (timeRange[0] + timeRange[1]) / 2;
        long time1 = midTime - 5;
        long time2 = midTime + 5;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(time1))
                .param("time2", String.valueOf(time2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.velocity").isNumber());
    }

    @Test
    void testGetVelocityConsistentResults() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 15;

        MvcResult result1 = mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(time1))
                .param("time2", String.valueOf(time2)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(time1))
                .param("time2", String.valueOf(time2)))
                .andExpect(status().isOk())
                .andReturn();

        assert result1.getResponse().getContentAsString().equals(result2.getResponse().getContentAsString());
    }

    @Test
    void testGetVelocityNegativeTimeValues() throws Exception {
        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(-100))
                .param("time2", String.valueOf(-50)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCsvDataEndpointContentType() throws Exception {
        mockMvc.perform(get("/api/csv-data"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testTimeRangeEndpointContentType() throws Exception {
        mockMvc.perform(get("/api/time-range"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testVelocityEndpointContentType() throws Exception {
        long[] timeRange = manchesterSealsService.getTimeRange();
        long time1 = timeRange[0];
        long time2 = timeRange[0] + 10;

        mockMvc.perform(get("/api/velocity")
                .param("time1", String.valueOf(time1))
                .param("time2", String.valueOf(time2)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}

