package com.example.graphql.resolver;

import com.example.graphql.config.BaseConfigurationTest;
import com.example.graphql.datasource.dto.UserDto;
import com.example.graphql.datasource.repository.UserRepository;
import com.example.graphql.types.User;
import com.example.graphql.types.UserCategory;
import com.example.graphql.types.UserResponse;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

class UserDataResolverTest extends BaseConfigurationTest {

  @Autowired
  private DgsQueryExecutor executor;
  @Autowired
  private UserRepository userRepository;

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void testCreateUser() {
    // given
    @Language("GraphQL") var query = """
            mutation {
              userCreate(
                user: {
                  username: "test"
                  email: "test@email.com"
                  displayName: "Test"
                  password: "password"
                  category: CUSTOMER
                }
              ) {
                user {
                  id
                  username
                  email
                  createDateTime
                  displayName
                  category
                  orders{
                    status
                  }
                }
                authToken {
                  authToken
                  expiryTime
                }
              }
            }
            """;

    // when
    var returnedUser = executor.executeAndExtractJsonPathAsObject(query, "data.userCreate.user", User.class);

    // then
    var savedUser = userRepository.findUserDtoByUsername("test")
            .orElseThrow(() -> new DgsEntityNotFoundException("No user with such username "));

    assertSoftly((all) -> {
      assertNotNull(savedUser);
      assertNotEquals(savedUser.getHashedPassword(), "password");
      assertNotNull(returnedUser.getId());
      assertNotNull(returnedUser.getCreateDateTime());
      assertNotNull(returnedUser.getCreateDateTime());
      assertEquals(returnedUser.getUsername(), "test");
      assertEquals(returnedUser.getEmail(), "test@email.com");
      assertEquals(returnedUser.getDisplayName(), "Test");
      assertEquals(returnedUser.getCategory(), UserCategory.CUSTOMER);
    });
  }

  @Test
  @SneakyThrows
  @Transactional
  void testGetUserByName() {
    // given
    UserDto userDto = UserDto.builder()
            .username("test")
            .email("test@email.com")
            .category(UserCategory.CUSTOMER)
            .displayName("Test")
            .hashedPassword("password")
            .build();

    userRepository.save(userDto);

    @Language("GraphQL") var query = """
            query getUser{
                 user(username: "test") {
                   id
                   username
                   email
                   createDateTime
                   displayName
                   category
                   orders{
                    status
                  }
                 }
               }
            """;

    // when
    var user = executor.executeAndExtractJsonPathAsObject(query, "data.user", User.class);

    // then
    assertSoftly((all) -> {
      assertNotNull(user.getId());
      assertEquals(user.getUsername(), "test");
      assertEquals(user.getEmail(), "test@email.com");
      assertEquals(user.getDisplayName(), "Test");
      assertEquals(user.getCategory(), UserCategory.CUSTOMER);
      assertNotNull(user.getCreateDateTime());
      assertEquals(user.getOrders().size(), 0);
    });
  }

  @Test
  @SneakyThrows
  @Transactional
  void testGetUsers() {
    // given
    UserDto user1 = UserDto.builder()
            .username("test1")
            .email("test@email1.com")
            .category(UserCategory.CUSTOMER)
            .displayName("Test")
            .hashedPassword("password")
            .build();

    UserDto user2 = UserDto.builder()
            .username("test2")
            .email("test@email2.com")
            .category(UserCategory.CUSTOMER)
            .displayName("Test")
            .hashedPassword("password")
            .build();

    userRepository.saveAll(List.of(user1, user2));

    @Language("GraphQL") var query = """
            query getUsers{
                  users {
                    id
                    username
                    email
                    createDateTime
                    displayName
                    category
                    orders {
                      id
                      userId
                      tradeDateTime
                      dueDateTime
                      status
                      deliveryAddress
                      orderDetails{
                        progress
                      }
                    }
                  }
                }
            """;

    // when
    List<User> list = executor.executeAndExtractJsonPathAsObject(query, "data.users[*]", new TypeRef<>() {
    });

    // then
    assertSoftly((all) -> {
      assertNotNull(list);
      assertEquals(list.size(), 2);
      assertThat(list.get(0).getUsername()).contains("test");
      assertThat(list.get(1).getUsername()).contains("test");
    });
  }

  @Test
  @SneakyThrows
  @Transactional
  void testGetUserLogin() {
    // given
    UserDto userDto = UserDto.builder()
            .username("test")
            .email("test@email.com")
            .category(UserCategory.CUSTOMER)
            .displayName("Test")
            .hashedPassword("$2y$05$07xsx.a7e7LAcdu8gfHRqu/7k/osmgI2igVXt7MsndBQ0wGMpxpJ2")
            .build();

    userRepository.save(userDto);

    @Language("GraphQL") var query = """
            mutation userLogin{
                  userLogin(user: { email: "test@email.com", password: "password" }) {
                    user {
                      id
                      username
                      email
                      createDateTime
                      displayName
                      category
                    }
                    authToken {
                      authToken
                      expiryTime
                    }
                  }
                }
            """;

    // when
    var response = executor.executeAndExtractJsonPathAsObject(query, "data.userLogin", UserResponse.class);
    var user = response.getUser();
    var token = response.getAuthToken();

    // then
    assertSoftly((all) -> {
      assertNotNull(user);
      assertNotNull(token);
      assertNotNull(token.getAuthToken());
      assertNotNull(token.getExpiryTime());
      assertNotNull(user.getId());
      assertEquals(user.getUsername(), "test");
      assertEquals(user.getEmail(), "test@email.com");
      assertEquals(user.getDisplayName(), "Test");
      assertEquals(user.getCategory(), UserCategory.CUSTOMER);
      assertNotNull(user.getCreateDateTime());
    });
  }


}