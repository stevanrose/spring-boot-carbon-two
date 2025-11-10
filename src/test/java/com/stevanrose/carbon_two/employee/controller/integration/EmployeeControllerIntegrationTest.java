package com.stevanrose.carbon_two.employee.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stevanrose.carbon_two.common.controller.BaseWebIntegrationTest;
import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeUpdateRequest;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class EmployeeControllerIntegrationTest extends BaseWebIntegrationTest {

  @Autowired private EmployeeRepository employeeRepository;
  @Autowired private OfficeRepository officeRepository;

  @SneakyThrows
  @Test
  void should_list_employees_with_pagination() {

    Office office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    Employee employee =
        Employee.builder()
            .email("john.doe@mail.com")
            .department("Engineering")
            .employmentType(EmploymentType.FULL_TIME)
            .workPattern(WorkPattern.HYBRID)
            .officeId(office.getId())
            .build();

    employeeRepository.save(employee);

    getJson("/api/employees?page=0&size=10")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].email").value("john.doe@mail.com"))
        .andExpect(jsonPath("$.content[0].department").value("Engineering"))
        .andExpect(jsonPath("$.content[0].employmentType").value(EmploymentType.FULL_TIME.name()))
        .andExpect(jsonPath("$.content[0].workPattern").value(WorkPattern.HYBRID.name()));
  }

  @SneakyThrows
  @Test
  void should_find_one_employee_entity_and_return_ok() {

    Office office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    Employee employee =
        Employee.builder()
            .email("john.doe@mail.com")
            .department("Engineering")
            .employmentType(EmploymentType.FULL_TIME)
            .workPattern(WorkPattern.HYBRID)
            .officeId(office.getId())
            .build();

    employeeRepository.save(employee);

    mvc.perform(get("/api/employees/{id}", employee.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.id").value(employee.getId().toString()))
        .andExpect(jsonPath("$.email").value("john.doe@mail.com"))
        .andExpect(jsonPath("$.department").value("Engineering"))
        .andExpect(jsonPath("$.employmentType").value(EmploymentType.FULL_TIME.name()))
        .andExpect(jsonPath("$.workPattern").value(WorkPattern.HYBRID.name()));
  }

  @SneakyThrows
  @Test
  void should_create_employee() {

    var office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    EmployeeRequest employeeRequest = new EmployeeRequest();
    employeeRequest.setEmail("john.doe@test.com");
    employeeRequest.setDepartment("Engineering");
    employeeRequest.setEmploymentType(EmploymentType.FULL_TIME);
    employeeRequest.setWorkPattern(WorkPattern.HYBRID);
    employeeRequest.setOfficeId(office.getId());

    postJson("/api/employees", employeeRequest).andExpect(status().isCreated());
  }

  @SneakyThrows
  @Test
  void should_update_employee() {
    Office office =
        officeRepository.save(
            Office.builder()
                .code("LON-01")
                .name("London HQ")
                .address("10 Downing Street")
                .gridRegionCode("GB-LDN")
                .floorAreaM2(2500.00)
                .build());

    Employee employee =
        employeeRepository.save(
            Employee.builder()
                .email("john.doe@mail.com")
                .department("Engineering")
                .employmentType(EmploymentType.FULL_TIME)
                .workPattern(WorkPattern.HYBRID)
                .officeId(office.getId())
                .build());

    EmployeeUpdateRequest request = new EmployeeUpdateRequest();
    request.setOfficeId(office.getId());
    request.setDepartment("Design");
    request.setEmail("john.doe@mail.com");
    request.setEmploymentType(EmploymentType.CONTRACT);
    request.setWorkPattern(WorkPattern.REMOTE);

    putJson(String.format("/api/employees/" + employee.getId()), request)
        .andExpect(status().isOk());
  }
}
