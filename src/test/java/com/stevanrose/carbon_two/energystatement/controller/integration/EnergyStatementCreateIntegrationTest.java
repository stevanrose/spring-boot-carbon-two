package com.stevanrose.carbon_two.energystatement.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import com.stevanrose.carbon_two.energystatement.repository.EnergyStatementRepository;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EnergyStatementCreateIntegrationTest {

  @Autowired private MockMvc mvc;

  @Autowired private OfficeRepository officeRepository;
  @Autowired private EnergyStatementRepository energyStatementRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    energyStatementRepository.deleteAll();
    officeRepository.deleteAll();
  }

  @SneakyThrows
  @Test
  void should_create_office_energy_statement() {

    var office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    EnergyStatementRequest request = new EnergyStatementRequest();
    request.setYear(2025);
    request.setMonth(10);
    request.setElectricityKwh(1234.0);
    request.setHeatingFuelType(HeatingFuelType.NONE);

    mvc.perform(
            post("/api/offices/{id}/energy-statements", office.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.officeId").value(office.getId().toString()))
        .andExpect(jsonPath("$.year").value(2025))
        .andExpect(jsonPath("$.month").value(10))
        .andExpect(jsonPath("$.electricityKwh").value(1234.0))
        .andExpect(jsonPath("$.heatingFuelType").value("NONE"));
  }

  @AfterEach
  void tearDown() {
    energyStatementRepository.deleteAll();
    officeRepository.deleteAll();
  }
}
