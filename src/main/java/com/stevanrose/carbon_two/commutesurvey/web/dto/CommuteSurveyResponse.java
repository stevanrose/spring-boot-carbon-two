package com.stevanrose.carbon_two.commutesurvey.web.dto;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CommuteSurveyResponse(
    UUID id,
    UUID employeeId,
    OffsetDateTime surveyDate,
    CommuteMode primaryMode,
    Double oneWayDistanceKm,
    Integer daysPerWeekCommuting,
    Integer carOccupancy,
    String notes,
    Instant createdAt,
    Instant updatedAt) {}
