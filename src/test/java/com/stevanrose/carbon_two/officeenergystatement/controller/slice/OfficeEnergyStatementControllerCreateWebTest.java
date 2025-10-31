package com.stevanrose.carbon_two.officeenergystatement.controller.slice;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.errors.GlobalExceptionHandler;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
import com.stevanrose.carbon_two.officeenergystatement.controller.OfficeEnergyStatementController;
import com.stevanrose.carbon_two.officeenergystatement.domain.OfficeEnergyStatement;
import com.stevanrose.carbon_two.officeenergystatement.service.OfficeEnergyStatementService;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.mapper.OfficeEnergyStatementMapper;
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

@WebMvcTest(controllers = OfficeEnergyStatementController.class)
class OfficeEnergyStatementControllerCreateWebTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeEnergyStatementService service;

  @SneakyThrows
  @Test
  void should_create_office_energy_statement_and_return_created() {

    UUID officeId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    var entity =
        OfficeEnergyStatement.builder()
            .id(id)
            .office(Office.builder().id(officeId).build())
            .year(2025)
            .month(10)
            .electricityKwh(1234.0)
            .build();

    when(service.create(eq(officeId), any())).thenReturn(entity);

    var json =
        """
      {"year":2025,"month":10,"electricityKwh":1234.0,"heatingFuelType":"NONE"}
    """;

    mvc.perform(
            post("/api/offices/{officeId}/energy-statements", officeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string(
                    "Location",
                    containsString("/api/offices/" + officeId + "/energy-statements/" + id)))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.officeId").value(officeId.toString()))
        .andExpect(jsonPath("$.year").value(2025))
        .andExpect(jsonPath("$.month").value(10))
        .andExpect(jsonPath("$.electricityKwh").value(1234.0));
  }

  @TestConfiguration
  static class MockConfig {

    @Bean
    OfficeEnergyStatementService service() {
      return mock(OfficeEnergyStatementService.class);
    }

    @Bean
    OfficeService officeService() {
      return mock(OfficeService.class);
    }

    @Bean
    OfficeEnergyStatementMapper mapper() {
      return Mappers.getMapper(OfficeEnergyStatementMapper.class);
    }

    @Bean
    OfficeMapper officeMapper() {
      return Mappers.getMapper(OfficeMapper.class);
    }

    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
      return new GlobalExceptionHandler();
    }
  }
}
