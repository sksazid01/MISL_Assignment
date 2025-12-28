package com.Assignment.dto;

import com.Assignment.entity.LeaveStatus;
import com.Assignment.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String department;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalDays;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime appliedDate;
    private LocalDateTime updatedAt;
    private String approvedBy;
    private LocalDateTime approvalDate;
    private String rejectionReason;
}
