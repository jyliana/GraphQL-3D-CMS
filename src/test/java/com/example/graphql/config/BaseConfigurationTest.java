package com.example.graphql.config;

import com.example.graphql.GraphqlApplication;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GraphqlApplication.class)
public class BaseConfigurationTest {

  @ClassRule
  public static PostgreSQLTestContainer testContainer = PostgreSQLTestContainer.getInstance();

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.datasource.url", testContainer::getJdbcUrl);
    dynamicPropertyRegistry.add("spring.datasource.username", testContainer::getUsername);
    dynamicPropertyRegistry.add("spring.datasource.password", testContainer::getPassword);
  }

  @BeforeAll
  public static void initAll() {
    testContainer.start();
  }

  @AfterAll
  public static void afterAll() {
    testContainer.stop();
  }

}
