package com.stevanrose.carbon_two.commutesurvey.domain;

import com.stevanrose.carbon_two.employee.domain.Employee;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "commutesurvey",
    indexes = {@Index(name = "idx_employeecommutesurvey_employeeId", columnList = "employeeid")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommuteSurvey {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "employeeid", nullable = false)
  private Employee employee;

  @Column(name = "surveydate", nullable = false)
  private OffsetDateTime surveyDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "primarymode", nullable = false, length = 32)
  private CommuteMode primaryMode;

  @Column(name = "onewaydistancekm", nullable = false)
  private Double oneWayDistanceKm;

  @Column(name = "daysperweekcommuting", nullable = false)
  private Integer daysPerWeekCommuting;

  @Column(name = "caroccupancy")
  private Integer carOccupancy;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private Instant updatedAt;
}
