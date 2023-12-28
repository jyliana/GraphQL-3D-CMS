package com.example.graphql.datasource.dto;

import com.example.graphql.types.OrderDetailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "order_details")
public class OrderDetailDto {

  @Id
  @GeneratedValue
  private UUID id;
  private UUID modelId;
  private Integer amount;
  private Integer available;
  @Enumerated(EnumType.STRING)
  private OrderDetailStatus status;
  private Integer progress;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderDto order;
}
