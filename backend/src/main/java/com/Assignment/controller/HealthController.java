package com.Assignment.controller;

import com.Assignment.repository.UserRepository;
import com.Assignment.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HealthCheckService healthCheckService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Perform a simple database query to keep the database active
            long userCount = userRepository.count();
            
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("service", "Employee Management System");
            response.put("message", "Backend is running");
            response.put("database", "Connected");
            response.put("userCount", userCount);
            
            // Enable scheduled health check to external endpoint
            healthCheckService.enableScheduledHealthCheck();
            response.put("scheduledHealthCheck", "Enabled - calling external endpoint every 1 minute");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("timestamp", LocalDateTime.now());
            response.put("service", "Employee Management System");
            response.put("message", "Backend is running but database connection failed");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(503).body(response);
        }
    }
}

