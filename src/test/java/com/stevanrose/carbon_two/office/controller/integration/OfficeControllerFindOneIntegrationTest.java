package com.stevanrose.carbon_two.office.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class OfficeControllerFindOneIntegrationTest {

  @Autowired private MockMvc mvc;

  @Autowired private OfficeRepository officeRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    officeRepository.deleteAll();
  }

  @SneakyThrows
  @Test
  void should_find_one_office_entity_and_return_ok() {
    Office office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(300.0)
                .build());

    mvc.perform(get("/api/offices/{id}", office.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(office.getId().toString()))
        .andExpect(jsonPath("$.code").value("LON-01"))
        .andExpect(jsonPath("$.name").value("London HQ"))
        .andExpect(jsonPath("$.address").value("10 Downing Street"))
        .andExpect(jsonPath("$.gridRegionCode").value("GB-LDN"))
        .andExpect(jsonPath("$.floorAreaM2").value(300.0));
  }
}
