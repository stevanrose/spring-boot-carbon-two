package com.stevanrose.carbon_two.energystatement.controller.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import com.stevanrose.carbon_two.energystatement.repository.EnergyStatementRepository;
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
public class EnergyStatementControllerDeleteIntegrationTest {

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
  void should_delete_energy_statement_and_return_no_content() {

    var office =
        officeRepository.save(
            Office.builder().code("ENG-01").name("Engineering").gridRegionCode("GB-LDN").build());

    energyStatementRepository.save(
        EnergyStatement.builder()
            .office(office)
            .year(2025)
            .month(10)
            .electricityKwh(1000.0)
            .heatingFuelType(HeatingFuelType.NONE)
            .build());

    mvc.perform(
            delete(
                "/api/offices/{officeId}/energy-statements/{year}/{month}",
                office.getId(),
                2025,
                10))
        .andExpect(status().isNoContent());

    assertTrue(
        energyStatementRepository
            .findByOfficeIdAndYearAndMonth(office.getId(), 2025, 10)
            .isEmpty());
  }
}
