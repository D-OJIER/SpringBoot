package com.management.water.modules.property.controller;

import com.management.water.modules.property.dto.ApartmentCreateRequest;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.property.entity.Block;
import com.management.water.modules.property.service.ApartmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor
public class ApartmentController {

  private final ApartmentService service;

  @PostMapping
  public Apartment create(@Valid @RequestBody ApartmentCreateRequest request) {
    Apartment apartment = new Apartment();
    apartment.setNumber(request.getNumber());
    Block block = new Block();
    block.setId(request.getBlockId());
    apartment.setBlock(block);
    ApartmentType type = new ApartmentType();
    type.setId(request.getTypeId());
    apartment.setType(type);
    return service.create(apartment);
  }

  @GetMapping
  public List<Apartment> getAll() {
    return service.getAll();
  }
}
