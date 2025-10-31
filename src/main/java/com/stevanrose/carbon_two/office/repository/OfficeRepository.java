package com.stevanrose.carbon_two.office.repository;

import com.stevanrose.carbon_two.office.domain.Office;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, UUID> {

  Optional<Office> findByCodeIgnoreCase(String code);

  boolean existsByCodeIgnoreCase(String code);
}
