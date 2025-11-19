package com.stevanrose.carbon_two.energystatement.web.dto;

import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EnergyStatementRequest(
    @Min(1900) @Max(3000) Integer year,
    @Min(1) @Max(12) Integer month,
    @NotNull @PositiveOrZero Double electricityKwh,
    HeatingFuelType heatingFuelType,
    @PositiveOrZero Double heatingEnergyKwh,
    @PositiveOrZero Double renewablePpasKwh,
    String notes) {
  public EnergyStatementRequest {
    if (heatingFuelType == null) {
      heatingFuelType = HeatingFuelType.NONE;
    }
  }
}
