package com.Assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveStatsResponse {
    private long totalLeaves;
    private long approvedLeaves;
    private long pendingLeaves;
    private long rejectedLeaves;
    private long totalDaysOnLeave;
}
