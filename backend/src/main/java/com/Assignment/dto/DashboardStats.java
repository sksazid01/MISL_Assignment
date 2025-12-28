package com.Assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalEmployees;
    private Long activeEmployees;
    private Long totalLeaves;
    private Long pendingLeaves;
    private Long approvedLeaves;
    private Long rejectedLeaves;
    private List<LeaveResponse> recentLeaves;
}
