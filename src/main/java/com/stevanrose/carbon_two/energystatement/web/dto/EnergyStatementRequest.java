package com.stevanrose.carbon_two.energystatement.web.dto;

import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class EnergyStatementRequest {

  @Min(1900)
  @Max(3000)
  private Integer year;

  @Min(1)
  @Max(12)
  private Integer month;

  @NotNull @PositiveOrZero private Double electricityKwh;

  private HeatingFuelType heatingFuelType = HeatingFuelType.NONE;

  @PositiveOrZero private Double heatingEnergyKwh;

  @PositiveOrZero private Double renewablePpasKwh;

  private String notes;
}
