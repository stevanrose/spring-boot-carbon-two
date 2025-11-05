package com.stevanrose.carbon_two.energystatement.controller.slice;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.energystatement.controller.EnergyStatementController;
import com.stevanrose.carbon_two.energystatement.service.EnergyStatementService;
import com.stevanrose.carbon_two.energystatement.web.dto.mapper.EnergyStatementMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EnergyStatementController.class)
class EnergyStatementControllerDeleteWebTest {

  @Autowired MockMvc mvc;
  @Autowired EnergyStatementService service;

  @SneakyThrows
  @Test
  void should_delete_energy_statement_and_return_no_content() {

    UUID officeId = UUID.randomUUID();

    mvc.perform(
            delete("/api/offices/{officeId}/energy-statements/{year}/{month}", officeId, 2025, 10))
        .andExpect(status().isNoContent());
  }

  @SneakyThrows
  @Test
  void should_not_find_energy_statement_and_return_not_found() {
    UUID officeId = UUID.randomUUID();

    doThrow(new EntityNotFoundException("Energy statement not found"))
        .when(service)
        .deleteByOfficeIdAndYearAndMonth(officeId, 2025, 10);

    mvc.perform(
            delete("/api/offices/{officeId}/energy-statements/{year}/{month}", officeId, 2025, 10))
        .andExpect(status().isNotFound());
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
