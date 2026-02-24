package com.Assignment.controller;

import com.Assignment.dto.LeaveRequest;
import com.Assignment.dto.LeaveResponse;
import com.Assignment.dto.LeaveStatusUpdateRequest;
import com.Assignment.entity.LeaveStatus;
import com.Assignment.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<LeaveResponse>> getAllLeaves(
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) Long employeeId) {
        return ResponseEntity.ok(leaveService.getAllLeaves(status, employeeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> applyLeave(@Valid @RequestBody LeaveRequest request) {
        LeaveResponse created = leaveService.applyLeave(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updateLeave(@PathVariable Long id, @Valid @RequestBody LeaveRequest request) {
        return ResponseEntity.ok(leaveService.updateLeave(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLeaveStatus(
            @PathVariable Long id,
            @Valid @RequestBody LeaveStatusUpdateRequest request) {
        return ResponseEntity.ok(leaveService.updateLeaveStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.deleteLeave(id));
    }

    @GetMapping("/stats/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getEmployeeLeaveStats(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getEmployeeLeaveStats(employeeId));
    }
}
