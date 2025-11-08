package com.stevanrose.carbon_two.employee.repository;

import com.stevanrose.carbon_two.employee.domain.Employee;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
  Optional<Employee> findByEmail(String email);
}
