package com.Assignment.dto;

import com.Assignment.entity.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private LeaveStatus status;

    private String rejectionReason; // Required if status is REJECTED
}
