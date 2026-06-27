package com.management.water.modules.property.controller;

import com.management.water.modules.property.dto.ApartmentCreateRequest;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.property.entity.Block;
import com.management.water.modules.property.service.ApartmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ApartmentController {

  private final ApartmentService service;

  @PostMapping
  public Apartment create(@Valid @RequestBody ApartmentCreateRequest request) {
    Apartment apartment = Apartment.builder()
            .number(request.getNumber())
            .block(Block.builder().id(request.getBlockId()).build())
            .type(ApartmentType.builder().id(request.getTypeId()).build())
            .build();
    return service.create(apartment);
  }

  @GetMapping
  public List<Apartment> getAll() {
    return service.getAll();
  }
}
