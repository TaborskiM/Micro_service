package com.library.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Test-only dummy value so the ${JWT_SECRET} placeholder resolves without a real secret.
@SpringBootTest(properties = "JWT_SECRET=test-only-dummy-secret-at-least-32-bytes")
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
