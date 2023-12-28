package com.example.graphql.datasource.repository;

import com.example.graphql.datasource.dto.OrderDto;
import com.example.graphql.datasource.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderDto, UUID> {

  List<OrderDto> findAllByUser(UserDto user);

}
