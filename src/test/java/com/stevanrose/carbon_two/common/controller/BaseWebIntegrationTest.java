package com.stevanrose.carbon_two.common.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.common.support.PostgresContainerSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public abstract class BaseWebIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES = PostgresContainerSingleton.getInstance();

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry r) {
    // Primary datasource → container
    r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    r.add("spring.datasource.username", POSTGRES::getUsername);
    r.add("spring.datasource.password", POSTGRES::getPassword);
    r.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);

    // Liquibase → same container (avoid running against local DB)
    r.add("spring.liquibase.enabled", () -> true);
    r.add("spring.liquibase.url", POSTGRES::getJdbcUrl);
    r.add("spring.liquibase.user", POSTGRES::getUsername);
    r.add("spring.liquibase.password", POSTGRES::getPassword);
    r.add("spring.liquibase.change-log", () -> "classpath:/db/changelog/changelog-master.json");

    // Hibernate schema control
    r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    r.add("spring.jpa.open-in-view", () -> false);
  }

  @Autowired protected MockMvc mvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected JdbcTemplate jdbcTemplate;

  @BeforeEach
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void cleanDatabase() {
    // If Liquibase hasn’t created tables yet (first test in class), ignore errors.
    try {
      try {
        // Avoid indefinite waits when another session holds locks
        jdbcTemplate.execute("SET lock_timeout = '5s'");

        // Truncate in dependency-safe order (child → parent) to minimize locking
        //        jdbcTemplate.execute("TRUNCATE TABLE commutesurvey CASCADE");
        //        jdbcTemplate.execute("TRUNCATE TABLE employee CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE officeenergystatement CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE office CASCADE");

      } catch (Exception ignored) {
        // First class that runs may hit this before Liquibase creates tables—safe to ignore.
      } finally {
        // Restore default (optional)
        jdbcTemplate.execute("SET lock_timeout = '0'");
      }
    } catch (Exception ignored) {
      /* ok during very first run */
    }
  }

  protected String json(Object value) throws Exception {
    return objectMapper.writeValueAsString(value);
  }

  protected ResultActions postJson(String url, Object body) throws Exception {
    return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json(body)));
  }

  protected ResultActions putJson(String url, Object body) throws Exception {
    return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json(body)));
  }

  protected ResultActions getJson(String url) throws Exception {
    return mvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
  }

  protected ResultActions deleteJson(String url) throws Exception {
    return mvc.perform(delete(url));
  }
}
