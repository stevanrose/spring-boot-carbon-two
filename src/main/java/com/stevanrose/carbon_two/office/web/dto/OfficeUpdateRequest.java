package com.stevanrose.carbon_two.office.web.dto;

import lombok.Data;

@Data
public class OfficeUpdateRequest {
  private String code;
  private String name;
  private String address;
  private String gridRegionCode;
  private Double floorAreaM2;
}
