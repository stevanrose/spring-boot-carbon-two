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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OfficeControllerListIntegrationTest {

  @Autowired private MockMvc mvc;

  @Autowired private OfficeRepository officeRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    officeRepository.deleteAll();
  }

  @SneakyThrows
  @Test
  void should_list_offices_with_pagination() {
    Office office1 =
        Office.builder()
            .code("LON-01")
            .name("London HQ")
            .address("10 Downing Street")
            .gridRegionCode("GB-LDN")
            .floorAreaM2(300.0)
            .build();

    officeRepository.save(office1);

    mvc.perform(
            get("/api/offices")
                .param("page", "0")
                .param("size", "1")
                .param("sort", "name,asc")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].code").value("LON-01"))
        .andExpect(jsonPath("$.content[0].name").value("London HQ"));
  }
}
