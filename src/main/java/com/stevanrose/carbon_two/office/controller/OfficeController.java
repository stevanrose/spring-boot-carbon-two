package com.stevanrose.carbon_two.office.controller;

import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.service.OfficeService;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;
    private final OfficeMapper officeMapper;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody OfficeRequest request, UriComponentsBuilder uri) {

        Office saved = officeService.create(officeMapper.toEntity(request));
        var response = officeMapper.toResponse(saved);
        var location = uri.path("/api/offices/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(response);

    }
}
