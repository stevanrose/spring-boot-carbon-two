package com.stevanrose.carbon_two.energystatement.repository;

import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnergyStatementRepository extends JpaRepository<EnergyStatement, UUID> {

  boolean existsByOfficeIdAndYearAndMonth(UUID officeId, Integer year, Integer month);

  Page<EnergyStatement> findByOfficeId(UUID officeId, Pageable pageable);

  Optional<EnergyStatement> findByIdAndOfficeId(UUID id, UUID officeId);

  Optional<EnergyStatement> findByOfficeIdAndYearAndMonth(
      UUID officeId, Integer year, Integer month);

  long deleteByOfficeIdAndYearAndMonth(UUID officeId, Integer year, Integer month);
}
