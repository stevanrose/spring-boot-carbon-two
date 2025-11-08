package com.stevanrose.carbon_two.employee.web.dto.mapper;

import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeRequest;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeResponse;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

  Employee toEntity(EmployeeRequest request);

  EmployeeResponse toResponse(Employee entity);

  void update(@MappingTarget Employee entity, EmployeeRequest request);
}
