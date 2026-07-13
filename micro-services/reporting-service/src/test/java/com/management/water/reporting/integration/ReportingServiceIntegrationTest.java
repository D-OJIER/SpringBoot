package com.management.water.reporting.integration;

import com.management.water.reporting.client.TelemetryServiceClient;
import com.management.water.reporting.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
public class ReportingServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelemetryServiceClient telemetryServiceClient;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testReportingStatsIntegration() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogs", 10);
        stats.put("totalUsage", 3000.0);
        stats.put("totalCost", 1500.0);

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("admin");
        when(jwtService.extractRole(anyString())).thenReturn("ADMIN");

        when(telemetryServiceClient.getStats(anyString(), any(), any(), any())).thenReturn(stats);

        mockMvc.perform(get("/dashboard/stats")
                .header("Authorization", "Bearer mock-token")
                .param("apartmentNumber", "A-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLogs").value(10))
                .andExpect(jsonPath("$.totalUsage").value(3000.0))
                .andExpect(jsonPath("$.totalCost").value(1500.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testReportingSummaryIntegration() throws Exception {
        Map<String, Object> summaryItem = new HashMap<>();
        summaryItem.put("apartment", "A-101");
        summaryItem.put("totalUsage", 3000.0);
        summaryItem.put("totalCost", 1500.0);
        List<Map<String, Object>> summaryList = new ArrayList<>();
        summaryList.add(summaryItem);

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("admin");
        when(jwtService.extractRole(anyString())).thenReturn("ADMIN");

        when(telemetryServiceClient.getSummary(anyString(), any(), any(), any())).thenReturn(summaryList);

        mockMvc.perform(get("/monthly-summary")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].apartment").value("A-101"))
                .andExpect(jsonPath("$[0].totalUsage").value(3000.0))
                .andExpect(jsonPath("$[0].totalCost").value(1500.0));
    }
}
