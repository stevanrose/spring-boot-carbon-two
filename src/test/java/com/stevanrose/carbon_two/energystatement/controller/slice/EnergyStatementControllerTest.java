package com.stevanrose.carbon_two.energystatement.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.controller.slice.BaseControllerTest;
import com.stevanrose.carbon_two.energystatement.controller.EnergyStatementController;
import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import com.stevanrose.carbon_two.energystatement.service.EnergyStatementService;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
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

@WebMvcTest(controllers = EnergyStatementController.class)
@Import(EnergyStatementControllerTest.MockConfig.class)
class EnergyStatementControllerTest extends BaseControllerTest {

  @Autowired EnergyStatementService service;

  @Nested
  class Upsert {

    @SneakyThrows
    @Test
    void should_create() {

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

      when(service.upsert(eq(officeId), any()))
          .thenReturn(new EnergyStatementService.UpsertResult(entity, true));

      EnergyStatementRequest request =
          new EnergyStatementRequest(2025, 10, 1000.0, HeatingFuelType.NONE, null, null, null);

      var url = "/api/offices/" + officeId + "/energy-statements";

      putJson(url, request)
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
    void should_update() {

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

      when(service.upsert(eq(officeId), any()))
          .thenReturn(new EnergyStatementService.UpsertResult(entity, false));

      EnergyStatementRequest request =
          new EnergyStatementRequest(2025, 10, 1500.0, HeatingFuelType.GAS, null, null, null);

      var url = "/api/offices/" + officeId + "/energy-statements";
      putJson(url, request)
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
    void should_list() {

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

      var url =
          "/api/offices/"
              + officeId
              + " /energy-statements?page=0&size=1&sort=year,desc&sort=month,desc";

      getJson(url)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(1)))
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
    void should_find_one() {

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
      var url = "/api/offices/" + officeId + "/energy-statements/" + id;

      getJson(url)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.officeId").value(officeId.toString()))
          .andExpect(jsonPath("$.year").value(2025))
          .andExpect(jsonPath("$.month").value(10))
          .andExpect(jsonPath("$.electricityKwh").value(1234.0));
    }

    @SneakyThrows
    @Test
    void should_not_find_one() {

      UUID officeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      doThrow(new EntityNotFoundException("Employee not found with id: " + id))
          .when(service)
          .findByIdAndOfficeId(any(UUID.class), any(UUID.class));

      var url = "/api/offices/" + officeId + "/energy-statements/" + id;

      getJson(url).andExpect(status().isNotFound());
    }
  }

  @Nested
  class Delete {

    @SneakyThrows
    @Test
    void should_delete() {

      UUID officeId = UUID.randomUUID();

      var url = "/api/offices/" + officeId + "/energy-statements/2025/10";

      deleteJson(url).andExpect(status().isNoContent());
    }

    @SneakyThrows
    @Test
    void should_not_find_for_delete() {
      UUID officeId = UUID.randomUUID();

      doThrow(new EntityNotFoundException("Energy statement not found"))
          .when(service)
          .deleteByOfficeIdAndYearAndMonth(officeId, 2025, 10);

      var url = "/api/offices/" + officeId + "/energy-statements/2025/10";

      deleteJson(url).andExpect(status().isNotFound());
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
