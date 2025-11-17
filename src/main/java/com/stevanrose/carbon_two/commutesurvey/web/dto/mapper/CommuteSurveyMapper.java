package com.stevanrose.carbon_two.commutesurvey.web.dto.mapper;

import com.stevanrose.carbon_two.commutesurvey.domain.CommuteSurvey;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyRequest;
import com.stevanrose.carbon_two.commutesurvey.web.dto.CommuteSurveyResponse;
import com.stevanrose.carbon_two.employee.domain.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CommuteSurveyMapper {

  @Mapping(
      target = "id",
      expression = "java(dto.id() != null ? dto.id() : java.util.UUID.randomUUID())")
  @Mapping(target = "employee", source = "employee")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  CommuteSurvey toEntity(CommuteSurveyRequest dto, Employee employee);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "employee", source = "employee")
  @Mapping(target = "updatedAt", ignore = true)
  void update(CommuteSurveyRequest dto, @MappingTarget CommuteSurvey entity, Employee employee);

  @Mapping(target = "employeeId", expression = "java(entity.getEmployee().getId())")
  CommuteSurveyResponse toResponse(CommuteSurvey entity);
}
