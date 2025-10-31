package com.stevanrose.carbon_two.office.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "office",
    uniqueConstraints = @UniqueConstraint(name = "uq_Office_code", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(nullable = false, updatable = false)
  private UUID id;

  @NotBlank
  @Column(nullable = false)
  private String code;

  @NotBlank
  @Column(nullable = false)
  private String name;

  private String address;

  @NotBlank
  @Column(name = "gridregioncode", nullable = false)
  private String gridRegionCode;

  private Double floorAreaM2;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    if (this.createdAt == null) this.createdAt = now;
    if (this.updatedAt == null) this.updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
