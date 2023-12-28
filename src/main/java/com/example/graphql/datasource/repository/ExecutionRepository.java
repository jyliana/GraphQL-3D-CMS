package com.example.graphql.datasource.repository;

import com.example.graphql.datasource.dto.ExecutionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExecutionRepository extends JpaRepository<ExecutionDto, UUID> {

  List<ExecutionDto> findByOrderIdAndModelId(UUID orderId, UUID modelId);
}
