package com.stevanrose.carbon_two.energystatement.web.dto;

import com.stevanrose.carbon_two.energystatement.domain.HeatingFuelType;
import java.time.Instant;
import java.util.UUID;

public record EnergyStatementResponse(
    UUID id,
    UUID officeId,
    Integer year,
    Integer month,
    Double electricityKwh,
    HeatingFuelType heatingFuelType,
    Double heatingEnergyKwh,
    Double renewablePpasKwh,
    String notes,
    Instant createdAt,
    Instant updatedAt) {}
