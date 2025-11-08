package com.stevanrose.carbon_two.employee.service;

import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import com.stevanrose.carbon_two.employee.web.dto.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @Transactional
  public Employee create(Employee employee) {
    return employeeRepository.save(employee);
  }
}
