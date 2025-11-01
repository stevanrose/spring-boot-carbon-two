package com.stevanrose.carbon_two.energystatement.service;

import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.repository.EnergyStatementRepository;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
import com.stevanrose.carbon_two.energystatement.web.dto.mapper.EnergyStatementMapper;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnergyStatementService {

  private final OfficeRepository officeRepository;
  private final EnergyStatementRepository energyStatementRepository;
  private final EnergyStatementMapper energyStatementMapper;

  @Transactional(readOnly = true)
  public Page<EnergyStatement> listByOfficeId(UUID officeId, Pageable pageable) {

    if (!officeRepository.existsById(officeId)) {
      throw new EntityNotFoundException("Office not found with id: " + officeId);
    }

    return energyStatementRepository.findByOfficeId(officeId, pageable);
  }

  @Transactional
  public EnergyStatement create(UUID officeId, EnergyStatementRequest request) {

    var office =
        officeRepository
            .findById(officeId)
            .orElseThrow(
                () -> new EntityNotFoundException("Office not found with id: " + officeId));

    if (energyStatementRepository.existsByOfficeIdAndYearAndMonth(
        officeId, request.getYear(), request.getMonth())) {
      throw new IllegalStateException(
          "Energy statement already exists for office id: "
              + officeId
              + ", year: "
              + request.getYear()
              + ", month: "
              + request.getMonth());
    }

    var officeEnergyStatement = energyStatementMapper.toEntity(request, office);
    return energyStatementRepository.save(officeEnergyStatement);
  }
}
