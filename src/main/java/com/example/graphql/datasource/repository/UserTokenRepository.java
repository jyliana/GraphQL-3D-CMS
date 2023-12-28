package com.example.graphql.datasource.repository;

import com.example.graphql.datasource.dto.UserTokenDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenDto, UUID> {
}

