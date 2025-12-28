package com.Assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotNull(message = "User ID is required")
    private Long userId; // Required link to User entity

    @NotBlank(message = "Department is required")
    private String department;

    private String designation;

    private String phoneNumber;

    @NotNull(message = "Join date is required")
    private LocalDate joinDate;
}
