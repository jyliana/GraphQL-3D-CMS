package com.example.graphql.service;

import com.example.graphql.datasource.dto.UserDto;
import com.example.graphql.datasource.dto.UserTokenDto;
import com.example.graphql.datasource.repository.UserRepository;
import com.example.graphql.datasource.repository.UserTokenRepository;
import com.example.graphql.exception.ProblemAuthenticationException;
import com.example.graphql.utils.HashUtil;
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private UserTokenRepository userTokenRepository;

  public List<UserDto> getAllUsers() {
    return userRepository.findAllByOrderByCreationTimestampDesc();
  }

  public Page<UserDto> findUsers(Integer page, Integer size) {
    var pageable = PageRequest.of(
            Optional.ofNullable(page).orElse(0),
            Optional.ofNullable(size).orElse(20),
            Sort.by("email")
    );

    return userRepository.findAll(pageable);
  }

  public UserDto getUser(String username) {
    return userRepository.findUserDtoByUsername(username)
            .orElseThrow(() -> new DgsEntityNotFoundException("No user with username " + username));
  }

  public UserDto createUser(UserDto user) {
    return userRepository.save(user);
  }

  public UserTokenDto login(String email, String password) {
    var result = userRepository.findByEmailIgnoreCase(email);

    if (result.isEmpty() ||
            !HashUtil.isBcryptMatch(password, result.get().getHashedPassword())) {
      throw new ProblemAuthenticationException();
    }

    var randomAuthToken = RandomStringUtils.randomAlphanumeric(40);
    return refreshToken(result.get().getId(), randomAuthToken);
  }

  private UserTokenDto refreshToken(UUID userId, String authToken) {
    var now = LocalDateTime.now();

    var token = UserTokenDto.builder()
            .userId(userId)
            .authToken(authToken)
            .creationTimestamp(now)
            .expiryTimestamp(now.plusHours(2))
            .build();

    return userTokenRepository.save(token);
  }

  public UserDto findUserByAuthToken(String authToken) {
    return userRepository.findUserByToken(authToken).orElseThrow(ProblemAuthenticationException::new);
  }

  public UserDto findUserByEmail(String email) {
    return userRepository.findUserByEmail(email)
            .orElseThrow(() -> new DgsEntityNotFoundException(String.format("User with email %s cannot be found", email)));
  }
}
