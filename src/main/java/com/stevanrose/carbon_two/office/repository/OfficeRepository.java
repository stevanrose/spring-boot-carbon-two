package com.stevanrose.carbon_two.office.repository;

import com.stevanrose.carbon_two.office.domain.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OfficeRepository extends JpaRepository<Office, UUID> {

    Optional<Office> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);

}
