package com.stevanrose.carbon_two.energystatement.controller.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.controller.integration.BaseControllerIntegrationTest;
import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import com.stevanrose.carbon_two.energystatement.repository.EnergyStatementRepository;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class EnergyStatementControllerIntegrationTest extends BaseControllerIntegrationTest {

  @Autowired private OfficeRepository officeRepository;
  @Autowired private EnergyStatementRepository energyStatementRepository;

  @Nested
  class Upsert {

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

      EnergyStatementRequest request =
          new EnergyStatementRequest(
              2025, 10, 1234.0, HeatingFuelType.GAS, 123.0, 123.0, "No notes");

      String uri = String.format("/api/offices/%s/energy-statements", office.getId());

      putJson(uri, request).andExpect(status().isCreated());
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

      energyStatementRepository.save(
          com.stevanrose.carbon_two.energystatement.domain.EnergyStatement.builder()
              .office(office)
              .year(2025)
              .month(10)
              .electricityKwh(1234.0)
              .heatingFuelType(HeatingFuelType.NONE)
              .build());

      EnergyStatementRequest request =
          new EnergyStatementRequest(
              2025, 10, 1234.0, HeatingFuelType.GAS, 123.0, 123.0, "No notes");

      String uri = String.format("/api/offices/%s/energy-statements", office.getId());

      putJson(uri, request)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.heatingFuelType").value("GAS"));
    }
  }

  @Nested
  class List {

    @SneakyThrows
    @Test
    void should_list_energy_statements_with_pagination() {

      var office =
          officeRepository.save(
              Office.builder()
                  .code("LON-01")
                  .name("London HQ")
                  .address("10 Downing Street")
                  .gridRegionCode("GB-LDN")
                  .floorAreaM2(2500.00)
                  .build());

      energyStatementRepository.save(
          EnergyStatement.builder()
              .office(office)
              .year(2025)
              .month(10)
              .electricityKwh(1234.0)
              .heatingFuelType(HeatingFuelType.NONE)
              .build());

      String uri =
          String.format("/api/offices/%s/energy-statements?page=0&size=10", office.getId());

      getJson(uri)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content[0].year").value(2025))
          .andExpect(jsonPath("$.content[0].month").value(10));
    }
  }

  @Nested
  class FindOne {
    @SneakyThrows
    @Test
    void should_find_one_energy_statement() {
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
              EnergyStatement.builder()
                  .office(office)
                  .year(2025)
                  .month(10)
                  .electricityKwh(1234.0)
                  .heatingFuelType(HeatingFuelType.NONE)
                  .build());

      String uri =
          String.format(
              "/api/offices/%s/energy-statements/%s", office.getId(), energyStatement.getId());

      getJson(uri)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.year").value(2025))
          .andExpect(jsonPath("$.month").value(10))
          .andExpect(jsonPath("$.electricityKwh").value(1234.0));
    }
  }

  @Nested
  class Delete {

    @SneakyThrows
    @Test
    void should_delete_energy_statement_and_return_no_content() {

      var office =
          officeRepository.save(
              Office.builder().code("ENG-01").name("Engineering").gridRegionCode("GB-LDN").build());

      var energyStatement =
          energyStatementRepository.save(
              EnergyStatement.builder()
                  .office(office)
                  .year(2025)
                  .month(10)
                  .electricityKwh(1000.0)
                  .heatingFuelType(HeatingFuelType.NONE)
                  .build());

      String uri =
          String.format(
              "/api/offices/%s/energy-statements/%d/%d",
              office.getId(), energyStatement.getYear(), energyStatement.getMonth());
      deleteJson(uri).andExpect(status().isNoContent());

      assertTrue(
          energyStatementRepository
              .findByOfficeIdAndYearAndMonth(
                  office.getId(), energyStatement.getYear(), energyStatement.getMonth())
              .isEmpty());
    }
  }
}
