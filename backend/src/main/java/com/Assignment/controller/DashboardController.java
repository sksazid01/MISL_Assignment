package com.Assignment.controller;

import com.Assignment.dto.DashboardStats;
import com.Assignment.entity.LeaveStatus;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        // Use count queries for better performance
        Long totalEmployees = employeeRepository.count();
        Long activeEmployees = employeeRepository.countByIsActive(true);
        Long totalLeaves = leaveRepository.count();
        Long pendingLeaves = leaveRepository.countByStatus(LeaveStatus.PENDING);
        Long approvedLeaves = leaveRepository.countByStatus(LeaveStatus.APPROVED);
        Long rejectedLeaves = leaveRepository.countByStatus(LeaveStatus.REJECTED);

        DashboardStats stats = DashboardStats.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .totalLeaves(totalLeaves)
                .pendingLeaves(pendingLeaves)
                .approvedLeaves(approvedLeaves)
                .rejectedLeaves(rejectedLeaves)
                .build();

        return ResponseEntity.ok(stats);
    }
}
