package com.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final Environment env;

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in Gateway Service" +
                ", port(server.port)=" + env.getProperty("server.port") +
                "encrypt key=" + env.getProperty("token.secret");
    }
}
