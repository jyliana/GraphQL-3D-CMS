package com.example.graphql.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLTestContainer extends PostgreSQLContainer<PostgreSQLTestContainer> {
  private static final String IMAGE_VERSION = "postgres:11.1";
  private static PostgreSQLTestContainer container;

  private PostgreSQLTestContainer() {
    super(IMAGE_VERSION);
  }

  public static PostgreSQLTestContainer getInstance() {
    if (container == null) {
      container = new PostgreSQLTestContainer();
      container.withInitScript("db.sql");
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("DB_URL", container.getJdbcUrl());
    System.setProperty("DB_USERNAME", container.getUsername());
    System.setProperty("DB_PASSWORD", container.getPassword());
  }

  @Override
  public void stop() {

  }
}
