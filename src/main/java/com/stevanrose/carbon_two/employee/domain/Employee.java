package com.stevanrose.carbon_two.employee.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "department")
  private String department;

  @Enumerated(EnumType.STRING)
  @Column(name = "employmenttype", nullable = false)
  private EmploymentType employmentType;

  @Enumerated(EnumType.STRING)
  @Column(name = "workpattern", nullable = false)
  private WorkPattern workPattern;

  @Column(name = "officeid")
  private UUID officeId;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private Instant updatedAt;
}
