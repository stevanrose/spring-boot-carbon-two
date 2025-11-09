package com.stevanrose.carbon_two.office.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.office.controller.OfficeController;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
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

@WebMvcTest(controllers = OfficeController.class)
public class OfficeControllerFindOneWebTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeService officeService;

  @SneakyThrows
  @Test
  void should_find_one_office_entity_and_return_ok() {

    var id = UUID.randomUUID();
    var entity =
        Office.builder().id(id).code("LON-01").name("London HQ").gridRegionCode("GB-LDN").build();

    when(officeService.findById(any(UUID.class))).thenReturn(entity);

    mvc.perform(get("/api/offices/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.code").value("LON-01"))
        .andExpect(jsonPath("$.name").value("London HQ"))
        .andExpect(jsonPath("$.gridRegionCode").value("GB-LDN"));
  }

  @SneakyThrows
  @Test
  void should_not_find_one_office_entity_and_return_not_found() {

    var id = UUID.randomUUID();
    when(officeService.findById(any(UUID.class)))
        .thenThrow(new EntityNotFoundException("Office not found with id: " + id));

    mvc.perform(get("/api/offices/{id}", id)).andExpect(status().isNotFound());
  }

  @TestConfiguration
  static class MockConfig {

    @Bean
    OfficeService officeService() {
      return mock(OfficeService.class);
    }

    @Bean
    OfficeMapper officeMapper() {
      return Mappers.getMapper(OfficeMapper.class);
    }
  }
}
