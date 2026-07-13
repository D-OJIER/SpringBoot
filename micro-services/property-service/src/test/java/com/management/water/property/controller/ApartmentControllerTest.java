package com.management.water.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.property.dto.ApartmentCreateRequest;
import com.management.water.property.entity.Apartment;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.entity.Block;
import com.management.water.property.jwt.JwtService;
import com.management.water.property.service.ApartmentService;
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
public class ApartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApartmentService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Apartment apartment;

    @BeforeEach
    public void setup() {
        apartment = Apartment.builder()
                .id(10L)
                .number("A-101")
                .block(Block.builder().id(1L).name("A").build())
                .type(ApartmentType.builder().id(2L).name("2BHK").build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        ApartmentCreateRequest request = new ApartmentCreateRequest();
        request.setNumber("A-101");
        request.setBlockId(1L);
        request.setTypeId(2L);

        when(service.create(any(Apartment.class))).thenReturn(apartment);

        mockMvc.perform(post("/apartments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("A-101"))
                .andExpect(jsonPath("$.block.id").value(1))
                .andExpect(jsonPath("$.type.id").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreate_ForbiddenForUser() throws Exception {
        ApartmentCreateRequest request = new ApartmentCreateRequest();
        request.setNumber("A-101");
        request.setBlockId(1L);
        request.setTypeId(2L);

        mockMvc.perform(post("/apartments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAll_Success() throws Exception {
        when(service.getAll()).thenReturn(Arrays.asList(apartment));

        mockMvc.perform(get("/apartments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value("A-101"));
    }

    @Test
    @WithMockUser
    public void testGetById_Success() throws Exception {
        when(service.getById(10L)).thenReturn(apartment);

        mockMvc.perform(get("/apartments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("A-101"));
    }
}
