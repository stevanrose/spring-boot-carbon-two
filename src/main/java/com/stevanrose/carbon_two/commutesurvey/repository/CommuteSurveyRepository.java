package com.stevanrose.carbon_two.commutesurvey.repository;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.employee.domain.Employee;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommuteSurveyRepository extends JpaRepository<CommuteSurvey, UUID> {

  List<CommuteSurvey> findByEmployee(Employee employee);

  List<CommuteSurvey> findByEmployeeAndSurveyDateGreaterThanEqual(
      Employee employee, OffsetDateTime surveyDate);

    Page<CommuteSurvey> findByEmployeeId(UUID employeeId, Pageable pageable);
}
