package com.stevanrose.carbon_two.commutesurvey.service;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.commutesurvey.repository.CommuteSurveyRepository;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.mapper.CommuteSurveyMapper;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
      // CREATE
      var entity = mapper.toEntity(dto, employee);
      var saved = commuteSurveyRepository.save(entity);
      return new UpsertResult(saved, true);
    } else {
      // UPDATE
      var existing =
          commuteSurveyRepository
              .findById(dto.id())
              .orElseThrow(() -> new IllegalArgumentException("CommuteSurvey not found"));
      mapper.update(dto, existing, employee);
      var saved = commuteSurveyRepository.save(existing);
      return new UpsertResult(saved, false);
    }
  }
}
