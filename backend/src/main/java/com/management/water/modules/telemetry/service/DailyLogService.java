package com.management.water.modules.telemetry.service;

import com.management.water.modules.billing.entity.SlabMonthlySummary;
import com.management.water.modules.billing.repository.SlabMonthlySummaryRepository;
import com.management.water.modules.billing.service.BillingService;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.modules.telemetry.repository.DailyLogRepository;
import com.management.water.modules.telemetry.repository.DailyLogSourceBreakdownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_RANGE_MONTHS = 2;

    private final SlabMonthlySummaryRepository summaryRepository;
    private final DailyLogSourceBreakdownRepository breakdownRepository;
    private final DailyLogRepository repository;
    private final ApartmentRepository apartmentRepository;
    private final BillingService billingService;

    public DailyLog create(DailyLog log) {

        Apartment apartment = apartmentRepository.findById(log.getApartment().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Apartment not found"));

        boolean exists = repository.existsByApartmentIdAndLogDate(
                apartment.getId(),
                log.getLogDate());

        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT, "Daily log already exists for this date");
        }

        log.setApartment(apartment);

        DailyLog savedLog = repository.save(log);
        double cost = billingService.calculateDailyCost(savedLog);
        savedLog.setDayCost(cost);
        updateMonthlySummary(savedLog);
        return repository.save(savedLog);
    }

    private void updateMonthlySummary(DailyLog log) {

        int year = log.getLogDate().getYear();
        int month = log.getLogDate().getMonthValue();

        List<DailyLogSourceBreakdown> breakdowns = breakdownRepository.findByDailyLogId(log.getId());

        for (DailyLogSourceBreakdown b : breakdowns) {

            SlabMonthlySummary summary = summaryRepository
                    .findByApartmentIdAndSourceIdAndYearAndMonth(
                            log.getApartment().getId(),
                            b.getSource().getId(),
                            year,
                            month)
                    .orElseGet(() -> {
                        SlabMonthlySummary s = new SlabMonthlySummary();
                        s.setApartment(log.getApartment());
                        s.setSource(b.getSource());
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
            LocalDate toDate) {
        validatePagination(page, size);
        DateRange dateRange = resolveDateRange(fromDate, toDate);
        Pageable pageable = PageRequest.of(
                page,
                Math.min(size, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "logDate"));
        return repository.findAll(
                createFilterSpecification(apartmentNumber, dateRange.fromDate(), dateRange.toDate()),
                pageable);
    }

    public Map<String, Object> getStats(
            String apartmentNumber,
            LocalDate fromDate,
            LocalDate toDate) {
        DateRange dateRange = resolveDateRange(fromDate, toDate);
        DailyLogRepository.DashboardStatsProjection stats = repository.summarizeStats(
                normalizeApartmentNumber(apartmentNumber),
                dateRange.fromDate(),
                dateRange.toDate());

        return Map.of(
                "totalLogs", stats.getTotalLogs(),
                "totalUsage", stats.getTotalUsage(),
                "totalCost", stats.getTotalCost());
    }

    public List<Map<String, Object>> getMonthlySummary(
            String apartmentNumber,
            LocalDate fromDate,
            LocalDate toDate) {
        DateRange dateRange = resolveDateRange(fromDate, toDate);
        return repository.summarizeByApartment(
                        normalizeApartmentNumber(apartmentNumber),
                        dateRange.fromDate(),
                        dateRange.toDate())
                .stream()
                .map(summary -> Map.<String, Object>of(
                        "apartment", summary.getApartment(),
                        "totalUsage", summary.getTotalUsage(),
                        "totalCost", summary.getTotalCost()))
                .toList();
    }

    private Specification<DailyLog> createFilterSpecification(
            String apartmentNumber,
            LocalDate fromDate,
            LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(apartmentNumber) && !apartmentNumber.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.join("apartment").get("number")),
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
        LocalDate effectiveFromDate = Objects.requireNonNullElse(
                fromDate,
                earliestAllowedDate(effectiveToDate));

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

    private record DateRange(LocalDate fromDate, LocalDate toDate) {
    }
}
