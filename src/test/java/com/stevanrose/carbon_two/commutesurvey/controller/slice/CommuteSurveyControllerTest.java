package com.stevanrose.carbon_two.commutesurvey.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.controller.slice.BaseControllerTest;
import com.stevanrose.carbon_two.commutesurvey.controller.CommuteSurveyController;
import com.stevanrose.carbon_two.commutesurvey.domain.CommuteMode;
import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.commutesurvey.service.CommuteSurveyService;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.mapper.CommuteSurveyMapper;
import com.stevanrose.carbon_two.employee.domain.Employee;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

  @Nested
  class FindAndList {

    @SneakyThrows
    @Test
    void should_list_with_pagination() {

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

      PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("surveyDate").descending());
      Page<CommuteSurvey> page = new PageImpl<>(java.util.List.of(entity), pageRequest, 2);

      when(service.listByEmployeeId(eq(employeeId), any())).thenReturn(page);

      String uri = String.format("/api/employees/%s/commute-surveys", employeeId);

      getJson(uri)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content[0].employeeId").value(employeeId.toString()))
          .andExpect(jsonPath("$.content[0].primaryMode").value(CommuteMode.WALK.toString()))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(1))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(2));
    }

    @SneakyThrows
    @Test
    void should_find_one_by_id() {

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

      when(service.findByIdAndEmployeeId(eq(id), eq(employeeId))).thenReturn(entity);
      String uri = String.format("/api/employees/%s/commute-surveys/%s", employeeId, id);

      getJson(uri)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.employeeId").value(employeeId.toString()))
          .andExpect(jsonPath("$.primaryMode").value(CommuteMode.WALK.toString()));
    }

    @SneakyThrows
    @Test
    void should_not_find_one() {

      UUID employeeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      when(service.findByIdAndEmployeeId(eq(id), eq(employeeId)))
          .thenThrow(
              new EntityNotFoundException(
                  "CommuteSurvey not found with id: " + id + " for employee id: " + employeeId));

      String uri = String.format("/api/employees/%s/commute-surveys/%s", employeeId, id);

      getJson(uri).andExpect(status().isNotFound());
    }
  }

  @Nested
  class Delete {

    @SneakyThrows
    @Test
    void should_delete_and_return_no_content() {

      UUID employeeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      String uri = String.format("/api/employees/%s/commute-surveys/%s", employeeId, id);

      deleteJson(uri).andExpect(status().isNoContent());
    }

    @SneakyThrows
    @Test
    void should_not_find_for_delete() {

      UUID employeeId = UUID.randomUUID();
      UUID id = UUID.randomUUID();

      doThrow(
              new EntityNotFoundException(
                  "CommuteSurvey not found with id: " + id + " for employee id: " + employeeId))
          .when(service)
          .deleteByEmployeeIdAndId(eq(employeeId), eq(id));

      String uri = String.format("/api/employees/%s/commute-surveys/%s", employeeId, id);

      deleteJson(uri).andExpect(status().isNotFound());
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
