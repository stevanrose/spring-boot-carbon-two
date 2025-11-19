package com.stevanrose.carbon_two.office.web.dto;

public record OfficeUpdateRequest(
    String code, String name, String address, String gridRegionCode, Double floorAreaM2) {}
