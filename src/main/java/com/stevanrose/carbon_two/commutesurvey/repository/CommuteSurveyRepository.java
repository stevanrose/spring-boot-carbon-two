package com.stevanrose.carbon_two.commutesurvey.repository;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommuteSurveyRepository extends JpaRepository<CommuteSurvey, UUID> {

  Page<CommuteSurvey> findByEmployeeId(UUID employeeId, Pageable pageable);

  Optional<CommuteSurvey> findByIdAndEmployeeId(UUID id, UUID employeeId);
}
