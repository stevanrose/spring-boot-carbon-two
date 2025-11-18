package com.stevanrose.carbon_two.commutesurvey.controller;

import com.stevanrose.carbon_two.common.paging.PageResponse;
import com.stevanrose.carbon_two.commutesurvey.service.CommuteSurveyService;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyResponse;
import com.stevanrose.carbon_two.commutesurvey.web.dto.mapper.CommuteSurveyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/employees/{employeeId}/commute-surveys")
@RequiredArgsConstructor
public class CommuteSurveyController {

  private final CommuteSurveyService service;
  private final CommuteSurveyMapper mapper;

  @PutMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create or update a commute survey for an employee")
  @ApiResponse(responseCode = "201", description = "Commute survey created or updated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request data")
  @ApiResponse(responseCode = "404", description = "Employee not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<CommuteSurveyResponse> upsert(
      @PathVariable UUID employeeId,
      @Valid @RequestBody CommuteSurveyRequest request,
      UriComponentsBuilder uri) {

    var result = service.upsert(employeeId, request);
    var body = mapper.toResponse(result.entity());

    if (result.created()) {
      var location =
          uri.path("/api/employees/{employeeId}/commute-surveys/{id}")
              .buildAndExpand(employeeId, result.entity().getId())
              .toUri();
      return ResponseEntity.created(location).body(body);

    } else {
      return ResponseEntity.ok(body);
    }
  }

  @GetMapping
  @Operation(
      summary = "List Commute Surveys by Employee ID",
      description = "Retrieve a paginated list of commute surveys for a specific employee.")
  @ApiResponse(responseCode = "200", description = "List of commute surveys retrieved successfully")
  @ApiResponse(responseCode = "404", description = "Employee not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public PageResponse<CommuteSurveyResponse> listByEmployeeId(
      @PathVariable UUID employeeId, @ParameterObject Pageable pageable) {

    Page<CommuteSurveyResponse> page =
        service.listByEmployeeId(employeeId, pageable).map(mapper::toResponse);

    return PageResponse.of(page);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get Commute Survey by ID",
      description = "Retrieve a specific commute survey by its ID for a given employee.")
  @ApiResponse(responseCode = "200", description = "Commute survey retrieved successfully")
  @ApiResponse(responseCode = "404", description = "Employee or commute survey not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<CommuteSurveyResponse> getById(
      @PathVariable UUID employeeId, @PathVariable UUID id) {
    var entity = service.findByIdAndEmployeeId(id, employeeId);
    var response = mapper.toResponse(entity);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete Commute Survey by ID",
      description = "Delete a specific commute survey by its ID for a given employee.")
  @ApiResponse(responseCode = "204", description = "Commute survey deleted successfully")
  @ApiResponse(responseCode = "404", description = "Employee or commute survey not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID employeeId, @PathVariable UUID id) {
    service.deleteByEmployeeIdAndId(employeeId, id);
  }
}
