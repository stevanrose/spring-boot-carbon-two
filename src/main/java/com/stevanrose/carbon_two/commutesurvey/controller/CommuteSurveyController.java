package com.stevanrose.carbon_two.commutesurvey.controller;

import com.stevanrose.carbon_two.commutesurvey.service.CommuteSurveyService;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyResponse;
import com.stevanrose.carbon_two.commutesurvey.web.dto.mapper.CommuteSurveyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
}
