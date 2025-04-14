package com.feastora.food_ordering.config;

import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.Utility.ThreadContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .anyRequest().permitAll()) // Allow these URLs
//                .authorizeHttpRequests(request -> request.anyRequest().authenticated()) // Any other requests require authentication
                .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic Authentication
                .csrf(AbstractHttpConfigurer::disable);// Disable CSRF for simplicity in this example

        return http.build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = request.getParameter("tkn");

        String path = request.getRequestURI();

        // Only validate for specific paths
        if (path.startsWith("/order") || path.startsWith("/cart")) {
            if (token == null || !jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired QR token.");
                return;
            }
            // setting userId and TableNum in THreadCOntext params instead of request.setAttributes();
            ThreadContextUtils.setUserId(jwtUtil.getUserId(token));
            ThreadContextUtils.setTableNumber(jwtUtil.getTableNumber(token));
        }

        chain.doFilter(request, response);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

}

