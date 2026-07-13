package com.management.water.reporting.service;

import com.management.water.reporting.client.TelemetryServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DashboardServiceTest {

    @Mock
    private TelemetryServiceClient telemetryClient;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCost", 150.0);
        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        when(telemetryClient.getStats("Bearer token", "A-101", from, to)).thenReturn(stats);

        Map<String, Object> result = dashboardService.getStats("Bearer token", "A-101", from, to);
        assertEquals(150.0, result.get("totalCost"));
    }

    @Test
    public void testGetMonthlySummary() {
        Map<String, Object> summaryItem = new HashMap<>();
        summaryItem.put("apartment", "A-101");
        List<Map<String, Object>> summaryList = Arrays.asList(summaryItem);
        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        when(telemetryClient.getSummary("Bearer token", "A-101", from, to)).thenReturn(summaryList);

        List<Map<String, Object>> result = dashboardService.getMonthlySummary("Bearer token", "A-101", from, to);
        assertEquals(1, result.size());
        assertEquals("A-101", result.get(0).get("apartment"));
    }
}
