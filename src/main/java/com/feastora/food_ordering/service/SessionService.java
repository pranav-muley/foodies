package com.feastora.food_ordering.service;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.model.CustomerSession;
import com.feastora.food_ordering.repository.CustomerSessionRepository;
import com.feastora.food_ordering.store.InMemorySessionStore;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.UUID;

@Service
public class SessionService extends BaseResponse {
    private final JwtUtil jwtUtil;
    private final InMemorySessionStore sessionStore;
    private final CustomerSessionRepository customerSessionRepository;

    public SessionService(JwtUtil jwtUtil, InMemorySessionStore sessionStore, CustomerSessionRepository customerSessionRepository) {
        this.jwtUtil = jwtUtil;
        this.sessionStore = sessionStore;
        this.customerSessionRepository = customerSessionRepository;
    }

    public String initiateSession(String tableToken, HttpServletRequest request) {
        Claims claims = jwtUtil.validateToken(tableToken);

        String restaurantId = claims.get("restaurantId", String.class);
        int tableNumber = claims.get("tableNumber", Integer.class);

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        String sessionToken = UUID.randomUUID().toString();

        long expiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        CustomerSession session = new CustomerSession();
        session.setSessionToken(sessionToken);
        session.setRestaurantId(restaurantId);
        session.setTableNumber(tableNumber);
        session.setUserAgent(userAgent);
        session.setIp(ip);
        session.setCreatedAt(System.currentTimeMillis());
        session.setExpiresAt(expiry);

        sessionStore.saveSession(session);
        return sessionToken;
    }

    public boolean validateSession(String sessionToken, HttpServletRequest request) {
        CustomerSession session = sessionStore.getSession(sessionToken);
        if (session == null || session.getExpiresAt() < System.currentTimeMillis()) return false;

        String reqUA = request.getHeader("User-Agent");
        String reqIP = request.getRemoteAddr();

        return session.getUserAgent().equals(reqUA) && session.getIp().equals(reqIP);
    }

    public GenericResponse<String> refreshCustomerSessionToken(String oldToken) {
        CustomerSession session = customerSessionRepository.findBySessionToken(oldToken);

        if (ObjectUtils.isEmpty(session) || session.getExpiresAt() < System.currentTimeMillis()) {
            return newRestErrorResponse(403, "Session expired");
        }

        String newSessionToken = UUID.randomUUID().toString();
        long newExpiry = System.currentTimeMillis() + (2 * 60 * 60 * 1000);

        CustomerSession newSession = new CustomerSession();
        newSession.setSessionToken(newSessionToken);
        newSession.setRestaurantId(session.getRestaurantId());
        newSession.setTableNumber(session.getTableNumber());
        newSession.setUserAgent(session.getUserAgent());
        newSession.setIp(session.getIp());
        newSession.setCreatedAt(System.currentTimeMillis());
        newSession.setExpiresAt(newExpiry);

        customerSessionRepository.save(newSession);
        return newRestResponseData(newSessionToken);
    }
}

