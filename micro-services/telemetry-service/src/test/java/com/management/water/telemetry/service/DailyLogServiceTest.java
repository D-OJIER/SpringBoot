package com.management.water.telemetry.service;

import com.management.water.telemetry.client.AuthServiceClient;
import com.management.water.telemetry.client.PropertyServiceClient;
import com.management.water.telemetry.dto.ApartmentDto;
import com.management.water.telemetry.dto.ApartmentTypeDto;
import com.management.water.telemetry.dto.UserDto;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.telemetry.entity.SlabMonthlySummary;
import com.management.water.telemetry.exception.ApiException;
import com.management.water.telemetry.repository.DailyLogRepository;
import com.management.water.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.telemetry.repository.SlabMonthlySummaryRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DailyLogServiceTest {

    @Mock private SlabMonthlySummaryRepository summaryRepository;
    @Mock private DailyLogSourceBreakdownRepository breakdownRepository;
    @Mock private DailyLogRepository repository;
    @Mock private PropertyServiceClient propertyClient;
    @Mock private AuthServiceClient authClient;
    @Mock private BillingService billingService;

    private DailyLogService service;
    private DailyLog log;
    private ApartmentDto apartment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new DailyLogService(
                summaryRepository,
                breakdownRepository,
                repository,
                propertyClient,
                authClient,
                billingService,
                new SimpleMeterRegistry()
        );

        log = DailyLog.builder()
                .id(10L)
                .apartmentId(100L)
                .logDate(LocalDate.now().minusDays(1))
                .totalLitresConsumed(300.0)
                .guestCount(2)
                .build();

        ApartmentTypeDto typeDto = new ApartmentTypeDto();
        typeDto.setId(2L);
        typeDto.setBaseOccupancy(2);
        typeDto.setLitresPerPerson(100.0);

        apartment = new ApartmentDto();
        apartment.setId(100L);
        apartment.setNumber("A-101");
        apartment.setType(typeDto);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCreate_Success() {
        when(propertyClient.getApartmentById(100L)).thenReturn(apartment);
        when(repository.existsByApartmentIdAndLogDate(100L, log.getLogDate())).thenReturn(false);
        when(repository.save(any(DailyLog.class))).thenReturn(log);
        when(billingService.calculateDailyCost(any(DailyLog.class), anyDouble())).thenReturn(25.0);

        DailyLogSourceBreakdown breakdown = new DailyLogSourceBreakdown();
        breakdown.setId(1L);
        breakdown.setDailyLog(log);
        breakdown.setSourceId(5L);
        breakdown.setLitres(300.0);
        breakdown.setCost(25.0);

        when(breakdownRepository.findByDailyLogId(10L)).thenReturn(Collections.singletonList(breakdown));
        when(summaryRepository.findByApartmentIdAndSourceIdAndYearAndMonth(100L, 5L, log.getLogDate().getYear(), log.getLogDate().getMonthValue()))
                .thenReturn(Optional.empty());

        DailyLog created = service.create(log);
        assertNotNull(created);
        assertEquals("A-101", created.getApartmentNumber());
        assertEquals(25.0, created.getDayCost());
        verify(repository, times(2)).save(any(DailyLog.class));
        verify(summaryRepository, times(1)).save(any(SlabMonthlySummary.class));
    }

    @Test
    public void testCreate_ApartmentNotFound() {
        feign.FeignException.NotFound feignException = mock(feign.FeignException.NotFound.class);
        when(propertyClient.getApartmentById(100L)).thenThrow(feignException);

        ApiException ex = assertThrows(ApiException.class, () -> service.create(log));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testCreate_GenericException() {
        when(propertyClient.getApartmentById(100L)).thenThrow(new RuntimeException("Connection error"));

        ApiException ex = assertThrows(ApiException.class, () -> service.create(log));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    }

    @Test
    public void testCreate_DuplicateLogForDate() {
        when(propertyClient.getApartmentById(100L)).thenReturn(apartment);
        when(repository.existsByApartmentIdAndLogDate(100L, log.getLogDate())).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> service.create(log));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    public void testCreate_ApartmentTypeDetailsMissing() {
        apartment.setType(null);
        when(propertyClient.getApartmentById(100L)).thenReturn(apartment);
        when(repository.existsByApartmentIdAndLogDate(100L, log.getLogDate())).thenReturn(false);
        when(repository.save(any(DailyLog.class))).thenReturn(log);

        ApiException ex = assertThrows(ApiException.class, () -> service.create(log));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testPage_InvalidPagination_PageLessThanZero() {
        ApiException ex = assertThrows(ApiException.class, () -> service.getPage(-1, 10, null, null, null, null, null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testPage_InvalidPagination_SizeLessThanOne() {
        ApiException ex = assertThrows(ApiException.class, () -> service.getPage(0, 0, null, null, null, null, null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testPage_ResolveDateRange_FromAfterTo() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.plusDays(1);
        ApiException ex = assertThrows(ApiException.class, () -> service.getPage(0, 10, null, from, to, null, null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testPage_ResolveDateRange_TooLarge() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(3);
        ApiException ex = assertThrows(ApiException.class, () -> service.getPage(0, 10, null, from, to, null, null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testPage_Success_AdminUser() {
        setupSecurityContext("ROLE_ADMIN", "adminUser");

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, "A-101", null, null, "dayCost", "ASC");
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testPage_Success_ResidentUser() {
        setupSecurityContext("ROLE_RESIDENT", "residentUser");

        UserDto userDto = new UserDto();
        userDto.setId(5L);
        userDto.setUsername("residentUser");
        userDto.setApartmentId(100L);
        when(authClient.getUserByUsername("residentUser")).thenReturn(userDto);

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, null, null, null, "invalidField", "DESC");
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testPage_ResidentUser_ErrorFetchingDetails() {
        setupSecurityContext("ROLE_RESIDENT", "residentUser");
        when(authClient.getUserByUsername("residentUser")).thenThrow(new RuntimeException("Feign communication failure"));

        ApiException ex = assertThrows(ApiException.class, () -> service.getPage(0, 10, null, null, null, null, null));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    }

    @Test
    public void testStats_Success_AdminUser() {
        setupSecurityContext("ROLE_ADMIN", "adminUser");

        DailyLogRepository.DashboardStatsProjection projection = mock(DailyLogRepository.DashboardStatsProjection.class);
        when(projection.getTotalLogs()).thenReturn(5L);
        when(projection.getTotalUsage()).thenReturn(1500.0);
        when(projection.getTotalCost()).thenReturn(450.0);

        when(repository.summarizeStats(eq(null), eq("A-101"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(projection);

        Map<String, Object> stats = service.getStats("A-101", null, null);
        assertNotNull(stats);
        assertEquals(5L, stats.get("totalLogs"));
        assertEquals(1500.0, stats.get("totalUsage"));
        assertEquals(450.0, stats.get("totalCost"));
    }

    @Test
    public void testStats_Success_ResidentUser() {
        setupSecurityContext("ROLE_RESIDENT", "residentUser");

        UserDto userDto = new UserDto();
        userDto.setApartmentId(100L);
        when(authClient.getUserByUsername("residentUser")).thenReturn(userDto);

        DailyLogRepository.DashboardStatsProjection projection = mock(DailyLogRepository.DashboardStatsProjection.class);
        when(projection.getTotalLogs()).thenReturn(3L);
        when(projection.getTotalUsage()).thenReturn(900.0);
        when(projection.getTotalCost()).thenReturn(270.0);

        when(repository.summarizeStats(eq(100L), eq(null), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(projection);

        Map<String, Object> stats = service.getStats(null, null, null);
        assertNotNull(stats);
        assertEquals(3L, stats.get("totalLogs"));
    }

    @Test
    public void testStats_ResidentUser_ErrorFetchingDetails() {
        setupSecurityContext("ROLE_RESIDENT", "residentUser");
        when(authClient.getUserByUsername("residentUser")).thenThrow(new RuntimeException("Feign failure"));

        ApiException ex = assertThrows(ApiException.class, () -> service.getStats(null, null, null));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    }

    @Test
    public void testMonthlySummary_Success() {
        DailyLogRepository.MonthlySummaryProjection summaryProjection = mock(DailyLogRepository.MonthlySummaryProjection.class);
        when(summaryProjection.getApartment()).thenReturn("A-101");
        when(summaryProjection.getTotalUsage()).thenReturn(3000.0);
        when(summaryProjection.getTotalCost()).thenReturn(900.0);

        when(repository.summarizeByApartment(eq(null), eq("A-101"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(summaryProjection));

        List<Map<String, Object>> result = service.getMonthlySummary("A-101", null, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A-101", result.get(0).get("apartment"));
        assertEquals(3000.0, result.get(0).get("totalUsage"));
        assertEquals(900.0, result.get(0).get("totalCost"));
    }

    @Test
    public void testCreate_Success_SummaryAlreadyExists() {
        when(propertyClient.getApartmentById(100L)).thenReturn(apartment);
        when(repository.existsByApartmentIdAndLogDate(100L, log.getLogDate())).thenReturn(false);
        when(repository.save(any(DailyLog.class))).thenReturn(log);
        when(billingService.calculateDailyCost(any(DailyLog.class), anyDouble())).thenReturn(25.0);

        DailyLogSourceBreakdown breakdown = new DailyLogSourceBreakdown();
        breakdown.setId(1L);
        breakdown.setDailyLog(log);
        breakdown.setSourceId(5L);
        breakdown.setLitres(300.0);
        breakdown.setCost(25.0);

        SlabMonthlySummary existingSummary = new SlabMonthlySummary();
        existingSummary.setId(20L);
        existingSummary.setApartmentId(100L);
        existingSummary.setSourceId(5L);
        existingSummary.setYear(log.getLogDate().getYear());
        existingSummary.setMonth(log.getLogDate().getMonthValue());
        existingSummary.setTotalLitres(1000);
        existingSummary.setTotalCost(100);

        when(breakdownRepository.findByDailyLogId(10L)).thenReturn(Collections.singletonList(breakdown));
        when(summaryRepository.findByApartmentIdAndSourceIdAndYearAndMonth(100L, 5L, log.getLogDate().getYear(), log.getLogDate().getMonthValue()))
                .thenReturn(Optional.of(existingSummary));

        DailyLog created = service.create(log);
        assertNotNull(created);
        verify(summaryRepository, times(1)).save(existingSummary);
        assertEquals(1300.0, existingSummary.getTotalLitres());
        assertEquals(125.0, existingSummary.getTotalCost());
    }

    @Test
    public void testPage_ResidentUser_UserDtoNull() {
        setupSecurityContext("ROLE_RESIDENT", "residentUser");
        when(authClient.getUserByUsername("residentUser")).thenReturn(null);

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, null, null, null, null, null);
        assertNotNull(result);
    }

    @Test
    public void testPage_Success_OnlyFromDate() {
        setupSecurityContext("ROLE_ADMIN", "adminUser");

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, null, LocalDate.now().minusDays(5), null, null, null);
        assertNotNull(result);
    }

    @Test
    public void testPage_Success_OnlyToDate() {
        setupSecurityContext("ROLE_ADMIN", "adminUser");

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, null, null, LocalDate.now(), null, null);
        assertNotNull(result);
    }

    @Test
    public void testPage_Success_NoFilters() {
        setupSecurityContext("ROLE_ADMIN", "adminUser");

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, "", null, null, null, null);
        assertNotNull(result);
    }

    @Test
    public void testPage_NoAuthenticationContext() {
        SecurityContextHolder.clearContext();

        Page<DailyLog> page = new PageImpl<>(Collections.singletonList(log));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<DailyLog> result = service.getPage(0, 10, "  ", null, null, null, null);
        assertNotNull(result);
    }

    @Test
    public void testStats_ResidentUser_UserDtoNull() {
        setupSecurityContext("ROLE_RESIDENT", "residentUser");
        when(authClient.getUserByUsername("residentUser")).thenReturn(null);

        DailyLogRepository.DashboardStatsProjection projection = mock(DailyLogRepository.DashboardStatsProjection.class);
        when(repository.summarizeStats(eq(null), eq(null), any(LocalDate.class), any(LocalDate.class))).thenReturn(projection);

        Map<String, Object> stats = service.getStats(null, null, null);
        assertNotNull(stats);
    }

    private void setupSecurityContext(String role, String username) {
        Authentication auth = mock(Authentication.class);
        SecurityContext secContext = mock(SecurityContext.class);
        when(secContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(secContext);

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        doReturn(Collections.singletonList(authority)).when(auth).getAuthorities();
        when(auth.getName()).thenReturn(username);
    }
}
