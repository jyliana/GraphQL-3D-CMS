package com.example.graphql.datasource.dto;

import com.example.graphql.types.ExecutionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "execution")
public class ExecutionDto {

  @Id
  @GeneratedValue
  private UUID id;
  private UUID workerId;
  private UUID orderId;
  private UUID modelId;
  @CreationTimestamp
  private LocalDateTime startDate;
  private Integer total;
  private Integer completed;
  @Enumerated(EnumType.STRING)
  private ExecutionStatus status;
  private Integer progress;
}
