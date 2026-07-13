package com.management.water.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.property.dto.ApartmentTypeCreateRequest;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.jwt.JwtService;
import com.management.water.property.service.ApartmentTypeService;
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
public class ApartmentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApartmentTypeService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private ApartmentType type;

    @BeforeEach
    public void setup() {
        type = ApartmentType.builder()
                .id(2L)
                .name("2BHK")
                .baseOccupancy(4)
                .litresPerPerson(150)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        ApartmentTypeCreateRequest request = new ApartmentTypeCreateRequest();
        request.setName("2BHK");
        request.setBaseOccupancy(4);
        request.setLitresPerPerson(150);

        when(service.create(any(ApartmentType.class))).thenReturn(type);

        mockMvc.perform(post("/apartment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("2BHK"))
                .andExpect(jsonPath("$.baseOccupancy").value(4))
                .andExpect(jsonPath("$.litresPerPerson").value(150));
    }

    @Test
    @WithMockUser
    public void testGetAll_Success() throws Exception {
        when(service.getAll()).thenReturn(Arrays.asList(type));

        mockMvc.perform(get("/apartment-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("2BHK"));
    }
}
