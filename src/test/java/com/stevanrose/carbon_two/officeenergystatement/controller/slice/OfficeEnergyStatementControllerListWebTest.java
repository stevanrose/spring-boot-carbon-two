package com.stevanrose.carbon_two.officeenergystatement.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.officeenergystatement.controller.OfficeEnergyStatementController;
import com.stevanrose.carbon_two.officeenergystatement.domain.OfficeEnergyStatement;
import com.stevanrose.carbon_two.officeenergystatement.service.OfficeEnergyStatementService;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.mapper.OfficeEnergyStatementMapper;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OfficeEnergyStatementController.class)
class OfficeEnergyStatementControllerListWebTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeEnergyStatementService service;

  @SneakyThrows
  @Test
  void should_list_office_energy_statement_and_return_ok() {

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

    PageRequest req =
        PageRequest.of(0, 1, Sort.by("year").descending().and(Sort.by("month").descending()));
    Page<OfficeEnergyStatement> page = new PageImpl<>(List.of(entity), req, 2);

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

  @TestConfiguration
  static class MockConfig {

    @Bean
    OfficeEnergyStatementService service() {
      return mock(OfficeEnergyStatementService.class);
    }

    @Bean
    OfficeEnergyStatementMapper mapper() {
      return Mappers.getMapper(OfficeEnergyStatementMapper.class);
    }
  }
}
