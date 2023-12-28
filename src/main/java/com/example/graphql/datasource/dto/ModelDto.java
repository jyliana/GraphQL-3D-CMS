package com.example.graphql.datasource.dto;

import com.example.graphql.types.Material;
import com.example.graphql.types.Type;
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
@Table(name = "model")
public class ModelDto {

  @Id
  @GeneratedValue
  private UUID id;
  private String name;
  private String description;
  @Enumerated(EnumType.STRING)
  private Material material;
  @Enumerated(EnumType.STRING)
  private Type type;
  private String settings;
}

