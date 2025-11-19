package com.stevanrose.carbon_two.employee.web.dto;

import com.stevanrose.carbon_two.employee.domain.EmploymentType;
import com.stevanrose.carbon_two.employee.domain.WorkPattern;
import java.time.Instant;
import java.util.UUID;

public record EmployeeResponse(
    UUID id,
    String email,
    String department,
    EmploymentType employmentType,
    WorkPattern workPattern,
    UUID officeId,
    Instant createdAt,
    Instant updatedAt) {}
