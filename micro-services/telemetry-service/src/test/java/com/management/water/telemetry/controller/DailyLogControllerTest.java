package com.management.water.telemetry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.telemetry.dto.DailyLogCreateRequest;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.jwt.JwtService;
import com.management.water.telemetry.service.DailyLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
public class DailyLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyLogService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private DailyLog log;

    @BeforeEach
    public void setup() {
        log = DailyLog.builder()
                .id(10L)
                .apartmentId(100L)
                .apartmentNumber("A-101")
                .logDate(LocalDate.now().minusDays(1))
                .totalLitresConsumed(300.0)
                .guestCount(2)
                .dayCost(25.0)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        DailyLogCreateRequest request = new DailyLogCreateRequest();
        request.setApartmentId(100L);
        request.setLogDate(LocalDate.now().minusDays(1));
        request.setTotalLitresConsumed(300.0);
        request.setGuestCount(2);

        when(service.create(any(DailyLog.class))).thenReturn(log);

        mockMvc.perform(post("/daily-logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apartmentNumber").value("A-101"))
                .andExpect(jsonPath("$.dayCost").value(25.0));
    }

    @Test
    @WithMockUser
    public void testGetAll_Success() throws Exception {
        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(service.getPage(anyInt(), anyInt(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/daily-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].apartmentNumber").value("A-101"));
    }
}
