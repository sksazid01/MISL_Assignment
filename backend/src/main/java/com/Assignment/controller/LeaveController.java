package com.Assignment.controller;

import com.Assignment.dto.LeaveRequest;
import com.Assignment.dto.LeaveResponse;
import com.Assignment.dto.LeaveStatusUpdateRequest;
import com.Assignment.dto.MessageResponse;
import com.Assignment.entity.Employee;
import com.Assignment.entity.Leave;
import com.Assignment.entity.LeaveStatus;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.LeaveRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<LeaveResponse>> getAllLeaves(
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) Long employeeId) {
        
        List<Leave> leaves;
        
        if (employeeId != null && status != null) {
            leaves = leaveRepository.findByEmployeeIdAndStatus(employeeId, status);
        } else if (employeeId != null) {
            leaves = leaveRepository.findByEmployeeId(employeeId);
        } else if (status != null) {
            leaves = leaveRepository.findByStatus(status);
        } else {
            leaves = leaveRepository.findAll();
        }
        
        List<LeaveResponse> response = leaves.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getLeaveById(@PathVariable Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        return ResponseEntity.ok(mapToResponse(leave));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable Long employeeId) {
        List<LeaveResponse> leaves = leaveRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
        List<LeaveResponse> leaves = leaveRepository.findByStatus(LeaveStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaves);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> applyLeave(@Valid @RequestBody LeaveRequest request) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        // Validate date range
        if (request.getEndDate().isBefore(request.getStartDate())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: End date cannot be before start date!"));
        }

        Leave leave = Leave.builder()
                .employee(employee)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToResponse(savedLeave));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updateLeave(@PathVariable Long id, @Valid @RequestBody LeaveRequest request) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        // Only allow updates for pending leaves
        if (leave.getStatus() != LeaveStatus.PENDING) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Cannot update a leave that is already " + leave.getStatus()));
        }

        // Validate date range
        if (request.getEndDate().isBefore(request.getStartDate())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: End date cannot be before start date!"));
        }

        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());

        Leave updatedLeave = leaveRepository.save(leave);
        return ResponseEntity.ok(mapToResponse(updatedLeave));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLeaveStatus(
            @PathVariable Long id,
            @Valid @RequestBody LeaveStatusUpdateRequest request) {
        
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        // Validate rejection reason
        if (request.getStatus() == LeaveStatus.REJECTED && 
            (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Rejection reason is required when rejecting a leave!"));
        }

        // Get current user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String approver = authentication.getName();

        leave.setStatus(request.getStatus());
        leave.setApprovedBy(approver);
        leave.setApprovalDate(LocalDateTime.now());
        
        if (request.getStatus() == LeaveStatus.REJECTED) {
            leave.setRejectionReason(request.getRejectionReason());
        }

        Leave updatedLeave = leaveRepository.save(leave);
        return ResponseEntity.ok(mapToResponse(updatedLeave));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        // Only allow deletion of pending leaves
        if (leave.getStatus() != LeaveStatus.PENDING) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Cannot delete a leave that is already " + leave.getStatus()));
        }

        leaveRepository.delete(leave);
        return ResponseEntity.ok(new MessageResponse("Leave deleted successfully!"));
    }

    @GetMapping("/stats/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getEmployeeLeaveStats(@PathVariable Long employeeId) {
        List<Leave> allLeaves = leaveRepository.findByEmployeeId(employeeId);
        
        long totalLeaves = allLeaves.size();
        long approvedLeaves = allLeaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED)
                .count();
        long pendingLeaves = allLeaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.PENDING)
                .count();
        long rejectedLeaves = allLeaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.REJECTED)
                .count();
        long totalDaysOnLeave = allLeaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED)
                .mapToLong(Leave::getTotalDays)
                .sum();

        return ResponseEntity.ok(new LeaveStats(
                totalLeaves,
                approvedLeaves,
                pendingLeaves,
                rejectedLeaves,
                totalDaysOnLeave
        ));
    }

    // Helper method to map Leave entity to LeaveResponse DTO
    private LeaveResponse mapToResponse(Leave leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getName())
                .department(leave.getEmployee().getDepartment())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .appliedDate(leave.getAppliedDate())
                .updatedAt(leave.getUpdatedAt())
                .approvedBy(leave.getApprovedBy())
                .approvalDate(leave.getApprovalDate())
                .rejectionReason(leave.getRejectionReason())
                .build();
    }

    // Inner class for stats response
    @Data
    @AllArgsConstructor
    static class LeaveStats {
        private long totalLeaves;
        private long approvedLeaves;
        private long pendingLeaves;
        private long rejectedLeaves;
        private long totalDaysOnLeave;
    }
}
