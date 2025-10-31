package com.stevanrose.carbon_two.office.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
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
class OfficeControllerTest {

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

  @SneakyThrows
  @Test
  void should_update_office() {

    Office office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    OfficeRequest request = new OfficeRequest();
    request.setCode("LON-02");
    request.setName("London Office");
    request.setAddress("Updated Address");
    request.setGridRegionCode("GB-LDN-1");
    request.setFloorAreaM2(1500.00);

    mvc.perform(
            put("/api/offices/{id}", office.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(office.getId().toString()))
        .andExpect(jsonPath("$.code").value(request.getCode()))
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.address").value(request.getAddress()))
        .andExpect(jsonPath("$.gridRegionCode").value(request.getGridRegionCode()))
        .andExpect(jsonPath("$.floorAreaM2").value(request.getFloorAreaM2()));
  }
}
