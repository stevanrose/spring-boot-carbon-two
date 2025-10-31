package com.stevanrose.carbon_two.office.controller.slice;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.errors.GlobalExceptionHandler;
import com.stevanrose.carbon_two.office.controller.OfficeController;
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
class OfficeControllerDeleteWebTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeService officeService;
  @Autowired OfficeMapper officeMapper;

  @SneakyThrows
  @Test
  void should_delete_office_and_return_deleted() {

    UUID id = UUID.randomUUID();
    mvc.perform(delete("/api/offices/{id}", id)).andExpect(status().isNoContent());
    verify(officeService).delete(id);
  }

  @SneakyThrows
  @Test
  void should_return_not_found_when_id_does_not_exist() {

    UUID id = UUID.randomUUID();
    doThrow(new EntityNotFoundException("Office not found with id: " + id))
        .when(officeService)
        .delete(id);
    mvc.perform(delete("/api/offices/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"));
  }

  @SneakyThrows
  @Test
  void should_return_conflict_when_references_exist() {

    UUID id = UUID.randomUUID();
    doThrow(new IllegalStateException("Cannot delete office with existing references"))
        .when(officeService)
        .delete(id);

    mvc.perform(delete("/api/offices/{id}", id))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("CONFLICT"));
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

    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
      return new GlobalExceptionHandler();
    }
  }
}
