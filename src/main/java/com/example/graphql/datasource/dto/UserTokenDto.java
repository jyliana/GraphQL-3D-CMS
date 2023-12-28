package com.example.graphql.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_token")
public class UserTokenDto {
  @Id
  private UUID userId;
  private String authToken;
  @CreationTimestamp
  private LocalDateTime creationTimestamp;
  private LocalDateTime expiryTimestamp;
}
