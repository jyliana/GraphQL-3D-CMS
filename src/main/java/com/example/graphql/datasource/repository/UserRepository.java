package com.example.graphql.datasource.repository;

import com.example.graphql.datasource.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserDto, UUID> {

  List<UserDto> findAllByOrderByCreationTimestampDesc();

  Optional<UserDto> findUserDtoByUsername(String username);

  Optional<UserDto> findByEmailIgnoreCase(String email);

  @Query(nativeQuery = true, value = """
          select u.* from users u
          join user_token ut
          on u.id = ut.user_id
          where ut.auth_token = ?
          and ut.expiry_timestamp > current_timestamp
          """)
  Optional<UserDto> findUserByToken(String authToken);

  Optional<UserDto> findUserByEmail(String email);

}
