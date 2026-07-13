package com.management.water.reporting.controller;

import com.management.water.reporting.jwt.JwtService;
import com.management.water.reporting.service.DashboardService;
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
public class MonthlySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetSummary_Success() throws Exception {
        Map<String, Object> summaryItem = new HashMap<>();
        summaryItem.put("apartment", "A-101");
        List<Map<String, Object>> summaryList = new ArrayList<>();
        summaryList.add(summaryItem);

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("admin");
        when(jwtService.extractRole(anyString())).thenReturn("ADMIN");
        when(dashboardService.getMonthlySummary(anyString(), any(), any(), any())).thenReturn(summaryList);

        mockMvc.perform(get("/monthly-summary")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].apartment").value("A-101"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetSummary_ForbiddenForUser() throws Exception {
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("user");
        when(jwtService.extractRole(anyString())).thenReturn("USER");

        mockMvc.perform(get("/monthly-summary")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }
}
