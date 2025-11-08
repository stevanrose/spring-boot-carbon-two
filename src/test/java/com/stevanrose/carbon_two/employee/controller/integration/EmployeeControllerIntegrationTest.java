package com.stevanrose.carbon_two.employee.controller.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stevanrose.carbon_two.common.controller.BaseWebIntegrationTest;
import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
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
}
