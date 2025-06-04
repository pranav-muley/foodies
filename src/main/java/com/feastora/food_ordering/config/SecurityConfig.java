package com.feastora.food_ordering.config;

import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.Utility.ThreadContextUtils;
import com.feastora.food_ordering.filters.JwtAuthFilter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/app/register", "/app/login", "/app/verifyRegistration").permitAll()
                        .requestMatchers("/order/edit", "/generate/qr").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain chain)
            throws ServletException, IOException {

        String sessionToken = request.getHeader("Authorization");
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        if (request.getRequestURI().startsWith("/secure-session")) {
            Claims claims = jwtUtil.validateToken(sessionToken);
            if (sessionToken == null || ObjectUtils.isEmpty(claims)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or stolen session token.");
                return;
            }
            // setting userId and TableNum in ThreadContext params instead of request.setAttributes();
            ThreadContextUtils.setUserId(jwtUtil.getUserId(sessionToken));
            ThreadContextUtils.setTableNumber(jwtUtil.getTableNumber(sessionToken));
        }

        chain.doFilter(request, response);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

}

