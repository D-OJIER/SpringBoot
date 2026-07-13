package com.management.water.telemetry.service;

import com.management.water.telemetry.client.AuthServiceClient;
import com.management.water.telemetry.client.PropertyServiceClient;
import com.management.water.telemetry.dto.ApartmentDto;
import com.management.water.telemetry.dto.UserDto;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.telemetry.entity.SlabMonthlySummary;
import com.management.water.telemetry.exception.ApiException;
import com.management.water.telemetry.repository.DailyLogRepository;
import com.management.water.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.telemetry.repository.SlabMonthlySummaryRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyLogService {

  private static final int MAX_PAGE_SIZE = 100;
  private static final int MAX_RANGE_MONTHS = 2;

  private final SlabMonthlySummaryRepository summaryRepository;
  private final DailyLogSourceBreakdownRepository breakdownRepository;
  private final DailyLogRepository repository;
  private final PropertyServiceClient propertyClient;
  private final AuthServiceClient authClient;
  private final BillingService billingService;

  private final Counter logCreationCounter;
  private final DistributionSummary waterConsumptionSummary;

  public DailyLogService(
      SlabMonthlySummaryRepository summaryRepository,
      DailyLogSourceBreakdownRepository breakdownRepository,
      DailyLogRepository repository,
      PropertyServiceClient propertyClient,
      AuthServiceClient authClient,
      BillingService billingService,
      MeterRegistry meterRegistry) {
    this.summaryRepository = summaryRepository;
    this.breakdownRepository = breakdownRepository;
    this.repository = repository;
    this.propertyClient = propertyClient;
    this.authClient = authClient;
    this.billingService = billingService;

    this.logCreationCounter = Counter.builder("water.logs.created")
        .description("Number of daily water logs submitted")
        .tag("module", "telemetry")
        .register(meterRegistry);

    this.waterConsumptionSummary = DistributionSummary.builder("water.litres.consumed")
        .description("Track distribution of litres consumed per log")
        .register(meterRegistry);
  }

  @Transactional
  public DailyLog create(DailyLog log) {
    // Fetch apartment details via Feign to validate existence and get details for allowance
    ApartmentDto apartment;
    try {
      apartment = propertyClient.getApartmentById(log.getApartmentId());
    } catch (feign.FeignException.NotFound e) {
      throw new ApiException(HttpStatus.NOT_FOUND, "Apartment not found");
    } catch (Exception e) {
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating apartment: " + e.getMessage());
    }

    boolean exists = repository.existsByApartmentIdAndLogDate(log.getApartmentId(), log.getLogDate());
    if (exists) {
      throw new ApiException(HttpStatus.CONFLICT, "Daily log already exists for this date");
    }

    // Cache the apartment number
    log.setApartmentNumber(apartment.getNumber());

    DailyLog savedLog = repository.save(log);

    // Calculate allowance using fetched apartment type info
    double allowance = calculateAllowance(apartment, savedLog.getGuestCount());

    // Calculate daily cost and save breakdowns
    double cost = billingService.calculateDailyCost(savedLog, allowance);
    savedLog.setDayCost(cost);

    // Update monthly summary
    updateMonthlySummary(savedLog);

    DailyLog finalLog = repository.save(savedLog);

    logCreationCounter.increment();
    waterConsumptionSummary.record(finalLog.getTotalLitresConsumed());

    return finalLog;
  }

  private double calculateAllowance(ApartmentDto apartment, int guestCount) {
    if (apartment.getType() == null) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Apartment type details are missing");
    }
    int base = apartment.getType().getBaseOccupancy();
    double litresPerPerson = apartment.getType().getLitresPerPerson();
    return (base + guestCount) * litresPerPerson;
  }

  private void updateMonthlySummary(DailyLog log) {
    int year = log.getLogDate().getYear();
    int month = log.getLogDate().getMonthValue();

    List<DailyLogSourceBreakdown> breakdowns = breakdownRepository.findByDailyLogId(log.getId());

    for (DailyLogSourceBreakdown b : breakdowns) {
      SlabMonthlySummary summary =
          summaryRepository
              .findByApartmentIdAndSourceIdAndYearAndMonth(
                  log.getApartmentId(), b.getSourceId(), year, month)
              .orElseGet(
                  () -> {
                    SlabMonthlySummary s = new SlabMonthlySummary();
                    s.setApartmentId(log.getApartmentId());
                    s.setSourceId(b.getSourceId());
                    s.setYear(year);
                    s.setMonth(month);
                    s.setTotalLitres(0);
                    s.setTotalCost(0);
                    return s;
                  });

      summary.setTotalLitres(summary.getTotalLitres() + b.getLitres());
      summary.setTotalCost(summary.getTotalCost() + b.getCost());

      summaryRepository.save(summary);
    }
  }

  public Page<DailyLog> getPage(
      int page,
      int size,
      String apartmentNumber,
      LocalDate fromDate,
      LocalDate toDate,
      String sortBy,
      String sortDir) {
    validatePagination(page, size);

    Long residentApartmentId = null;
    if (isCurrentUserResident()) {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      try {
        UserDto userDto = authClient.getUserByUsername(username);
        if (userDto != null) {
          residentApartmentId = userDto.getApartmentId();
        }
      } catch (Exception e) {
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching resident details: " + e.getMessage());
      }
    }

    DateRange dateRange = resolveDateRange(fromDate, toDate);

    List<String> allowedSortFields = List.of("logDate", "totalLitresConsumed", "dayCost", "guestCount");
    String effectiveSortBy = (sortBy != null && allowedSortFields.contains(sortBy)) ? sortBy : "logDate";
    Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

    Pageable pageable =
        PageRequest.of(
            page, Math.min(size, MAX_PAGE_SIZE), Sort.by(direction, effectiveSortBy));

    return repository.findAll(
        createFilterSpecification(apartmentNumber, residentApartmentId, dateRange.fromDate(), dateRange.toDate()),
        pageable);
  }

  public Map<String, Object> getStats(
      String apartmentNumber, LocalDate fromDate, LocalDate toDate) {

    Long residentApartmentId = null;
    if (isCurrentUserResident()) {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      try {
        UserDto userDto = authClient.getUserByUsername(username);
        if (userDto != null) {
          residentApartmentId = userDto.getApartmentId();
        }
      } catch (Exception e) {
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching resident details: " + e.getMessage());
      }
    }

    DateRange dateRange = resolveDateRange(fromDate, toDate);
    DailyLogRepository.DashboardStatsProjection stats =
        repository.summarizeStats(
            residentApartmentId, normalizeApartmentNumber(apartmentNumber), dateRange.fromDate(), dateRange.toDate());

    return Map.of(
        "totalLogs", stats.getTotalLogs(),
        "totalUsage", stats.getTotalUsage(),
        "totalCost", stats.getTotalCost());
  }

  public List<Map<String, Object>> getMonthlySummary(
      String apartmentNumber, LocalDate fromDate, LocalDate toDate) {
    DateRange dateRange = resolveDateRange(fromDate, toDate);
    return repository
        .summarizeByApartment(
            null, normalizeApartmentNumber(apartmentNumber), dateRange.fromDate(), dateRange.toDate())
        .stream()
        .map(
            summary ->
                Map.<String, Object>of(
                    "apartment", summary.getApartment(),
                    "totalUsage", summary.getTotalUsage(),
                    "totalCost", summary.getTotalCost()))
        .toList();
  }

  private Specification<DailyLog> createFilterSpecification(
      String apartmentNumber, Long residentApartmentId, LocalDate fromDate, LocalDate toDate) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (residentApartmentId != null) {
        predicates.add(criteriaBuilder.equal(root.get("apartmentId"), residentApartmentId));
      } else if (Objects.nonNull(apartmentNumber) && !apartmentNumber.isBlank()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("apartmentNumber")),
                "%" + apartmentNumber.trim().toLowerCase() + "%"));
      }

      if (Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
        predicates.add(criteriaBuilder.between(root.get("logDate"), fromDate, toDate));
      } else if (Objects.nonNull(fromDate)) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("logDate"), fromDate));
      } else if (Objects.nonNull(toDate)) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("logDate"), toDate));
      }

      return predicates.isEmpty()
          ? criteriaBuilder.conjunction()
          : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private DateRange resolveDateRange(LocalDate fromDate, LocalDate toDate) {
    LocalDate effectiveToDate = Objects.requireNonNullElse(toDate, LocalDate.now());
    LocalDate effectiveFromDate =
        Objects.requireNonNullElse(fromDate, earliestAllowedDate(effectiveToDate));

    if (effectiveFromDate.isAfter(effectiveToDate)) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "fromDate must be on or before toDate");
    }

    if (effectiveFromDate.isBefore(earliestAllowedDate(effectiveToDate))) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST,
          "Date range cannot start before the first day of the month two months ago");
    }

    return new DateRange(effectiveFromDate, effectiveToDate);
  }

  private LocalDate earliestAllowedDate(LocalDate toDate) {
    return toDate.minusMonths(MAX_RANGE_MONTHS).withDayOfMonth(1);
  }

  private void validatePagination(int page, int size) {
    if (page < 0) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Page must be 0 or greater");
    }
    if (size < 1) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Page size must be at least 1");
    }
  }

  private String normalizeApartmentNumber(String apartmentNumber) {
    return Objects.nonNull(apartmentNumber) && !apartmentNumber.isBlank()
        ? apartmentNumber.trim()
        : null;
  }

  private boolean isCurrentUserResident() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_RESIDENT"));
  }

  private record DateRange(LocalDate fromDate, LocalDate toDate) {}
}
