package com.himedia.luckydokiapi.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final String uniqueId = UUID.randomUUID().toString();

    @GetMapping
    public String checkHealth() {
        return "Server ID" + uniqueId;
    }

}
