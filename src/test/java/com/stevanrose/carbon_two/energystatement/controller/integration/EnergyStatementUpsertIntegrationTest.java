package com.stevanrose.carbon_two.energystatement.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
public class EnergyStatementUpsertIntegrationTest {

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
            put(
                    "/api/offices/{officeId}/energy-statements/{year}/{month}",
                    office.getId(),
                    request.getYear(),
                    request.getMonth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @SneakyThrows
  @Test
  void should_update_existing_office_energy_statement() {

    var office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    var energyStatement =
        energyStatementRepository.save(
            com.stevanrose.carbon_two.energystatement.domain.EnergyStatement.builder()
                .office(office)
                .year(2025)
                .month(10)
                .electricityKwh(1234.0)
                .heatingFuelType(HeatingFuelType.NONE)
                .build());

    EnergyStatementRequest request = new EnergyStatementRequest();
    request.setYear(2025);
    request.setMonth(10);
    request.setElectricityKwh(1500.0);
    request.setHeatingFuelType(HeatingFuelType.GAS);

    mvc.perform(
            put(
                    "/api/offices/{officeId}/energy-statements/{year}/{month}",
                    office.getId(),
                    request.getYear(),
                    request.getMonth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.heatingFuelType").value("GAS"));
  }

  @AfterEach
  void tearDown() {
    energyStatementRepository.deleteAll();
    officeRepository.deleteAll();
  }
}
