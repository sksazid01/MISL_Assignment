package com.Assignment.controller;

import com.Assignment.dto.DashboardStats;
import com.Assignment.dto.LeaveResponse;
import com.Assignment.entity.Leave;
import com.Assignment.entity.LeaveStatus;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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

        // Fetch only recent 5 leaves with necessary data
        List<Leave> recentLeavesList = leaveRepository.findTop5ByOrderByAppliedDateDesc();
        List<LeaveResponse> recentLeaves = recentLeavesList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        DashboardStats stats = DashboardStats.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .totalLeaves(totalLeaves)
                .pendingLeaves(pendingLeaves)
                .approvedLeaves(approvedLeaves)
                .rejectedLeaves(rejectedLeaves)
                .recentLeaves(recentLeaves)
                .build();

        return ResponseEntity.ok(stats);
    }

    private LeaveResponse mapToResponse(Leave leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getUser().getUsername())
                .department(leave.getEmployee().getDepartment())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .appliedDate(leave.getAppliedDate())
                .approvalDate(leave.getApprovalDate())
                .approvedBy(leave.getApprovedBy())
                .rejectionReason(leave.getRejectionReason())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }
}
