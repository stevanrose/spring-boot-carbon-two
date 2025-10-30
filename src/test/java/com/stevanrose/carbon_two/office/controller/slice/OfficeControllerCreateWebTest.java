package com.stevanrose.carbon_two.office.controller.slice;

import com.stevanrose.carbon_two.office.controller.OfficeController;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeResponse;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OfficeController.class)
class OfficeControllerCreateWebTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    OfficeService officeService;
    @Autowired
    OfficeMapper officeMapper;

    @SneakyThrows
    @Test
    void should_create_office_and_return_201_with_location_header() {

        var reqJson = """
                  {
                    "code":"LON-01",
                    "name":"London HQ",
                    "address":"10 Downing St",
                    "gridRegionCode":"GB-LDN",
                    "floorAreaM2":300.0
                  }
                """;

        var entity = Office.builder().id(UUID.randomUUID()).code("LON-01").name("London HQ").gridRegionCode("GB-LDN").build();

        var resp = new OfficeResponse();
        resp.setId(entity.getId());

        when(officeMapper.toEntity(any(OfficeRequest.class))).thenReturn(entity);
        when(officeService.create(entity)).thenReturn(entity);
        when(officeMapper.toResponse(entity)).thenReturn(resp);

        mvc.perform(post("/api/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.matchesRegex(".*/api/offices/.+")))
                .andExpect(jsonPath("$.id").value(entity.getId().toString()));

    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        OfficeService officeService() {
            return mock(OfficeService.class);
        }

        @Bean
        OfficeMapper officeMapper() {
            return mock(OfficeMapper.class);
        }
    }

}