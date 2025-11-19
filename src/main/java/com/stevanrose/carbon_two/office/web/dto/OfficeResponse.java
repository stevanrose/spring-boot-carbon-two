package com.stevanrose.carbon_two.office.web.dto;

import java.time.Instant;
import java.util.UUID;

public record OfficeResponse(
    UUID id,
    String code,
    String name,
    String address,
    String gridRegionCode,
    Double floorAreaM2,
    Instant createdAt,
    Instant updatedAt) {}
