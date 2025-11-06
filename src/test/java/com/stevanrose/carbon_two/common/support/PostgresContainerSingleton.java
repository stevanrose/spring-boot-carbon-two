package com.stevanrose.carbon_two.common.support;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerSingleton extends PostgreSQLContainer<PostgresContainerSingleton> {

  private static final PostgresContainerSingleton INSTANCE = new PostgresContainerSingleton();

  private PostgresContainerSingleton() {
    super("postgres:16-alpine");
    withDatabaseName("carbon_two_test");
    withUsername("test");
    withPassword("test");
  }

  public static PostgreSQLContainer<?> getInstance() {
    return INSTANCE;
  }

  static {
    INSTANCE.start();
  }
}
