package com.feastora.food_ordering.service;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.model.CustomerSessionModel;
import com.feastora.food_ordering.repository.CustomerSessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
public class CustomerSessionService extends BaseResponse {

    private final CustomerSessionRepository sessionRepository;
    private final JwtUtil jwtUtil;

    private static final long SESSION_DURATION_MS = Duration.ofHours(2).toMillis();

    public CustomerSessionService(CustomerSessionRepository sessionRepository, JwtUtil jwtUtil) {
        this.sessionRepository = sessionRepository;
        this.jwtUtil = jwtUtil;
    }

    public CustomerSessionModel createSession(String userId, Long tableNumber, String userAgent, String ipAddress, String sessionToken) {
        CustomerSessionModel session = new CustomerSessionModel();
        long now = System.currentTimeMillis();
        session.setSessionToken(sessionToken);
        session.setUserId(userId);
        session.setTableNumber(tableNumber);
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);
        session.setCreatedAt(now);
        session.setExpiresAt(now + SESSION_DURATION_MS);
        return sessionRepository.save(session);
    }

    public GenericResponse<String> refreshCustomerSessionToken(String oldToken) {
        CustomerSessionModel session = sessionRepository.findBySessionToken(oldToken);

        if (ObjectUtils.isEmpty(session) || session.getExpiresAt() < System.currentTimeMillis()) {
            return newRestErrorResponse(403, "Session expired");
        }

        String newSessionToken = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        session.setSessionToken(newSessionToken);
        session.setCreatedAt(now);
        session.setExpiresAt(now + SESSION_DURATION_MS);
        sessionRepository.save(session);

        return newRestResponseData(newSessionToken);
    }

    public String initiateSession(String token, HttpServletRequest request) {
        if (!jwtUtil.validateSessionToken(token, request)) {
            throw new IllegalArgumentException("Token is invalid or expired.");
        }

        String userId = jwtUtil.getUserId(token);
        Long tableNumber = jwtUtil.getTableNumber(token);
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String sessionToken = jwtUtil.generateSessionToken(userId, tableNumber, ip, userAgent);
        if (sessionToken == null) throw new IllegalArgumentException("Failed to generate session token.");

        return createSession(userId, tableNumber, userAgent, ip, sessionToken).getSessionToken();
    }
}
