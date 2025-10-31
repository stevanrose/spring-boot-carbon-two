package com.stevanrose.carbon_two.office.service;

import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import com.stevanrose.carbon_two.office.web.dto.OfficeUpdateRequest;
import com.stevanrose.carbon_two.office.web.dto.mapper.OfficeMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeService {

  private final OfficeRepository officeRepository;
  private final OfficeMapper officeMapper;

  @Transactional(readOnly = true)
  public Page<Office> list(Pageable pageable) {
    return officeRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Office findById(UUID id) {
    return officeRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + id));
  }

  @Transactional
  public Office create(Office office) {
    return officeRepository.save(office);
  }

  @Transactional
  public Office update(UUID id, OfficeUpdateRequest request) {

    Office entity = findById(id);

    if (request.getCode() != null && !request.getCode().equalsIgnoreCase(entity.getCode())) {

      officeRepository
          .findByCodeIgnoreCase(request.getCode())
          .ifPresent(
              existing -> {
                if (!existing.getId().equals(id)) {
                  throw new IllegalStateException(
                      "Office code already exists: " + request.getCode());
                }
              });
      entity.setCode(request.getCode());
      officeMapper.update(entity, request);
      return officeRepository.save(entity);
    }

    return officeRepository.save(entity);
  }

  @Transactional
  public void delete(UUID id) {
    if (!officeRepository.existsById(id)) {
      throw new EntityNotFoundException("Office not found with id: " + id);
    }
    try {
      officeRepository.deleteById(id);
      officeRepository.flush();
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalStateException("Office cannot be deleted due to existing references", ex);
    }
  }
}
