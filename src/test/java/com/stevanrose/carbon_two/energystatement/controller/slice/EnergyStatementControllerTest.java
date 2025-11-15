package com.stevanrose.carbon_two.energystatement.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.energystatement.controller.EnergyStatementController;
import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import com.stevanrose.carbon_two.energystatement.service.EnergyStatementService;
import com.stevanrose.carbon_two.energystatement.web.dto.mapper.EnergyStatementMapper;
import com.stevanrose.carbon_two.office.domain.Office;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EnergyStatementController.class)
@Import(EnergyStatementControllerTest.MockConfig.class)
class EnergyStatementControllerTest {

  @Autowired MockMvc mvc;
  @Autowired EnergyStatementService service;

  @Nested
  class Upsert {

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
  }

  @Nested
  class List {

    @SneakyThrows
    @Test
    void should_list_office_energy_statement_and_return_ok() {

      UUID officeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      var entity =
          EnergyStatement.builder()
              .id(id)
              .office(Office.builder().id(officeId).build())
              .year(2025)
              .month(10)
              .electricityKwh(1234.0)
              .build();

      PageRequest req =
          PageRequest.of(0, 1, Sort.by("year").descending().and(Sort.by("month").descending()));
      Page<EnergyStatement> page = new PageImpl<>(java.util.List.of(entity), req, 2);

      when(service.listByOfficeId(any(UUID.class), any(PageRequest.class))).thenReturn(page);

      mvc.perform(
              get("/api/offices/{officeId}/energy-statements", officeId)
                  .param("page", "0")
                  .param("size", "1")
                  .param("sort", "year,desc")
                  .param("sort", "month,desc")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].id").value(id.toString()))
          .andExpect(jsonPath("$.content[0].officeId").value(officeId.toString()))
          .andExpect(jsonPath("$.content[0].year").value(2025))
          .andExpect(jsonPath("$.content[0].month").value(10))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(1))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(2));
    }
  }

  @Nested
  class FindOne {

    @SneakyThrows
    @Test
    void should_find_one_office_energy_statement_and_return_ok() {

      UUID officeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      var entity =
          EnergyStatement.builder()
              .id(id)
              .office(Office.builder().id(officeId).build())
              .year(2025)
              .month(10)
              .electricityKwh(1234.0)
              .build();

      when(service.findByIdAndOfficeId(any(UUID.class), any(UUID.class))).thenReturn(entity);

      mvc.perform(
              get("/api/offices/{officeId}/energy-statements/{id}", officeId, id)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.officeId").value(officeId.toString()))
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

      UUID officeId = UUID.randomUUID();

      mvc.perform(
              delete(
                  "/api/offices/{officeId}/energy-statements/{year}/{month}", officeId, 2025, 10))
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
              delete(
                  "/api/offices/{officeId}/energy-statements/{year}/{month}", officeId, 2025, 10))
          .andExpect(status().isNotFound());
    }
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
