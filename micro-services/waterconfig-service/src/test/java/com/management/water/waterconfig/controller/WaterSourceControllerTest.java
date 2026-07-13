package com.management.water.waterconfig.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.waterconfig.dto.WaterSourceCreateRequest;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.jwt.JwtService;
import com.management.water.waterconfig.service.WaterSourceService;
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
public class WaterSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaterSourceService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private WaterSource source;

    @BeforeEach
    public void setup() {
        source = WaterSource.builder()
                .id(1L)
                .name("Corporation")
                .pricingType("SLAB")
                .supplyType("MUNICIPAL")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        WaterSourceCreateRequest request = new WaterSourceCreateRequest();
        request.setName("Corporation");
        request.setPricingType("SLAB");
        request.setSupplyType("MUNICIPAL");

        when(service.create(any(WaterSource.class))).thenReturn(source);

        mockMvc.perform(post("/water-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Corporation"));
    }

    @Test
    @WithMockUser
    public void testGetAll_Success() throws Exception {
        when(service.getAll()).thenReturn(Arrays.asList(source));

        mockMvc.perform(get("/water-sources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Corporation"));
    }
}
