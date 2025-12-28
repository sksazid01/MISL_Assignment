package com.Assignment.controller;

import com.Assignment.dto.EmployeeRequest;
import com.Assignment.dto.EmployeeResponse;
import com.Assignment.dto.MessageResponse;
import com.Assignment.entity.Employee;
import com.Assignment.entity.User;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return ResponseEntity.ok(mapToResponse(employee));
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable String department) {
        List<EmployeeResponse> employees = employeeRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EmployeeResponse>> getActiveEmployees() {
        List<EmployeeResponse> employees = employeeRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        // Validate that user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        
        // Check if user is already linked to an employee
        if (employeeRepository.findByUserId(user.getId()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Error: User is already linked to an employee!"));
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToResponse(savedEmployee));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update only non-user fields
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setJoinDate(request.getJoinDate());

        // Note: User cannot be changed after employee creation
        // If you need to change the linked user, delete and recreate the employee

        Employee updatedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(mapToResponse(updatedEmployee));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        employee.setIsActive(false);
        employeeRepository.save(employee);
        
        return ResponseEntity.ok(new MessageResponse("Employee deactivated successfully!"));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        employee.setIsActive(true);
        employeeRepository.save(employee);
        
        return ResponseEntity.ok(new MessageResponse("Employee activated successfully!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        employeeRepository.delete(employee);
        return ResponseEntity.ok(new MessageResponse("Employee deleted successfully!"));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getMyEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElse(null);
        
        if (employee == null) {
            return ResponseEntity.ok(new MessageResponse("No employee record linked to this user"));
        }
        
        return ResponseEntity.ok(mapToResponse(employee));
    }

    // Helper method to map Employee entity to EmployeeResponse DTO
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
