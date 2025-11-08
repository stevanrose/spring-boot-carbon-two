package com.stevanrose.carbon_two.employee.web.dto;

import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class EmployeeResponse {
  private UUID id;
  private String email;
  private String department;
  private EmploymentType employmentType;
  private WorkPattern workPattern;
  private UUID officeId;
  private Instant createdAt;
  private Instant updatedAt;
}
