package com.management.water.property.controller;

import com.management.water.property.dto.BlockCreateRequest;
import com.management.water.property.entity.Block;
import com.management.water.property.service.BlockService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

  private final BlockService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Block create(@Valid @RequestBody BlockCreateRequest request) {
    Block block = new Block();
    block.setName(request.getName());
    return service.create(block);
  }

  @GetMapping
  public List<Block> getAll() {
    return service.getAll();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable long id) {
    service.deleteById(id);
  }
}
