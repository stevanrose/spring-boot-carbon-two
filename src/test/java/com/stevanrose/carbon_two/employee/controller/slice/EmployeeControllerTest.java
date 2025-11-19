package com.stevanrose.carbon_two.employee.controller.slice;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.stevanrose.carbon_two.common.controller.slice.BaseControllerTest;
import com.stevanrose.carbon_two.employee.controller.EmployeeController;
import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import com.stevanrose.carbon_two.employee.service.EmployeeService;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeUpdateRequest;
import com.stevanrose.carbon_two.employee.web.dto.mapper.EmployeeMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
import org.springframework.http.MediaType;

@WebMvcTest(controllers = EmployeeController.class)
@Import(EmployeeControllerTest.MockConfig.class)
class EmployeeControllerTest extends BaseControllerTest {

  @Autowired EmployeeService employeeService;

  @SneakyThrows
  @Test
  void should_create() {

    UUID officeId = UUID.randomUUID();
    UUID id = UUID.randomUUID();

    var entity =
        Employee.builder()
            .id(id)
            .email("john.doe@mail.com")
            .department("Engineering")
            .employmentType(EmploymentType.FULL_TIME)
            .workPattern(WorkPattern.HYBRID)
            .officeId(officeId)
            .build();

    when(employeeService.create(any(Employee.class))).thenReturn(entity);

    EmployeeRequest employeeRequest =
        new EmployeeRequest(
            "john.doe@test.com",
            "Engineering",
            EmploymentType.FULL_TIME,
            WorkPattern.HYBRID,
            officeId);

    var url = "/api/employees";

    postJson(url, employeeRequest)
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/employees/" + id));
  }

  @SneakyThrows
  @Test
  void should_update() {

    UUID id = UUID.randomUUID();
    UUID officeId = UUID.randomUUID();

    EmployeeUpdateRequest request =
        new EmployeeUpdateRequest(
            "John.doe@mail.com",
            "Engineering",
            EmploymentType.FULL_TIME,
            WorkPattern.HYBRID,
            officeId);

    Employee updated =
        Employee.builder()
            .id(id)
            .email(request.email())
            .department(request.department())
            .employmentType(request.employmentType())
            .workPattern(request.workPattern())
            .officeId(request.officeId())
            .build();

    when(employeeService.update(any(UUID.class), any(EmployeeUpdateRequest.class)))
        .thenReturn(updated);

    var url = "/api/employees/" + id;

    putJson(url, request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.email").value(request.email()))
        .andExpect(jsonPath("$.department").value(request.department()))
        .andExpect(jsonPath("$.employmentType").value(request.employmentType().name()))
        .andExpect(jsonPath("$.workPattern").value(request.workPattern().name()));
  }

  @SneakyThrows
  @Test
  void should_list() {

    UUID officeId = UUID.randomUUID();
    UUID id = UUID.randomUUID();

    var employee1 =
        Employee.builder()
            .id(id)
            .email("john.doe@mail.com")
            .department("Engineering")
            .employmentType(EmploymentType.FULL_TIME)
            .workPattern(WorkPattern.HYBRID)
            .officeId(officeId)
            .build();

    PageRequest req = PageRequest.of(0, 1);
    Page<Employee> page = new PageImpl<>(List.of(employee1), req, 2L);

    when(employeeService.list(any())).thenReturn(page);

    getJson("/api/employees?page=0&size=10")
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id", notNullValue()))
        .andExpect(jsonPath("$.content[0].email").value("john.doe@mail.com"))
        .andExpect(jsonPath("$.content[0].department").value("Engineering"))
        .andExpect(jsonPath("$.content[0].employmentType").value(EmploymentType.FULL_TIME.name()))
        .andExpect(jsonPath("$.content[0].workPattern").value(WorkPattern.HYBRID.name()))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(2));
  }

  @Nested
  class Find {

    @SneakyThrows
    @Test
    void should_find_one() {

      var id = UUID.randomUUID();
      var officeId = UUID.randomUUID();

      var entity =
          Employee.builder()
              .id(id)
              .email("john.doe@mail.com")
              .department("Engineering")
              .employmentType(EmploymentType.FULL_TIME)
              .workPattern(WorkPattern.HYBRID)
              .officeId(officeId)
              .build();

      when(employeeService.findById(any(UUID.class))).thenReturn(entity);

      var url = "/api/employees/" + id;

      getJson(url)
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith("application/json"))
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.email").value("john.doe@mail.com"))
          .andExpect(jsonPath("$.department").value("Engineering"))
          .andExpect(jsonPath("$.employmentType").value(EmploymentType.FULL_TIME.name()))
          .andExpect(jsonPath("$.workPattern").value(WorkPattern.HYBRID.name()));
    }

    @SneakyThrows
    @Test
    void should_not_find_one() {
      var id = UUID.randomUUID();

      when(employeeService.findById(any(UUID.class)))
          .thenThrow(new EntityNotFoundException("Employee not found with id: " + id));

      var url = "/api/employees/" + id;

      getJson(url).andExpect(status().isNotFound());
    }
  }

  @Nested
  class Delete {
    @SneakyThrows
    @Test
    void should_delete() {

      UUID id = UUID.randomUUID();
      var url = "/api/employees/" + id;

      deleteJson(url).andExpect(status().isNoContent());
    }

    @SneakyThrows
    @Test
    void should_not_find_for_delete() {

      UUID id = UUID.randomUUID();

      doThrow(
              new IllegalStateException(
                  "Cannot delete Employee with id: " + id + " due to existing references."))
          .when(employeeService)
          .delete(id);

      var url = "/api/employees/" + id;
      deleteJson(url).andExpect(status().isConflict());
    }
  }

  @TestConfiguration
  static class MockConfig {
    @Bean
    EmployeeService employeeService() {
      return mock(EmployeeService.class);
    }

    @Bean
    EmployeeMapper employeeMapper() {
      return Mappers.getMapper(EmployeeMapper.class);
    }
  }
}
