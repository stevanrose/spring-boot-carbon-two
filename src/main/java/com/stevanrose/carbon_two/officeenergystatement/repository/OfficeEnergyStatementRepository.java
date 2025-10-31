package com.stevanrose.carbon_two.officeenergystatement.repository;

import com.stevanrose.carbon_two.officeenergystatement.domain.OfficeEnergyStatement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeEnergyStatementRepository
    extends JpaRepository<OfficeEnergyStatement, UUID> {

  boolean existsByOfficeIdAndYearAndMonth(UUID officeId, Integer year, Integer month);
}
