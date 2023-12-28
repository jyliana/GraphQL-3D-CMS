package com.example.graphql.datasource.repository;

import com.example.graphql.datasource.dto.ModelDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ModelRepository extends JpaRepository<ModelDto, UUID> {

    @NotNull
    List<ModelDto> findAll();
}
