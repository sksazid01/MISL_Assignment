package com.Assignment.repository;

import com.Assignment.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    List<Employee> findByDepartment(String department);
    
    List<Employee> findByIsActive(Boolean isActive);
    
    List<Employee> findByDepartmentAndIsActive(String department, Boolean isActive);
    
    Optional<Employee> findByUserId(Long userId);
}
