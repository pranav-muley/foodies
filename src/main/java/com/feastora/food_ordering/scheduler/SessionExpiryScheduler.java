package com.feastora.food_ordering.scheduler;


import com.feastora.food_ordering.store.InMemorySessionStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionExpiryScheduler {
    private final InMemorySessionStore sessionStore;

    public SessionExpiryScheduler(InMemorySessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Scheduled(fixedRate = 60000 * 10) // every 10 min
    public void cleanExpiredSessions() {
        sessionStore.getAllSessions().values().removeIf(
                session -> session.getExpiresAt() < System.currentTimeMillis());
    }
}