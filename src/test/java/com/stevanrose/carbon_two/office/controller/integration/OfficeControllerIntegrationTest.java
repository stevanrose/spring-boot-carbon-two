package com.stevanrose.carbon_two.office.controller.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stevanrose.carbon_two.common.controller.BaseWebIntegrationTest;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OfficeControllerIntegrationTest extends BaseWebIntegrationTest {

  @Autowired private OfficeRepository officeRepository;

  @BeforeEach
  void setUp() {
    officeRepository.deleteAll();
  }

  @SneakyThrows
  @Test
  void should_create_office() {

    Office office =
        Office.builder()
            .code("LON-01")
            .name("London HQ")
            .address("10 Downing Street")
            .gridRegionCode("GB-LDN")
            .floorAreaM2(300.0)
            .build();

    postJson("/api/offices", office).andExpect(status().isCreated());
  }

  @SneakyThrows
  @Test
  void should_find_one_office_entity_and_return_ok() {
    Office office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(300.0)
                .build());

    getJson("/api/offices/" + office.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(office.getId().toString()))
        .andExpect(jsonPath("$.code").value("LON-01"))
        .andExpect(jsonPath("$.name").value("London HQ"))
        .andExpect(jsonPath("$.address").value("10 Downing Street"))
        .andExpect(jsonPath("$.gridRegionCode").value("GB-LDN"))
        .andExpect(jsonPath("$.floorAreaM2").value(300.0));
  }

  @SneakyThrows
  @Test
  void should_list_offices_with_pagination() {
    Office office1 =
        Office.builder()
            .code("LON-01")
            .name("London HQ")
            .address("10 Downing Street")
            .gridRegionCode("GB-LDN")
            .floorAreaM2(300.0)
            .build();

    officeRepository.save(office1);

    getJson("/api/offices?page=0&size=10&sort=name,asc")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].code").value("LON-01"))
        .andExpect(jsonPath("$.content[0].name").value("London HQ"));
  }

  @SneakyThrows
  @Test
  void should_update_office() {

    var office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    OfficeRequest request = new OfficeRequest();
    request.setCode("LON-02");
    request.setName("London Office");
    request.setAddress("Updated Address");
    request.setGridRegionCode("GB-LDN-1");
    request.setFloorAreaM2(1500.00);

    putJson("/api/offices/" + office.getId(), request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(office.getId().toString()))
        .andExpect(jsonPath("$.code").value(request.getCode()))
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.address").value(request.getAddress()))
        .andExpect(jsonPath("$.gridRegionCode").value(request.getGridRegionCode()))
        .andExpect(jsonPath("$.floorAreaM2").value(request.getFloorAreaM2()));
    ;
  }

  @SneakyThrows
  @Test
  void should_delete_office() {

    var office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    mvc.perform(delete("/api/offices/{id}", office.getId())).andExpect(status().isNoContent());

    assertTrue(officeRepository.findById(office.getId()).isEmpty());
  }
}
