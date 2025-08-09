package com.feastora.food_ordering.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ServerConfig {
    public String url;

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() +
                ":" + request.getContextPath();
    }
}
