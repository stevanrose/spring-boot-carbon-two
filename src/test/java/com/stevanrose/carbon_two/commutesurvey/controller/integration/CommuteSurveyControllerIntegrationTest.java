package com.stevanrose.carbon_two.commutesurvey.controller.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stevanrose.carbon_two.common.controller.integration.BaseControllerIntegrationTest;
import com.stevanrose.carbon_two.commutesurvey.domain.CommuteMode;
import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.commutesurvey.repository.CommuteSurveyRepository;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import java.time.OffsetDateTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CommuteSurveyControllerIntegrationTest extends BaseControllerIntegrationTest {

  @Autowired private OfficeRepository officeRepository;
  @Autowired private EmployeeRepository employeeRepository;
  @Autowired private CommuteSurveyRepository commuteSurveyRepository;

  @Nested
  class Upsert {
    @SneakyThrows
    @Test
    void should_create() {

      var office =
          officeRepository.save(
              Office.builder()
                  .code("LON-01")
                  .name("London HQ")
                  .address("10 Downing Street")
                  .gridRegionCode("GB-LDN")
                  .floorAreaM2(2500.00)
                  .build());

      var employee =
          employeeRepository.save(
              Employee.builder()
                  .email("john.doe@mail.com")
                  .department("Engineering")
                  .employmentType(EmploymentType.FULL_TIME)
                  .workPattern(WorkPattern.HYBRID)
                  .officeId(office.getId())
                  .build());

      CommuteSurveyRequest request =
          new CommuteSurveyRequest(
              null, OffsetDateTime.now(), CommuteMode.BIKE, 10.0, 5, 1, "No notes");

      String uri = String.format("/api/employees/%s/commute-surveys", employee.getId());
      putJson(uri, request).andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    void should_update() {

      var office =
          officeRepository.save(
              Office.builder()
                  .code("LON-01")
                  .name("London HQ")
                  .address("10 Downing Street")
                  .gridRegionCode("GB-LDN")
                  .floorAreaM2(2500.00)
                  .build());

      var employee =
          employeeRepository.save(
              Employee.builder()
                  .email("john.doe@mail.com")
                  .department("Engineering")
                  .employmentType(EmploymentType.FULL_TIME)
                  .workPattern(WorkPattern.HYBRID)
                  .officeId(office.getId())
                  .build());

      var commuteSurvey =
          commuteSurveyRepository.save(
              CommuteSurvey.builder()
                  .employee(employee)
                  .surveyDate(OffsetDateTime.now().minusDays(10))
                  .primaryMode(CommuteMode.CAR)
                  .oneWayDistanceKm(15.0)
                  .daysPerWeekCommuting(5)
                  .carOccupancy(1)
                  .notes("Initial survey")
                  .build());

      CommuteSurveyRequest request =
          new CommuteSurveyRequest(
              commuteSurvey.getId(),
              OffsetDateTime.now(),
              CommuteMode.BIKE,
              12.0,
              4,
              1,
              "Updated survey notes");

      String uri = String.format("/api/employees/%s/commute-surveys", employee.getId());
      putJson(uri, request).andExpect(status().isOk());
    }
  }
}
