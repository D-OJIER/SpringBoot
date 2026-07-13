package com.management.water.telemetry.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.telemetry.client.AuthServiceClient;
import com.management.water.telemetry.client.PropertyServiceClient;
import com.management.water.telemetry.client.WaterconfigServiceClient;
import com.management.water.telemetry.dto.*;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.repository.DailyLogRepository;
import com.management.water.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.telemetry.repository.SlabMonthlySummaryRepository;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:telemetry_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=MONTH,YEAR",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
public class TelemetryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private DailyLogSourceBreakdownRepository breakdownRepository;

    @Autowired
    private SlabMonthlySummaryRepository summaryRepository;

    @MockBean
    private PropertyServiceClient propertyServiceClient;

    @MockBean
    private AuthServiceClient authServiceClient;

    @MockBean
    private WaterconfigServiceClient waterconfigServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanup() {
        summaryRepository.deleteAll();
        breakdownRepository.deleteAll();
        dailyLogRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testTelemetryLifecycleIntegration() throws Exception {
        // Setup Feign client stubs
        // 1. Mock PropertyServiceClient to return valid apartment
        ApartmentTypeDto typeDto = new ApartmentTypeDto();
        typeDto.setId(2L);
        typeDto.setBaseOccupancy(2);
        typeDto.setLitresPerPerson(100.0);

        ApartmentDto apartmentDto = new ApartmentDto();
        apartmentDto.setId(100L);
        apartmentDto.setNumber("A-101");
        apartmentDto.setType(typeDto);

        when(propertyServiceClient.getApartmentById(100L)).thenReturn(apartmentDto);

        // 2. Mock WaterconfigServiceClient to return configuration mapping Borewell with ratio=100
        WaterSourceDto sourceDto = new WaterSourceDto();
        sourceDto.setId(1L);
        sourceDto.setName("Borewell");

        ApartmentSourceConfigDto configDto = new ApartmentSourceConfigDto();
        configDto.setId(10L);
        configDto.setApartmentId(100L);
        configDto.setRatioPercent(100.0);
        configDto.setSource(sourceDto);

        when(waterconfigServiceClient.getConfigsByApartmentId(100L)).thenReturn(Collections.singletonList(configDto));

        // 3. Mock WaterconfigServiceClient to return water rates
        WaterRateDto rateDto = new WaterRateDto();
        rateDto.setId(5L);
        rateDto.setMinLitres(0.0);
        rateDto.setMaxLitres(1000.0);
        rateDto.setRatePerLitre(5.0);

        LocalDate logDate = LocalDate.now().minusDays(1);
        when(waterconfigServiceClient.getRatesBySourceAndDate(eq(1L), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(rateDto));

        // 4. Perform POST /daily-logs to add a log (total litres = 300, guest count = 2)
        DailyLogCreateRequest createReq = new DailyLogCreateRequest();
        createReq.setApartmentId(100L);
        createReq.setLogDate(logDate);
        createReq.setTotalLitresConsumed(300.0);
        createReq.setGuestCount(2);

        mockMvc.perform(post("/daily-logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.apartmentNumber").value("A-101"))
                // cost = 300 * 5 = 1500.0
                .andExpect(jsonPath("$.dayCost").value(1500.0));

        // Verify database state
        List<DailyLog> logs = dailyLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals(1500.0, logs.get(0).getDayCost());

        // Verify summary is updated
        assertEquals(1, summaryRepository.count());
        assertTrue(summaryRepository.findByApartmentIdAndSourceIdAndYearAndMonth(100L, 1L, logDate.getYear(), logDate.getMonthValue()).isPresent());

        // 5. Test retrieve stats
        mockMvc.perform(get("/daily-logs")
                .param("apartmentNumber", "A-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].dayCost").value(1500.0));
    }
}
