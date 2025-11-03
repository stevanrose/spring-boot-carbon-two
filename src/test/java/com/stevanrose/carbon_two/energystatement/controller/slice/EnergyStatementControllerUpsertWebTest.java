package com.stevanrose.carbon_two.energystatement.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.energystatement.controller.EnergyStatementController;
import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import com.stevanrose.carbon_two.energystatement.service.EnergyStatementService;
import com.stevanrose.carbon_two.energystatement.web.dto.mapper.EnergyStatementMapper;
import com.stevanrose.carbon_two.office.domain.Office;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EnergyStatementController.class)
class EnergyStatementControllerUpsertWebTest {

  @Autowired MockMvc mvc;
  @Autowired EnergyStatementService service;

  @SneakyThrows
  @Test
  void should_create_energy_statement_and_return_created() {

    UUID officeId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    var entity =
        EnergyStatement.builder()
            .id(id)
            .office(Office.builder().id(officeId).build())
            .year(2025)
            .month(10)
            .electricityKwh(1234.0)
            .heatingFuelType(HeatingFuelType.NONE)
            .build();

    when(service.upsert(eq(officeId), eq(2025), eq(10), any()))
        .thenReturn(new EnergyStatementService.UpsertResult(entity, true));

    var json =
        """
          {"year":2025,"month":10,"electricityKwh":1000.0,"heatingFuelType":"NONE"}
        """;

    mvc.perform(
            put("/api/offices/{officeId}/energy-statements/{year}/{month}", officeId, 2025, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string(
                    "Location",
                    org.hamcrest.Matchers.containsString(
                        "/api/offices/" + officeId + "/energy-statements/" + id)))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.year").value(2025))
        .andExpect(jsonPath("$.month").value(10));
  }

  @SneakyThrows
  @Test
  void should_update_energy_statement_and_return_ok() {

    UUID officeId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    var entity =
        EnergyStatement.builder()
            .id(id)
            .office(Office.builder().id(officeId).build())
            .year(2025)
            .month(10)
            .electricityKwh(1500.0)
            .heatingFuelType(HeatingFuelType.GAS)
            .build();

    when(service.upsert(eq(officeId), eq(2025), eq(10), any()))
        .thenReturn(new EnergyStatementService.UpsertResult(entity, false));

    var json =
        """
          {"year":2025,"month":10,"electricityKwh":1500.0,"heatingFuelType":"GAS"}
        """;

    mvc.perform(
            put("/api/offices/{officeId}/energy-statements/{year}/{month}", officeId, 2025, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.electricityKwh").value(1500.0))
        .andExpect(jsonPath("$.heatingFuelType").value("GAS"));
  }

  @TestConfiguration
  static class MockConfig {

    @Bean
    EnergyStatementService service() {
      return mock(EnergyStatementService.class);
    }

    @Bean
    EnergyStatementMapper mapper() {
      return Mappers.getMapper(EnergyStatementMapper.class);
    }
  }
}
