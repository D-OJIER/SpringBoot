package com.management.water.telemetry.controller;

import com.management.water.telemetry.jwt.JwtService;
import com.management.water.telemetry.service.DailyLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
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
    private DailyLogService service;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetSummary() throws Exception {
        when(service.getMonthlySummary(any(), any(), any())).thenReturn(Collections.singletonList(Collections.singletonMap("month", "JULY")));

        mockMvc.perform(get("/monthly-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("JULY"));
    }
}
