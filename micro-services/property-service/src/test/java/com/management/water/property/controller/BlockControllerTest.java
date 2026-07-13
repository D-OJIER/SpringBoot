package com.management.water.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.property.dto.BlockCreateRequest;
import com.management.water.property.entity.Block;
import com.management.water.property.jwt.JwtService;
import com.management.water.property.service.BlockService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
public class BlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Block block;

    @BeforeEach
    public void setup() {
        block = Block.builder().id(1L).name("A").build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Success() throws Exception {
        BlockCreateRequest request = new BlockCreateRequest();
        request.setName("A");

        when(service.create(any(Block.class))).thenReturn(block);

        mockMvc.perform(post("/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDelete_Success() throws Exception {
        doNothing().when(service).deleteById(1L);

        mockMvc.perform(delete("/blocks/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser
    public void testGetAll_Success() throws Exception {
        when(service.getAll()).thenReturn(Arrays.asList(block));

        mockMvc.perform(get("/blocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("A"));
    }
}
