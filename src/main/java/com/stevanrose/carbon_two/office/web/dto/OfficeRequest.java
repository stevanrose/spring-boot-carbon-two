package com.stevanrose.carbon_two.office.web.dto;

import jakarta.validation.constraints.NotBlank;

public record OfficeRequest(
    @NotBlank String code,
    @NotBlank String name,
    String address,
    @NotBlank String gridRegionCode,
    Double floorAreaM2) {}
