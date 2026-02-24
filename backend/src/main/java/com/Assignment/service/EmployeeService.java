package com.Assignment.service;

import com.Assignment.dto.EmployeeRequest;
import com.Assignment.dto.EmployeeResponse;
import com.Assignment.dto.MessageResponse;
import com.Assignment.entity.Employee;
import com.Assignment.entity.User;
import com.Assignment.exception.ConflictException;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    public List<EmployeeResponse> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> getActiveEmployees() {
        return employeeRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        if (employeeRepository.findByUserId(user.getId()).isPresent()) {
            throw new ConflictException("Error: User is already linked to an employee!");
        }

        Employee employee = Employee.builder()
                .user(user)
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .phoneNumber(request.getPhoneNumber())
                .joinDate(request.getJoinDate())
                .isActive(true)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setJoinDate(request.getJoinDate());

        // Note: User cannot be changed after employee creation
        // If you need to change the linked user, delete and recreate the employee

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    public MessageResponse deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setIsActive(false);
        employeeRepository.save(employee);

        return new MessageResponse("Employee deactivated successfully!");
    }

    public MessageResponse activateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setIsActive(true);
        employeeRepository.save(employee);

        return new MessageResponse("Employee activated successfully!");
    }

    public MessageResponse deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employeeRepository.delete(employee);
        return new MessageResponse("Employee deleted successfully!");
    }

    public EmployeeResponse getMyEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElse(null);

        if (employee == null) {
            return null;
        }

        return mapToResponse(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(employee.getUser().getId())
                .username(employee.getUser().getUsername())
                .email(employee.getUser().getEmail())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .phoneNumber(employee.getPhoneNumber())
                .joinDate(employee.getJoinDate())
                .isActive(employee.getIsActive())
                .createdAt(employee.getCreatedAt())
                .totalLeaves(employee.getLeaves() != null ? employee.getLeaves().size() : 0)
                .build();
    }
}
