package com.stevanrose.carbon_two.employee.controller;

import com.stevanrose.carbon_two.common.paging.PageResponse;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService service;
  private final EmployeeMapper mapper;

  @GetMapping("/{id}")
  @Operation(
      summary = "Get Employee by ID",
      description = "Retrieve a specific employee by its ID.")
  @ApiResponse(
      responseCode = "200",
      description = "Employee found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = EmployeeResponse.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Employee not found",
      content = @Content(schema = @Schema(implementation = Void.class)))
  public EmployeeResponse getOffice(@PathVariable UUID id) {
    return mapper.toResponse(service.findById(id));
  }

  @GetMapping
  @Operation(summary = "List Employees", description = "Retrieve a paginated list of employees.")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = EmployeeResponse.class)))
  public PageResponse<EmployeeResponse> list(@ParameterObject Pageable pageable) {
    var page = service.list(pageable).map(mapper::toResponse);
    return PageResponse.of(page);
  }

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
    Employee saved = service.create(mapper.toEntity(request));
    var response = mapper.toResponse(saved);
    var location = uri.path("/api/employees/{id}").buildAndExpand(saved.getId()).toUri();
    return ResponseEntity.created(location).body(response);
  }
}
