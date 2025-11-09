package com.stevanrose.carbon_two.employee.controller.slice;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.employee.controller.EmployeeController;
import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import com.stevanrose.carbon_two.employee.service.EmployeeService;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeResponse;
import com.stevanrose.carbon_two.employee.web.dto.mapper.EmployeeMapper;
import jakarta.persistence.EntityNotFoundException;
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

@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerWebTest {

  @Autowired MockMvc mvc;

  @Autowired EmployeeService employeeService;

  @Autowired EmployeeMapper employeeMapper;

  @Autowired private ObjectMapper objectMapper;

  @SneakyThrows
  @Test
  void should_create_employee_and_return_201_with_location_header() {

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

    var response = new EmployeeResponse();
    response.setId(id);

    when(employeeService.create(any(Employee.class))).thenReturn(entity);

    EmployeeRequest employeeRequest = new EmployeeRequest();
    employeeRequest.setEmail("john.doe@test.com");
    employeeRequest.setDepartment("Engineering");
    employeeRequest.setEmploymentType(EmploymentType.FULL_TIME);
    employeeRequest.setWorkPattern(WorkPattern.HYBRID);
    employeeRequest.setOfficeId(officeId);

    var json = objectMapper.writeValueAsString(employeeRequest);

    mvc.perform(post("/api/employees").contentType("application/json").content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/employees/" + id));
  }

  @SneakyThrows
  @Test
  void should_list_employees_with_pagination() {

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

    mvc.perform(
            get("/api/employees")
                .param("page", "0")
                .param("size", "1")
                .accept(MediaType.APPLICATION_JSON))
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

  @SneakyThrows
  @Test
  void should_find_one_employee_by_id_and_return_ok() {

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

    mvc.perform(get("/api/employees/{id}", id))
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
  void should_not_find_employee_by_id_and_return_not_not_found() {
    var id = UUID.randomUUID();

    when(employeeService.findById(any(UUID.class)))
        .thenThrow(new EntityNotFoundException("Employee not found with id: " + id));

    mvc.perform(get("/api/employees/{id}", id)).andExpect(status().isNotFound());
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
