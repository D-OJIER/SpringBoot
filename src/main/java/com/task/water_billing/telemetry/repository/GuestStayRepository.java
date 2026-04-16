package com.task.water_billing.telemetry.repository;

import com.task.water_billing.telemetry.entity.GuestStay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface GuestStayRepository extends JpaRepository<GuestStay, Long> {
    List<GuestStay> findByApartmentId(Long apartmentId);

}
