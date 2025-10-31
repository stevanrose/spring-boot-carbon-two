package com.stevanrose.carbon_two.office.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OfficeRequest {

  @NotBlank private String code;

  @NotBlank private String name;

  private String address;

  @NotBlank private String gridRegionCode;

  private Double floorAreaM2;
}
