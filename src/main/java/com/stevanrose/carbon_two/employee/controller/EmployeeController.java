package com.stevanrose.carbon_two.employee.controller;

import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.service.EmployeeService;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeResponse;
import com.stevanrose.carbon_two.employee.web.dto.mapper.EmployeeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;
  private final EmployeeMapper employeeMapper;

  @PostMapping
  @Operation(summary = "Create Employee", description = "Create a new employee.")
  @ApiResponse(
      responseCode = "201",
      description = "Employee created",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = EmployeeResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid input",
      content = @Content(schema = @Schema(implementation = Void.class)))
  @ApiResponse(
      responseCode = "409",
      description = "Conflict",
      content = @Content(schema = @Schema(implementation = Void.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = Void.class)))
  public ResponseEntity<EmployeeResponse> create(
      @Valid @RequestBody EmployeeRequest request, UriComponentsBuilder uri) {
    Employee saved = employeeService.create(employeeMapper.toEntity(request));
    var response = employeeMapper.toResponse(saved);
    var location = uri.path("/api/employees/{id}").buildAndExpand(saved.getId()).toUri();
    return ResponseEntity.created(location).body(response);
  }
}
