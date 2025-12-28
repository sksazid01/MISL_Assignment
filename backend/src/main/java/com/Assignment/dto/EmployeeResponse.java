package com.Assignment.dto;

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
public class EmployeeResponse {
    
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String department;
    private String designation;
    private String phoneNumber;
    private LocalDate joinDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer totalLeaves;
}
