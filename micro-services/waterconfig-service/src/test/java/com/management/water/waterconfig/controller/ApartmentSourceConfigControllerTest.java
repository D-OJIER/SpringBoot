package com.management.water.waterconfig.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.waterconfig.dto.ApartmentSourceConfigCreateRequest;
import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.jwt.JwtService;
import com.management.water.waterconfig.service.ApartmentSourceConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
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
public class ApartmentSourceConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApartmentSourceConfigService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private ApartmentSourceConfig config;

    @BeforeEach
    public void setup() {
        config = ApartmentSourceConfig.builder()
                .id(10L)
                .apartmentId(100L)
                .ratioPercent(50.0)
                .source(WaterSource.builder().id(1L).name("Corporation").build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        ApartmentSourceConfigCreateRequest request = new ApartmentSourceConfigCreateRequest();
        request.setApartmentId(100L);
        request.setSourceId(1L);
        request.setRatioPercent(50.0);

        when(service.create(any(ApartmentSourceConfig.class))).thenReturn(config);

        mockMvc.perform(post("/apartment-source-configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apartmentId").value(100))
                .andExpect(jsonPath("$.ratioPercent").value(50.0));
    }

    @Test
    @WithMockUser
    public void testGetAll_Success() throws Exception {
        when(service.getAll()).thenReturn(Arrays.asList(config));

        mockMvc.perform(get("/apartment-source-configs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].apartmentId").value(100));
    }

    @Test
    @WithMockUser
    public void testGetByApartmentId_Success() throws Exception {
        when(service.getByApartmentId(100L)).thenReturn(Arrays.asList(config));

        mockMvc.perform(get("/apartment-source-configs/apartment/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].apartmentId").value(100));
    }
}
