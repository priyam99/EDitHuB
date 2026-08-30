package com.edithub.health;

import com.edithub.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health and status endpoints for the EditHub API.
 * <p>
 * These are separate from Spring Actuator health checks.
 * They provide application-level information to the frontend.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "service", "edithub-backend"
        ));
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, String>> info() {
        return ApiResponse.ok(Map.of(
                "name", "EditHub",
                "description", "GitHub for Video Editing",
                "version", "0.0.1-SNAPSHOT"
        ));
    }
}
