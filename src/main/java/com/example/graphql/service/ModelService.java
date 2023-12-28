package com.example.graphql.service;

import com.example.graphql.datasource.dto.ModelDto;
import com.example.graphql.datasource.repository.ModelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ModelService {

  private ModelRepository repository;

  public ModelDto createDetail(ModelDto detail) {
    return repository.save(detail);
  }

  public List<ModelDto> getAllModels() {
    return repository.findAll();
  }
}
