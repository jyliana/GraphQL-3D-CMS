package com.example.graphql.datasource.dto;

import com.example.graphql.types.UserCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
public class UserDto {

  @Id
  @GeneratedValue
  private UUID id;
  private String username;
  private String email;
  private String hashedPassword;
  @CreationTimestamp
  private LocalDateTime creationTimestamp;
  private String displayName;
  @Enumerated(EnumType.STRING)
  private UserCategory category;

  @OneToMany(mappedBy = "user")
  @BatchSize(size = 50)
  @OrderBy("tradeDate desc")
  private List<OrderDto> orders;

}



