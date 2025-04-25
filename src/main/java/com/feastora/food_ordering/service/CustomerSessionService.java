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

import java.util.Date;
import java.util.UUID;

@Service
public class CustomerSessionService extends BaseResponse {

    private final CustomerSessionRepository sessionRepository;
    private final JwtUtil jwtUtil;

    public CustomerSessionService(CustomerSessionRepository sessionRepository, JwtUtil jwtUtil) {
        this.sessionRepository = sessionRepository;
        this.jwtUtil = jwtUtil;
    }

    public void createSession(String userId, Long tableNumber, String userAgent, String ipAddress, String sessionToken) {
        CustomerSessionModel session = new CustomerSessionModel();
        session.setSessionToken(sessionToken);
        session.setUserId(userId);
        session.setTableNumber(tableNumber);
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);
        session.setCreatedAt(System.currentTimeMillis());
        session.setExpiresAt(System.currentTimeMillis() + 2 * 60 * 60 * 1000L); // 2 hours
        sessionRepository.save(session);
    }

    public boolean validateSession(String sessionToken, HttpServletRequest request) {
        CustomerSessionModel session = sessionRepository.findBySessionToken(sessionToken);
        if (session == null || session.getExpiresAt() < System.currentTimeMillis()) return false;

        String reqUserAgent = request.getHeader("User-Agent");
        String reqIp = request.getRemoteAddr();

        return session.getUserAgent().equals(reqUserAgent) && session.getIpAddress().equals(reqIp);
    }

    public GenericResponse<String> refreshCustomerSessionToken(String oldToken) {
        CustomerSessionModel session = sessionRepository.findBySessionToken(oldToken);

        if (ObjectUtils.isEmpty(session) || session.getExpiresAt() < System.currentTimeMillis()) {
            return newRestErrorResponse(403, "Session expired");
        }

        String newSessionId = UUID.randomUUID().toString();
        long newExpiry = System.currentTimeMillis() + 2 * 60 * 60 * 1000L;

        session.setSessionToken(newSessionId);
        session.setCreatedAt(System.currentTimeMillis());
        session.setExpiresAt(newExpiry);

        sessionRepository.save(session);
        return newRestResponseData(newSessionId);
    }

    public String initiateSession(String token, HttpServletRequest request) {
        Claims claims = jwtUtil.validateToken(token);
        if (claims == null || claims.getExpiration() == null || claims.getExpiration().before(new Date())) {
            throw new IllegalArgumentException("Token has expired or is invalid.");
        }

        String userId = jwtUtil.getUserId(claims.getSubject());
        Long tableNumber = jwtUtil.getTableNumber(claims.getSubject());
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String sessionToken = jwtUtil.generateSessionToken(userId, tableNumber, ip, userAgent);
        createSession(userId, tableNumber, userAgent, ip, sessionToken);
        return sessionToken;
    }
}
