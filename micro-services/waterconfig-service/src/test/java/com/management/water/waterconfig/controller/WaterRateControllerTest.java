package com.management.water.waterconfig.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.waterconfig.dto.WaterRateCreateRequest;
import com.management.water.waterconfig.entity.WaterRate;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.jwt.JwtService;
import com.management.water.waterconfig.service.WaterRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
public class WaterRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaterRateService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private WaterRate rate;

    @BeforeEach
    public void setup() {
        rate = WaterRate.builder()
                .id(10L)
                .minLitres(0.0)
                .maxLitres(100.0)
                .ratePerLitre(5.0)
                .effectiveFrom(LocalDate.now().minusDays(1))
                .source(WaterSource.builder().id(1L).name("Corporation").build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        WaterRateCreateRequest request = new WaterRateCreateRequest();
        request.setMinLitres(0.0);
        request.setMaxLitres(100.0);
        request.setRatePerLitre(5.0);
        request.setEffectiveFrom(LocalDate.now().minusDays(1));
        request.setSourceId(1L);

        when(service.create(any(WaterRate.class))).thenReturn(rate);

        mockMvc.perform(post("/water-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratePerLitre").value(5.0));
    }

    @Test
    @WithMockUser
    public void testGetAll_Success() throws Exception {
        when(service.getAll()).thenReturn(Arrays.asList(rate));

        mockMvc.perform(get("/water-rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ratePerLitre").value(5.0));
    }

    @Test
    @WithMockUser
    public void testGetRatesBySourceAndDate_Success() throws Exception {
        LocalDate date = LocalDate.now();
        when(service.getRatesBySourceAndDate(1L, date)).thenReturn(Arrays.asList(rate));

        mockMvc.perform(get("/water-rates/source/1/date/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ratePerLitre").value(5.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDelete_Success() throws Exception {
        doNothing().when(service).deleteById(10L);

        mockMvc.perform(delete("/water-rates/10"))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteById(10L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_Success() throws Exception {
        WaterRateCreateRequest request = new WaterRateCreateRequest();
        request.setMinLitres(0.0);
        request.setMaxLitres(100.0);
        request.setRatePerLitre(6.0);
        request.setEffectiveFrom(LocalDate.now().minusDays(1));
        request.setSourceId(1L);

        rate.setRatePerLitre(6.0);
        when(service.update(eq(10L), any(WaterRate.class))).thenReturn(rate);

        mockMvc.perform(put("/water-rates/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratePerLitre").value(6.0));
    }
}
