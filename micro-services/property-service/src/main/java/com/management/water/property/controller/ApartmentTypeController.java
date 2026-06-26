package com.management.water.property.controller;

import com.management.water.property.dto.ApartmentTypeCreateRequest;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.service.ApartmentTypeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartment-types")
@RequiredArgsConstructor
public class ApartmentTypeController {

  private final ApartmentTypeService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ApartmentType create(@Valid @RequestBody ApartmentTypeCreateRequest request) {
    ApartmentType type = new ApartmentType();
    type.setName(request.getName());
    type.setBaseOccupancy(request.getBaseOccupancy());
    type.setLitresPerPerson(request.getLitresPerPerson());
    return service.create(type);
  }

  @GetMapping
  public List<ApartmentType> getAll() {
    return service.getAll();
  }
}
