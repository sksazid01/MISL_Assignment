package com.Assignment.controller;

import com.Assignment.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private HealthCheckService healthCheckService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Employee Management System");
        response.put("message", "Backend is running");
        
        // Try to check database connection (optional)
        if (jdbcTemplate != null) {
            try {
                // Perform a simple database query to keep the database active
                Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
                response.put("database", "Connected");
                response.put("userCount", count);
            } catch (Exception e) {
                response.put("database", "Error: " + e.getMessage());
            }
        } else {
            response.put("database", "Not configured");
        }
        
        // Enable scheduled health check to external endpoint
        healthCheckService.enableScheduledHealthCheck();
        response.put("scheduledHealthCheck", "Enabled - calling external endpoint every 1 minute");
        
        return ResponseEntity.ok(response);
    }
}

