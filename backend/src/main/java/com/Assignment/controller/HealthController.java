package com.Assignment.controller;

import com.Assignment.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private final HealthCheckService healthCheckService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("[HEALTH] GET /api/health endpoint hit - checking backend status...");
        Map<String, Object> info = healthCheckService.getHealthInfo();
        logger.info("[HEALTH] Status: {}, DB: {}", info.get("status"), info.get("database"));
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> healthCheckWithId(@PathVariable String id) {
        logger.info("[HEALTH] GET /api/health/{} - received ID: {}", id, id);
        Map<String, Object> info = healthCheckService.getHealthInfo();
        logger.info("[HEALTH] ID: {} | Status: {}, DB: {}", id, info.get("status"), info.get("database"));
        info.put("requestedId", id);
        return ResponseEntity.ok(info);
    }
}
