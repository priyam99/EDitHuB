package com.edithub.health;

import com.edithub.common.ApiResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HealthController.
 * Tests the response structure without starting a Spring context.
 */
class HealthControllerTest {

    private final HealthController controller = new HealthController();

    @Test
    void healthEndpointReturnsUp() {
        ApiResponse<Map<String, String>> response = controller.health();

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("UP", response.getData().get("status"));
        assertEquals("edithub-backend", response.getData().get("service"));
    }

    @Test
    void infoEndpointReturnsAppInfo() {
        ApiResponse<Map<String, String>> response = controller.info();

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("EditHub", response.getData().get("name"));
        assertNotNull(response.getData().get("version"));
    }
}
