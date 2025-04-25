package com.feastora.food_ordering.store;

import com.feastora.food_ordering.model.CustomerSessionModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionStore {
    private final Map<String, CustomerSessionModel> sessionMap = new ConcurrentHashMap<>();

    public void saveSession(CustomerSessionModel session) {
        sessionMap.put(session.getSessionToken(), session);
    }

    public CustomerSessionModel getSession(String sessionToken) {
        return sessionMap.get(sessionToken);
    }

    public Map<String, CustomerSessionModel> getAllSessions() {
        return this.sessionMap;
    }
}

