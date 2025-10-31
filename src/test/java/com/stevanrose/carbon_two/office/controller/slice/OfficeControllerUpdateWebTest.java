package com.stevanrose.carbon_two.office.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.office.controller.OfficeController;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeUpdateRequest;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
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

@WebMvcTest(controllers = OfficeController.class)
class OfficeControllerUpdateWebTest {

  @Autowired MockMvc mvc;
  @Autowired OfficeService officeService;
  @Autowired OfficeMapper officeMapper;
  @Autowired ObjectMapper objectMapper;

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

    when(officeService.update(any(UUID.class), any(OfficeUpdateRequest.class))).thenReturn(updated);

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
