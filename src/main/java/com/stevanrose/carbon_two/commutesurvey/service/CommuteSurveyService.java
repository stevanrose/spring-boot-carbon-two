package com.stevanrose.carbon_two.commutesurvey.service;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.commutesurvey.repository.CommuteSurveyRepository;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.mapper.CommuteSurveyMapper;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommuteSurveyService {

  private final CommuteSurveyRepository commuteSurveyRepository;
  private final EmployeeRepository employeeRepository;
  private final CommuteSurveyMapper mapper;

  public record UpsertResult(CommuteSurvey entity, boolean created) {}

  @Transactional
  public UpsertResult upsert(UUID employeeId, CommuteSurveyRequest dto) {
    var employee =
        employeeRepository
            .findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

    if (dto.id() == null) {
      var entity = mapper.toEntity(dto, employee);
      var saved = commuteSurveyRepository.save(entity);
      return new UpsertResult(saved, true);
    } else {
      var existing =
          commuteSurveyRepository
              .findById(dto.id())
              .orElseThrow(() -> new IllegalArgumentException("CommuteSurvey not found"));
      mapper.update(dto, existing, employee);
      var saved = commuteSurveyRepository.save(existing);
      return new UpsertResult(saved, false);
    }
  }

  @Transactional(readOnly = true)
  public Page<CommuteSurvey> listByEmployeeId(UUID employeeId, Pageable pageable) {
    if (!employeeRepository.existsById(employeeId)) {
      throw new EntityNotFoundException("Employee not found with id: " + employeeId);
    }

    return commuteSurveyRepository.findByEmployeeId(employeeId, pageable);
  }

  @Transactional(readOnly = true)
  public CommuteSurvey findByIdAndEmployeeId(UUID id, UUID employeeId) {
    return commuteSurveyRepository
        .findByIdAndEmployeeId(id, employeeId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    "CommuteSurvey not found with id: " + id + " for employee id: " + employeeId));
  }
}
