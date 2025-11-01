package com.stevanrose.carbon_two.officeenergystatement.service;

import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import com.stevanrose.carbon_two.officeenergystatement.domain.OfficeEnergyStatement;
import com.stevanrose.carbon_two.officeenergystatement.repository.OfficeEnergyStatementRepository;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.OfficeEnergyStatementRequest;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.mapper.OfficeEnergyStatementMapper;
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
public class OfficeEnergyStatementService {

  private final OfficeRepository officeRepository;
  private final OfficeEnergyStatementRepository officeEnergyStatementRepository;
  private final OfficeEnergyStatementMapper officeEnergyStatementMapper;

  @Transactional(readOnly = true)
  public Page<OfficeEnergyStatement> listByOfficeId(UUID officeId, Pageable pageable) {

    if (!officeRepository.existsById(officeId)) {
      throw new EntityNotFoundException("Office not found with id: " + officeId);
    }

    return officeEnergyStatementRepository.findByOfficeId(officeId, pageable);
  }

  @Transactional
  public OfficeEnergyStatement create(UUID officeId, OfficeEnergyStatementRequest request) {

    var office =
        officeRepository
            .findById(officeId)
            .orElseThrow(
                () -> new EntityNotFoundException("Office not found with id: " + officeId));

    if (officeEnergyStatementRepository.existsByOfficeIdAndYearAndMonth(
        officeId, request.getYear(), request.getMonth())) {
      throw new IllegalStateException(
          "Energy statement already exists for office id: "
              + officeId
              + ", year: "
              + request.getYear()
              + ", month: "
              + request.getMonth());
    }

    var officeEnergyStatement = officeEnergyStatementMapper.toEntity(request, office);
    return officeEnergyStatementRepository.save(officeEnergyStatement);
  }
}
