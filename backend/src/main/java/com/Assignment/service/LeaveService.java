package com.Assignment.service;

import com.Assignment.dto.LeaveStatsResponse;
import com.Assignment.dto.LeaveRequest;
import com.Assignment.dto.LeaveResponse;
import com.Assignment.dto.LeaveStatusUpdateRequest;
import com.Assignment.dto.MessageResponse;
import com.Assignment.entity.Employee;
import com.Assignment.entity.Leave;
import com.Assignment.entity.LeaveStatus;
import com.Assignment.entity.User;
import com.Assignment.exception.BadRequestException;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.LeaveRepository;
import com.Assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaves(LeaveStatus status, Long employeeId) {
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

        return leaves.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeaveResponse getLeaveById(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        return mapToResponse(leave);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getLeavesByEmployee(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LeaveResponse applyLeave(LeaveRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Error: End date cannot be before start date!");
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
        return mapToResponse(savedLeave);
    }

    public LeaveResponse updateLeave(Long id, LeaveRequest request) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Error: Cannot update a leave that is already " + leave.getStatus());
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Error: End date cannot be before start date!");
        }

        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());

        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    public LeaveResponse updateLeaveStatus(Long id, LeaveStatusUpdateRequest request) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        if (request.getStatus() == LeaveStatus.REJECTED &&
                (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty())) {
            throw new BadRequestException("Error: Rejection reason is required when rejecting a leave!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User approver = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Approver user not found"));

        leave.setStatus(request.getStatus());
        leave.setApprovedBy(approver);
        leave.setApprovalDate(LocalDateTime.now());

        if (request.getStatus() == LeaveStatus.REJECTED) {
            leave.setRejectionReason(request.getRejectionReason());
        }

        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    public MessageResponse deleteLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Error: Cannot delete a leave that is already " + leave.getStatus());
        }

        leaveRepository.delete(leave);
        return new MessageResponse("Leave deleted successfully!");
    }

    @Transactional(readOnly = true)
    public LeaveStatsResponse getEmployeeLeaveStats(Long employeeId) {
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

        return new LeaveStatsResponse(totalLeaves, approvedLeaves, pendingLeaves, rejectedLeaves, totalDaysOnLeave);
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
                .totalDays(leave.getTotalDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .appliedDate(leave.getAppliedDate())
                .updatedAt(leave.getUpdatedAt())
                .approvedBy(leave.getApprovedBy() != null ? leave.getApprovedBy().getUsername() : null)
                .approvalDate(leave.getApprovalDate())
                .rejectionReason(leave.getRejectionReason())
                .build();
    }
}
