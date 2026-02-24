package com.Assignment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class HealthCheckService {
    
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);
    private static final String EXTERNAL_HEALTH_URL = "https://employee-management-system-xfnv.onrender.com/api/health";
    
    private final AtomicBoolean isSchedulingEnabled = new AtomicBoolean(false);
    private final RestTemplate restTemplate;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public HealthCheckService() {
        this.restTemplate = new RestTemplate();
    }
    
    public Map<String, Object> getHealthInfo() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Employee Management System");
        response.put("message", "Backend is running");

        if (jdbcTemplate != null) {
            try {
                Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
                response.put("database", "Connected");
                response.put("userCount", count);
            } catch (Exception e) {
                response.put("database", "Error: " + e.getMessage());
            }
        } else {
            response.put("database", "Not configured");
        }

        enableScheduledHealthCheck();
        response.put("scheduledHealthCheck", "Enabled - calling external endpoint every 1 minute");

        return response;
    }

    /**
     * Enable the scheduled health check
     */
    public void enableScheduledHealthCheck() {
        if (!isSchedulingEnabled.get()) {
            isSchedulingEnabled.set(true);
            logger.info("External health check scheduling enabled");
        }
    }
    
    /**
     * Scheduled method that runs every 1 minute to call external health endpoint
     */
    @Scheduled(fixedRate = 60000) // 60000 ms = 1 minute
    public void performScheduledHealthCheck() {
        if (!isSchedulingEnabled.get()) {
            return; // Skip if not enabled
        }
        
        try {
            logger.info("Calling external health endpoint: {}", EXTERNAL_HEALTH_URL);
            String response = restTemplate.getForObject(EXTERNAL_HEALTH_URL, String.class);
            logger.info("External health check successful: {}", response);
        } catch (Exception e) {
            logger.error("Failed to call external health endpoint: {}", e.getMessage());
        }
    }
}
