package com.feastora.food_ordering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/health")
public class HealthController {
    @GetMapping("/status")
    public String healthCheck() {
        return "OK";
    }
}
