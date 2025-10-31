package com.stevanrose.carbon_two.officeenergystatement.web.dto;

import com.stevanrose.carbon_two.officeenergystatement.domain.HeatingFuelType;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class OfficeEnergyStatementResponse {
  private UUID id;
  private UUID officeId;
  private Integer year;
  private Integer month;
  private Double electricityKwh;
  private HeatingFuelType heatingFuelType;
  private Double heatingEnergyKwh;
  private Double renewablePpasKwh;
  private String notes;
  private Instant createdAt;
  private Instant updatedAt;
}
