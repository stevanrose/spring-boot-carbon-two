package com.stevanrose.carbon_two.employee.web.dto;

import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record EmployeeRequest(
    @NotBlank @Email String email,
    String department,
    EmploymentType employmentType,
    WorkPattern workPattern,
    UUID officeId) {
  public EmployeeRequest {
    if (employmentType == null) {
      employmentType = EmploymentType.FULL_TIME;
    }
    if (workPattern == null) {
      workPattern = WorkPattern.HYBRID;
    }
  }
}
