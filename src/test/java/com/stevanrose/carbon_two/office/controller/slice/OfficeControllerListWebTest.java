package com.stevanrose.carbon_two.office.controller.slice;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.office.controller.OfficeController;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OfficeController.class)
class OfficeControllerListWebTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeService officeService;
  @Autowired OfficeMapper officeMapper;

  @SneakyThrows
  @Test
  void should_list_offices_with_pagination() {

    var office1 =
        Office.builder()
            .id(UUID.randomUUID())
            .code("LON-01")
            .name("London HQ")
            .gridRegionCode("GB-LDN")
            .build();

    PageRequest req = PageRequest.of(0, 1);
    Page<Office> page = new PageImpl<>(List.of(office1), req, 2L);

    when(officeService.list(any())).thenReturn(page);

    mvc.perform(
            get("/api/offices")
                .param("page", "0")
                .param("size", "1")
                .param("sort", "name,asc")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id", notNullValue()))
        .andExpect(jsonPath("$.content[0].code").value("LON-01"))
        .andExpect(jsonPath("$.content[0].name").value("London HQ"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(2));
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
