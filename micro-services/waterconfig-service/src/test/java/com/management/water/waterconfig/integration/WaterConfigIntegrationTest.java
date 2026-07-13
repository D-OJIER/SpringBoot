package com.management.water.waterconfig.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.waterconfig.dto.ApartmentSourceConfigCreateRequest;
import com.management.water.waterconfig.dto.WaterRateCreateRequest;
import com.management.water.waterconfig.dto.WaterSourceCreateRequest;
import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.waterconfig.entity.WaterRate;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.waterconfig.repository.WaterRateRepository;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import com.management.water.waterconfig.client.PropertyServiceClient;
import com.management.water.waterconfig.dto.ApartmentDto;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

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
    "spring.datasource.url=jdbc:h2:mem:waterconfig_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
public class WaterConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WaterSourceRepository waterSourceRepository;

    @Autowired
    private WaterRateRepository waterRateRepository;

    @Autowired
    private ApartmentSourceConfigRepository apartmentSourceConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyServiceClient propertyServiceClient;

    @BeforeEach
    public void cleanup() {
        apartmentSourceConfigRepository.deleteAll();
        waterRateRepository.deleteAll();
        waterSourceRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testWaterConfigWorkflowIntegration() throws Exception {
        // 1. Create a WaterSource via REST API
        WaterSourceCreateRequest sourceReq = new WaterSourceCreateRequest();
        sourceReq.setName("Borewell");
        sourceReq.setPricingType("SLAB");
        sourceReq.setSupplyType("GROUND");

        MvcResult sourceResult = mockMvc.perform(post("/water-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sourceReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Borewell"))
                .andReturn();

        String sourceResponse = sourceResult.getResponse().getContentAsString();
        WaterSource createdSource = objectMapper.readValue(sourceResponse, WaterSource.class);
        assertNotNull(createdSource.getId());

        // 2. Create a WaterRate for the Borwell WaterSource
        WaterRateCreateRequest rateReq = new WaterRateCreateRequest();
        rateReq.setMinLitres(0.0);
        rateReq.setMaxLitres(500.0);
        rateReq.setRatePerLitre(7.5);
        rateReq.setEffectiveFrom(LocalDate.now().minusDays(5));
        rateReq.setEffectiveTo(LocalDate.now().plusDays(30));
        rateReq.setSourceId(createdSource.getId());

        MvcResult rateResult = mockMvc.perform(post("/water-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ratePerLitre").value(7.5))
                .andReturn();

        String rateResponse = rateResult.getResponse().getContentAsString();
        WaterRate createdRate = objectMapper.readValue(rateResponse, WaterRate.class);
        assertNotNull(createdRate.getId());

        // 3. Create an ApartmentSourceConfig for apartment ID 101, referencing Borewell
        ApartmentDto apartmentDto = new ApartmentDto(101L, "A-101");
        when(propertyServiceClient.getApartmentById(101L)).thenReturn(apartmentDto);

        ApartmentSourceConfigCreateRequest configReq = new ApartmentSourceConfigCreateRequest();
        configReq.setApartmentId(101L);
        configReq.setSourceId(createdSource.getId());
        configReq.setRatioPercent(100.0);

        MvcResult configResult = mockMvc.perform(post("/apartment-source-configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(configReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.apartmentId").value(101))
                .andExpect(jsonPath("$.ratioPercent").value(100))
                .andReturn();

        String configResponse = configResult.getResponse().getContentAsString();
        ApartmentSourceConfig createdConfig = objectMapper.readValue(configResponse, ApartmentSourceConfig.class);
        assertNotNull(createdConfig.getId());

        // 4. Retrieve configurations by apartment ID and verify
        mockMvc.perform(get("/apartment-source-configs/apartment/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].source.name").value("Borewell"));

        // 5. Query rates by source and date
        LocalDate date = LocalDate.now();
        mockMvc.perform(get("/water-rates/source/" + createdSource.getId() + "/date/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ratePerLitre").value(7.5));
    }
}
