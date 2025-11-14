package com.stevanrose.carbon_two.office.controller.slice;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.office.controller.OfficeController;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeResponse;
import com.stevanrose.carbon_two.office.web.dto.OfficeUpdateRequest;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OfficeController.class)
@Import(OfficeControllerTest.MockConfig.class)
class OfficeControllerTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeService officeService;
  @Autowired OfficeMapper officeMapper;
  @Autowired private ObjectMapper objectMapper;

  @Nested
  class Create {

    @SneakyThrows
    @Test
    void should_create_office_and_return_201_with_location_header() {

      UUID id = UUID.randomUUID();

      var entity =
          Office.builder().id(id).code("LON-01").name("London HQ").gridRegionCode("GB-LDN").build();

      var resp = new OfficeResponse();
      resp.setId(entity.getId());

      when(officeService.create(any(Office.class))).thenReturn(entity);

      OfficeRequest request = new OfficeRequest();
      request.setCode(entity.getCode());
      request.setName(entity.getName());
      request.setGridRegionCode(entity.getGridRegionCode());

      var json = objectMapper.writeValueAsString(request);

      mvc.perform(post("/api/offices").contentType(MediaType.APPLICATION_JSON).content(json))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", Matchers.matchesRegex(".*/api/offices/" + id)));
    }
  }

  @Nested
  class List {

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
      Page<Office> page = new PageImpl<>(java.util.List.of(office1), req, 2L);

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
  }

  @Nested
  class FindOne {

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
  }

  @Nested
  class Update {

    @SneakyThrows
    @Test
    void should_update_office_using_put_and_return_ok() {

      UUID id = UUID.randomUUID();

      OfficeRequest request = new OfficeRequest();
      request.setCode("LON-01");
      request.setName("London HQ");
      request.setAddress("New Address");
      request.setGridRegionCode("GB-LDN");
      request.setFloorAreaM2(2500.00);

      Office updated =
          Office.builder()
              .id(id)
              .code(request.getCode())
              .name(request.getName())
              .address(request.getAddress())
              .gridRegionCode(request.getGridRegionCode())
              .floorAreaM2(request.getFloorAreaM2())
              .build();

      when(officeService.update(any(UUID.class), any(OfficeUpdateRequest.class)))
          .thenReturn(updated);

      mvc.perform(
              put("/api/offices/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.code").value(request.getCode()))
          .andExpect(jsonPath("$.name").value(request.getName()))
          .andExpect(jsonPath("$.address").value(request.getAddress()))
          .andExpect(jsonPath("$.gridRegionCode").value(request.getGridRegionCode()))
          .andExpect(jsonPath("$.floorAreaM2").value(request.getFloorAreaM2()));
    }
  }

  @Nested
  class Delete {

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
