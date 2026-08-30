package com.edithub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Basic smoke test — verifies the application class exists.
 * Full context tests require a running database, so they are
 * integration tests that run against Docker Compose.
 */
class EditHubApplicationTests {

    @Test
    void applicationClassExists() {
        EditHubApplication app = new EditHubApplication();
        assertNotNull(app);
    }
}
