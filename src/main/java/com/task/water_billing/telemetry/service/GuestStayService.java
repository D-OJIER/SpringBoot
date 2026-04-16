package com.task.water_billing.telemetry.service;

import com.task.water_billing.property.repository.ApartmentRepository;
import com.task.water_billing.telemetry.entity.GuestStay;
import com.task.water_billing.telemetry.repository.GuestStayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestStayService {

    private final GuestStayRepository repo;
    private final ApartmentRepository apartmentRepo;

    public GuestStay create(Long apartmentId, GuestStay stay) {

        var apartment = apartmentRepo.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        stay.setApartment(apartment);
        return repo.save(stay);
    }
}