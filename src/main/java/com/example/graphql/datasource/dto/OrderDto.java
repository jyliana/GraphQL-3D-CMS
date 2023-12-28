package com.example.graphql.datasource.dto;

import com.example.graphql.types.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "orders")
public class OrderDto {

  @Id
  @GeneratedValue
  private UUID id;
  @CreationTimestamp
  private LocalDateTime tradeDate;
  private LocalDateTime dueDate;
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
  private String deliveryAddress;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserDto user;

  @OneToMany(mappedBy = "order")
  @Fetch(FetchMode.SUBSELECT)
  private List<OrderDetailDto> ordersDetails;

}
