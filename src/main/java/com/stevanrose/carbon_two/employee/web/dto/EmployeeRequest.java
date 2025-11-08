package com.stevanrose.carbon_two.employee.web.dto;

import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class EmployeeRequest {

  @NotBlank @Email private String email;
  private String department;
  private EmploymentType employmentType = EmploymentType.FULL_TIME;
  private WorkPattern workPattern = WorkPattern.HYBRID;
  private UUID officeId;
}
