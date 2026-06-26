package com.management.water.property.service;

import com.management.water.property.entity.Block;
import com.management.water.property.repository.BlockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {

  private final BlockRepository repository;

  public Block create(Block block) {
    return repository.save(block);
  }

  public List<Block> getAll() {
    return repository.findAll();
  }

  public void deleteById(long id) {
    repository.deleteById(id);
  }
}
