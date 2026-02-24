package com.Assignment.service;

import com.Assignment.dto.DashboardStats;
import com.Assignment.entity.LeaveStatus;
import com.Assignment.repository.EmployeeRepository;
import com.Assignment.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;

    public DashboardStats getDashboardStats() {
        Long totalEmployees = employeeRepository.count();
        Long activeEmployees = employeeRepository.countByIsActive(true);
        Long totalLeaves = leaveRepository.count();
        Long pendingLeaves = leaveRepository.countByStatus(LeaveStatus.PENDING);
        Long approvedLeaves = leaveRepository.countByStatus(LeaveStatus.APPROVED);
        Long rejectedLeaves = leaveRepository.countByStatus(LeaveStatus.REJECTED);

        return DashboardStats.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .totalLeaves(totalLeaves)
                .pendingLeaves(pendingLeaves)
                .approvedLeaves(approvedLeaves)
                .rejectedLeaves(rejectedLeaves)
                .build();
    }
}
