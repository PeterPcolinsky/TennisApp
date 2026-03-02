package sk.peter.tenis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}