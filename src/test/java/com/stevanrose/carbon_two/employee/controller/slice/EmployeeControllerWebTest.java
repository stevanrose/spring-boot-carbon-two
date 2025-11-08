package com.stevanrose.carbon_two.employee.controller.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevanrose.carbon_two.employee.controller.EmployeeController;
import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import com.stevanrose.carbon_two.employee.service.EmployeeService;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeResponse;
import com.stevanrose.carbon_two.employee.web.dto.mapper.EmployeeMapper;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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

    when(employeeMapper.toEntity(any(EmployeeRequest.class))).thenReturn(entity);
    when(employeeService.create(any(Employee.class))).thenReturn(entity);
    when(employeeMapper.toResponse(entity)).thenReturn(response);

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

  @TestConfiguration
  static class MockConfig {
    @Bean
    EmployeeService employeeService() {
      return mock(EmployeeService.class);
    }

    @Bean
    EmployeeMapper employeeMapper() {
      return mock(EmployeeMapper.class);
    }
  }
}
