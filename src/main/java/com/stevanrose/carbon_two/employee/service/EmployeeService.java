package com.stevanrose.carbon_two.employee.service;

import com.stevanrose.carbon_two.employee.domain.Employee;
import com.stevanrose.carbon_two.employee.repository.EmployeeRepository;
import com.stevanrose.carbon_two.employee.web.dto.EmployeeUpdateRequest;
import com.stevanrose.carbon_two.employee.web.dto.mapper.EmployeeMapper;
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
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @Transactional
  public Employee create(Employee employee) {
    return employeeRepository.save(employee);
  }

  @Transactional(readOnly = true)
  public Page<Employee> list(Pageable pageable) {
    return employeeRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Employee findById(UUID id) {
    return employeeRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
  }

  @Transactional
  public Employee update(UUID id, EmployeeUpdateRequest request) {

    var existing = employeeRepository.findById(id);

    if (existing.isPresent()) {
      var entity = existing.get();
      employeeMapper.update(entity, request);
      return employeeRepository.save(entity);
    } else {
      throw new EntityNotFoundException("Employee not found with id: " + id);
    }
  }

  @Transactional
  public void delete(UUID id) {

    if (!employeeRepository.existsById(id)) {
      throw new EntityNotFoundException("Employee not found with id: " + id);
    }

    try {
      employeeRepository.deleteById(id);
      employeeRepository.flush();
    } catch (DataIntegrityViolationException e) {
      throw new IllegalStateException(
          "Cannot delete Employee with id: " + id + " due to existing references.");
    }
  }
}
