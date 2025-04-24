package com.feastora.food_ordering.store;

import com.feastora.food_ordering.model.CustomerSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionStore {
    private final Map<String, CustomerSession> sessionMap = new ConcurrentHashMap<>();

    public void saveSession(CustomerSession session) {
        sessionMap.put(session.getSessionToken(), session);
    }

    public CustomerSession getSession(String sessionToken) {
        return sessionMap.get(sessionToken);
    }

    public Map<String, CustomerSession> getAllSessions() {
        return this.sessionMap;
    }
}

