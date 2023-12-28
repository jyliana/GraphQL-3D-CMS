package com.example.graphql.datasource.repository;

import com.example.graphql.datasource.dto.OrderDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetailDto, UUID> {

  List<OrderDetailDto> findAllByAvailableNot(Integer value);

  Optional<OrderDetailDto> findByOrderIdAndModelId(UUID orderId, UUID modelId);
}
