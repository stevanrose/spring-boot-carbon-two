package com.stevanrose.carbon_two.office.controller;

import com.stevanrose.carbon_two.common.paging.PageResponse;
import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.OfficePageResponse;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeResponse;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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

    @GetMapping
    @Operation(summary = "List Offices", description = "Retrieve a paginated list of offices.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OfficePageResponse.class)))
    public PageResponse<OfficeResponse> list(@ParameterObject Pageable pageable) {
        var page = officeService.list(pageable).map(officeMapper::toResponse);
        return PageResponse.of(page);
    }

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody OfficeRequest request, UriComponentsBuilder uri) {

        Office saved = officeService.create(officeMapper.toEntity(request));
        var response = officeMapper.toResponse(saved);
        var location = uri.path("/api/offices/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(response);

    }
}
