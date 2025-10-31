package com.stevanrose.carbon_two.officeenergystatement.domain;

import com.stevanrose.carbon_two.office.domain.Office;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "officeenergystatement",
    indexes = {
      @Index(
          name = "idx_OfficeEnergyStatement_officeId_year_month",
          columnList = "officeid, year, month")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeEnergyStatement {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "officeid", nullable = false)
  private Office office;

  @Min(1900)
  @Max(3000)
  @Column(nullable = false)
  private Integer year;

  @Min(1)
  @Max(12)
  @Column(nullable = false)
  private Integer month;

  @NotNull
  @PositiveOrZero
  @Column(name = "electricitykwh", nullable = false)
  private Double electricityKwh;

  @Enumerated(EnumType.STRING)
  @Column(name = "heatingfueltype", nullable = false)
  private HeatingFuelType heatingFuelType = HeatingFuelType.NONE;

  @PositiveOrZero
  @Column(name = "heatingenergykwh")
  private Double heatingEnergyKwh;

  @PositiveOrZero
  @Column(name = "renewableppaskwh")
  private Double renewablePpasKwh;

  private String notes;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    this.createdAt = (this.createdAt == null) ? now : this.createdAt;
    this.updatedAt = (this.updatedAt == null) ? now : this.updatedAt;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
