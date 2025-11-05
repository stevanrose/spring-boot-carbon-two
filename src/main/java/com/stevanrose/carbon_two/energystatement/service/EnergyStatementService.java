package com.stevanrose.carbon_two.energystatement.service;

import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.repository.EnergyStatementRepository;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
import com.stevanrose.carbon_two.energystatement.web.dto.mapper.EnergyStatementMapper;
import com.stevanrose.carbon_two.office.domain.Office;
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

  public record UpsertResult(EnergyStatement entity, boolean created) {}

  @Transactional(readOnly = true)
  public EnergyStatement findByIdAndOfficeId(UUID id, UUID officeId) {
    return energyStatementRepository
        .findByIdAndOfficeId(id, officeId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    "Energy statement not found with id: " + id + " for office id: " + officeId));
  }

  @Transactional(readOnly = true)
  public Page<EnergyStatement> listByOfficeId(UUID officeId, Pageable pageable) {

    if (!officeRepository.existsById(officeId)) {
      throw new EntityNotFoundException("Office not found with id: " + officeId);
    }

    return energyStatementRepository.findByOfficeId(officeId, pageable);
  }

  @Transactional
  public UpsertResult upsert(UUID officeId, int year, int month, EnergyStatementRequest request) {
    Office office =
        officeRepository
            .findById(officeId)
            .orElseThrow(() -> new EntityNotFoundException("Office not found: " + officeId));

    request.setYear(year);
    request.setMonth(month);

    var existing = energyStatementRepository.findByOfficeIdAndYearAndMonth(officeId, year, month);
    if (existing.isPresent()) {
      var entity = existing.get();
      energyStatementMapper.update(entity, request);
      var saved = energyStatementRepository.save(entity);
      return new UpsertResult(saved, false);
    } else {
      var entity = energyStatementMapper.toEntity(request, office);
      var saved = energyStatementRepository.save(entity);
      return new UpsertResult(saved, true);
    }
  }
}
