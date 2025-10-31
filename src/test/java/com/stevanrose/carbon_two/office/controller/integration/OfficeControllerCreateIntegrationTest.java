package com.stevanrose.carbon_two.office.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OfficeControllerCreateIntegrationTest {

  @Autowired private MockMvc mvc;

  @Autowired private OfficeRepository officeRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    officeRepository.deleteAll();
  }

  @SneakyThrows
  @Test
  void should_create_office() {

    String reqJson =
        """
                    {
                      "code": "LON-01",
                      "name": "London HQ",
                      "address": "10 Downing Street",
                      "gridRegionCode": "GB-LDN",
                      "floorAreaM2": 300.0
                    }
                """;

    mvc.perform(post("/api/offices").contentType("application/json").content(reqJson))
        .andExpect(status().isCreated());
  }
}
