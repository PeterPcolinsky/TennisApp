package sk.peter.tenis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Simple health check controller.
 *
 * Used to verify that the application is running and reachable.
 */
@RestController
public class HealthController {


    /**
     * Returns basic health status of the application.
     *
     * @return "OK" if the application is running
     */

    private static final String STATUS = "OK";

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", STATUS);
    }
}