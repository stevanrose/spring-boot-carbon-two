package com.stevanrose.carbon_two.commutesurvey.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.controller.slice.BaseControllerTest;
import com.stevanrose.carbon_two.commutesurvey.controller.CommuteSurveyController;
import com.stevanrose.carbon_two.commutesurvey.domain.CommuteMode;
import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.commutesurvey.service.CommuteSurveyService;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.mapper.CommuteSurveyMapper;
import com.stevanrose.carbon_two.employee.domain.Employee;
import java.time.OffsetDateTime;
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

@WebMvcTest(controllers = CommuteSurveyController.class)
@Import(CommuteSurveyControllerTest.MockConfig.class)
public class CommuteSurveyControllerTest extends BaseControllerTest {

  @Autowired CommuteSurveyService service;

  @Nested
  class Upsert {

    @SneakyThrows
    @Test
    void should_upsert_and_return_created() {

      UUID employeeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      var entity =
          CommuteSurvey.builder()
              .surveyDate(OffsetDateTime.now())
              .primaryMode(CommuteMode.WALK)
              .oneWayDistanceKm(5.0)
              .daysPerWeekCommuting(5)
              .employee(Employee.builder().id(employeeId).build())
              .id(id)
              .build();

      when(service.upsert(eq(employeeId), any()))
          .thenReturn(new CommuteSurveyService.UpsertResult(entity, true));

      CommuteSurveyRequest request =
          new CommuteSurveyRequest(
              null,
              entity.getSurveyDate(),
              entity.getPrimaryMode(),
              entity.getOneWayDistanceKm(),
              entity.getDaysPerWeekCommuting(),
              entity.getCarOccupancy(),
              entity.getNotes());

      String uri = String.format("/api/employees/%s/commute-surveys", employeeId);

      putJson(uri, request).andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    void should_upsert_and_return_ok() {

      UUID employeeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      var entity =
          CommuteSurvey.builder()
              .surveyDate(OffsetDateTime.now())
              .primaryMode(CommuteMode.WALK)
              .oneWayDistanceKm(5.0)
              .daysPerWeekCommuting(5)
              .carOccupancy(1)
              .employee(Employee.builder().id(employeeId).build())
              .id(id)
              .build();

      when(service.upsert(eq(employeeId), any()))
          .thenReturn(new CommuteSurveyService.UpsertResult(entity, false));

      CommuteSurveyRequest request =
          new CommuteSurveyRequest(
              id,
              entity.getSurveyDate(),
              CommuteMode.BIKE,
              entity.getOneWayDistanceKm(),
              entity.getDaysPerWeekCommuting(),
              entity.getCarOccupancy(),
              entity.getNotes());

      String uri = String.format("/api/employees/%s/commute-surveys", employeeId);

      putJson(uri, request).andExpect(status().isOk());
    }
  }

  @TestConfiguration
  static class MockConfig {

    @Bean
    CommuteSurveyService service() {
      return mock(CommuteSurveyService.class);
    }

    @Bean
    CommuteSurveyMapper mapper() {
      return Mappers.getMapper(CommuteSurveyMapper.class);
    }
  }
}
