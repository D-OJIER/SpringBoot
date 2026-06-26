package com.management.water.property.controller;

import com.management.water.property.dto.ApartmentCreateRequest;
import com.management.water.property.entity.Apartment;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.entity.Block;
import com.management.water.property.service.ApartmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor
public class ApartmentController {

  private final ApartmentService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Apartment create(@Valid @RequestBody ApartmentCreateRequest request) {
    Apartment apartment = Apartment.builder()
            .number(request.getNumber())
            .block(Block.builder().id(request.getBlockId()).build())
            .type(ApartmentType.builder().id(request.getTypeId()).build())
            .build();
    return service.create(apartment);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<Apartment> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public Apartment getById(@PathVariable Long id) {
    return service.getById(id);
  }
}
