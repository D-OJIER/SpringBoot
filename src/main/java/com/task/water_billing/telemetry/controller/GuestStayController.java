package com.task.water_billing.telemetry.controller;

import com.task.water_billing.telemetry.entity.GuestStay;
import com.task.water_billing.telemetry.service.GuestStayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestStayController {

    private final GuestStayService service;

    @PostMapping
    public GuestStay create(@RequestParam Long apartmentId, @RequestBody GuestStay stay) {
        return service.create(apartmentId, stay);
    }
}