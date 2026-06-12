package com.management.water.modules.waterconfig.service;

import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterRateService {

    private final WaterRateRepository rateRepository;
    private final WaterSourceRepository sourceRepository;

    public WaterRate create(WaterRate rate) {

        WaterSource source = sourceRepository.findById(rate.getSource().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

        rate.setSource(source);

        if (rate.getMinLitres() > rate.getMaxLitres()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid slab range");
        }

        if (rate.getEffectiveFrom() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date is required");
        }

        if (rate.getEffectiveFrom().isAfter(java.time.LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date cannot be in the future");
        }

        if (rate.getEffectiveTo() != null && rate.getEffectiveTo().isBefore(rate.getEffectiveFrom())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Effective to date must be on or after effective from date");
        }

        validateSlabs(source.getId(), rate);

        return rateRepository.save(rate);
    }

    private void validateSlabs(Long sourceId, WaterRate candidate) {

        List<WaterRate> rates =
                rateRepository.findBySourceIdOrderByMinLitresAsc(sourceId);

        List<WaterRate> allRates = new java.util.ArrayList<>(rates);
        allRates.add(candidate);
        allRates.sort((a, b) -> Double.compare(a.getMinLitres(), b.getMinLitres()));

        if (allRates.isEmpty()) return;

        if (allRates.get(0).getMinLitres() != 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The first water rate slab must start at 0 litres");
        }

        for (int i = 0; i < allRates.size() - 1; i++) {

            WaterRate current = allRates.get(i);
            WaterRate next = allRates.get(i + 1);

            if (current.getMaxLitres() + 1 != next.getMinLitres()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs are not continuous");
            }

            if (current.getMaxLitres() >= next.getMinLitres()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs overlap");
            }
        }
    }

    public void deleteById(Long id) {

        WaterRate rate =
                rateRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Rate not found"));
        if (
            rate.getEffectiveFrom()
                .isBefore(java.time.LocalDate.now())
        ) {

            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Cannot delete historical rates"
            );
        }

        rateRepository.deleteById(id);
    }
    
    public List<WaterRate> getAll() {
        return rateRepository.findAll();
    }

    public WaterRate update(Long id, WaterRate updated) {

        WaterRate existing = rateRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rate not found"));

        WaterSource source = sourceRepository.findById(updated.getSource().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

        if (updated.getMinLitres() > updated.getMaxLitres()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid slab range");
        }

        if (updated.getEffectiveFrom() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date is required");
        }

        if (updated.getEffectiveFrom().isAfter(java.time.LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date cannot be in the future");
        }

        if (updated.getEffectiveTo() != null && updated.getEffectiveTo().isBefore(updated.getEffectiveFrom())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Effective to date must be on or after effective from date");
        }

        List<WaterRate> rates =
                rateRepository.findBySourceIdOrderByMinLitresAsc(source.getId());

        for (WaterRate rate : rates) {

            if (rate.getId().equals(id)) {
                continue;
            }

            boolean overlap =
                    updated.getMinLitres() <= rate.getMaxLitres()
                    && updated.getMaxLitres() >= rate.getMinLitres();

            if (overlap) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Slab overlaps existing slabs");
            }
        }

        existing.setMinLitres(updated.getMinLitres());
        existing.setMaxLitres(updated.getMaxLitres());
        existing.setRatePerLitre(updated.getRatePerLitre());
        existing.setSource(source);

        return rateRepository.save(existing);
    }
}
