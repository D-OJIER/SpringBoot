package com.management.water.property.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.property.dto.ApartmentCreateRequest;
import com.management.water.property.dto.ApartmentTypeCreateRequest;
import com.management.water.property.dto.BlockCreateRequest;
import com.management.water.property.entity.Apartment;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.entity.Block;
import com.management.water.property.repository.ApartmentRepository;
import com.management.water.property.repository.ApartmentTypeRepository;
import com.management.water.property.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:property_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
public class PropertyServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private ApartmentTypeRepository apartmentTypeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanup() {
        apartmentRepository.deleteAll();
        blockRepository.deleteAll();
        apartmentTypeRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testPropertyWorkflowIntegration() throws Exception {
        // 1. Create a Block "Block-B"
        BlockCreateRequest blockReq = new BlockCreateRequest();
        blockReq.setName("Block-B");

        MvcResult blockResult = mockMvc.perform(post("/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blockReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Block-B"))
                .andReturn();

        String blockResponse = blockResult.getResponse().getContentAsString();
        Block createdBlock = objectMapper.readValue(blockResponse, Block.class);
        assertNotNull(createdBlock.getId());

        // 2. Create an ApartmentType "3BHK"
        ApartmentTypeCreateRequest typeReq = new ApartmentTypeCreateRequest();
        typeReq.setName("3BHK");
        typeReq.setBaseOccupancy(6);
        typeReq.setLitresPerPerson(180);

        MvcResult typeResult = mockMvc.perform(post("/apartment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("3BHK"))
                .andReturn();

        String typeResponse = typeResult.getResponse().getContentAsString();
        ApartmentType createdType = objectMapper.readValue(typeResponse, ApartmentType.class);
        assertNotNull(createdType.getId());

        // 3. Create an Apartment "B-302" mapping to Block-B and 3BHK
        ApartmentCreateRequest aptReq = new ApartmentCreateRequest();
        aptReq.setNumber("B-302");
        aptReq.setBlockId(createdBlock.getId());
        aptReq.setTypeId(createdType.getId());

        MvcResult aptResult = mockMvc.perform(post("/apartments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aptReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value("B-302"))
                .andExpect(jsonPath("$.block.name").value("Block-B"))
                .andExpect(jsonPath("$.type.name").value("3BHK"))
                .andReturn();

        String aptResponse = aptResult.getResponse().getContentAsString();
        Apartment createdApt = objectMapper.readValue(aptResponse, Apartment.class);
        assertNotNull(createdApt.getId());

        // 4. Retrieve all apartments as ADMIN and assert
        mockMvc.perform(get("/apartments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value("B-302"));

        // 5. Retrieve apartment by ID as general authenticated user
        mockMvc.perform(get("/apartments/" + createdApt.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("B-302"))
                .andExpect(jsonPath("$.block.name").value("Block-B"))
                .andExpect(jsonPath("$.type.name").value("3BHK"));
    }
}
