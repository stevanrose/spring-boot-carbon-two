package com.stevanrose.carbon_two.office.controller.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.office.domain.Office;
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
class OfficeControllerDeleteIntegrationTest {

  @Autowired private MockMvc mvc;

  @Autowired private OfficeRepository officeRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    officeRepository.deleteAll();
  }

  @SneakyThrows
  @Test
  void should_delete_office() {

    var office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    mvc.perform(delete("/api/offices/{id}", office.getId())).andExpect(status().isNoContent());

    assertTrue(officeRepository.findById(office.getId()).isEmpty());
  }
}
