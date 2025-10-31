package com.stevanrose.carbon_two.officeenergystatement.controller;

import com.stevanrose.carbon_two.officeenergystatement.service.OfficeEnergyStatementService;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.OfficeEnergyStatementRequest;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.OfficeEnergyStatementResponse;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.mapper.OfficeEnergyStatementMapper;
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
@RequestMapping("api/offices/{officeId}/energy-statements")
@RequiredArgsConstructor
public class OfficeEnergyStatementController {

  private final OfficeEnergyStatementService service;
  private final OfficeEnergyStatementMapper mapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new office energy statement")
  @ApiResponse(responseCode = "201", description = "Office energy statement created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  @ApiResponse(responseCode = "404", description = "Office not found")
  @ApiResponse(
      responseCode = "409",
      description = "Energy statement already exists for the given month and year")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<OfficeEnergyStatementResponse> create(
      @PathVariable UUID officeId,
      @Valid @RequestBody OfficeEnergyStatementRequest request,
      UriComponentsBuilder uri) {

    var saved = service.create(officeId, request);
    var body = mapper.toResponse(saved);
    var location =
        uri.path("/api/offices/{officeId}/energy-statements/{id}")
            .buildAndExpand(officeId, saved.getId())
            .toUri();
    return ResponseEntity.created(location).body(body);
  }
}
