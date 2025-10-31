package com.stevanrose.carbon_two.office.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.stevanrose.carbon_two.common.paging.PageResponse;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.OfficePageResponse;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeResponse;
import com.stevanrose.carbon_two.office.web.dto.OfficeUpdateRequest;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
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
@RequestMapping("/api/offices")
@RequiredArgsConstructor
public class OfficeController {

  private final OfficeService officeService;
  private final OfficeMapper officeMapper;
  private final ObjectMapper objectMapper = JsonMapper.builder().build();

  @GetMapping
  @Operation(summary = "List Offices", description = "Retrieve a paginated list of offices.")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = OfficePageResponse.class)))
  public PageResponse<OfficeResponse> list(@ParameterObject Pageable pageable) {
    var page = officeService.list(pageable).map(officeMapper::toResponse);
    return PageResponse.of(page);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get Office by ID", description = "Retrieve a specific office by its ID.")
  @ApiResponse(
      responseCode = "200",
      description = "Office found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = OfficeResponse.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Office not found",
      content = @Content(schema = @Schema(implementation = Void.class)))
  public OfficeResponse getOffice(@PathVariable UUID id) {
    return officeMapper.toResponse(officeService.findById(id));
  }

  @PostMapping
  @Operation(summary = "Create Office", description = "Create a new office.")
  @ApiResponse(
      responseCode = "201",
      description = "Office created",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = OfficeResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid input",
      content = @Content(schema = @Schema(implementation = Void.class)))
  @ApiResponse(
      responseCode = "409",
      description = "Office already exists",
      content = @Content(schema = @Schema(implementation = Void.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = Void.class)))
  public ResponseEntity<OfficeResponse> create(
      @Valid @RequestBody OfficeRequest request, UriComponentsBuilder uri) {
    Office saved = officeService.create(officeMapper.toEntity(request));
    var response = officeMapper.toResponse(saved);
    var location = uri.path("/api/offices/{id}").buildAndExpand(saved.getId()).toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an office (full)")
  @ApiResponse(responseCode = "200", description = "Office updated successfully")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid input",
      content = @Content(schema = @Schema(implementation = Void.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Office not found",
      content = @Content(schema = @Schema(implementation = Void.class)))
  public OfficeResponse put(@PathVariable UUID id, @Valid @RequestBody OfficeRequest body) {
    OfficeUpdateRequest update = new OfficeUpdateRequest();
    update.setCode(body.getCode());
    update.setName(body.getName());
    update.setAddress(body.getAddress());
    update.setGridRegionCode(body.getGridRegionCode());
    update.setFloorAreaM2(body.getFloorAreaM2());

    return officeMapper.toResponse(officeService.update(id, update));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete an office by ID",
      description = "Delete a specific office by its ID.")
  @ApiResponse(
      responseCode = "204",
      description = "Office deleted successfully",
      content = @Content)
  @ApiResponse(
      responseCode = "404",
      description = "Office not found",
      content = @Content(schema = @Schema(implementation = Void.class)))
  public ResponseEntity<Void> deleteOffice(@PathVariable UUID id) {
    officeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
