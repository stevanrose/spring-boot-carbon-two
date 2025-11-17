package com.stevanrose.carbon_two.commutesurvey.web.dto;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CommuteSurveyRequest(
    // Optional: if null -> create; if non-null -> update
    UUID id,
    @NotNull OffsetDateTime surveyDate,
    @NotNull CommuteMode primaryMode,
    @NotNull @PositiveOrZero Double oneWayDistanceKm,
    @NotNull @Min(0) @Max(7) Integer daysPerWeekCommuting, // note spelling, rename if you like
    @Min(1) Integer carOccupancy,
    String notes) {}
