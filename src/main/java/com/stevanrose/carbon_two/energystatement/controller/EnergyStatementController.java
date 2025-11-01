package com.stevanrose.carbon_two.energystatement.controller;

import com.stevanrose.carbon_two.common.paging.PageResponse;
import com.stevanrose.carbon_two.energystatement.service.EnergyStatementService;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementResponse;
import com.stevanrose.carbon_two.energystatement.web.dto.mapper.EnergyStatementMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api/offices/{officeId}/energy-statements")
@RequiredArgsConstructor
public class EnergyStatementController {

  private final EnergyStatementService service;
  private final EnergyStatementMapper mapper;

  @GetMapping
  @Operation(summary = "List office energy statements")
  @ApiResponse(responseCode = "200", description = "List of office energy statements retrieved")
  @ApiResponse(responseCode = "404", description = "Office not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public PageResponse<EnergyStatementResponse> listByOfficeId(
      @PathVariable UUID officeId,
      @ParameterObject
          @PageableDefault(size = 20)
          @SortDefault.SortDefaults({
            @SortDefault(sort = "year", direction = Sort.Direction.DESC),
            @SortDefault(sort = "month", direction = Sort.Direction.DESC)
          })
          Pageable pageable) {

    Page<EnergyStatementResponse> page =
        service.listByOfficeId(officeId, pageable).map(mapper::toResponse);

    return PageResponse.of(page);
  }

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
  public ResponseEntity<EnergyStatementResponse> create(
      @PathVariable UUID officeId,
      @Valid @RequestBody EnergyStatementRequest request,
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
