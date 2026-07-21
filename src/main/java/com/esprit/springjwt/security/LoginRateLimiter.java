package com.esprit.springjwt.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiter: max 10 login attempts per IP per 15 minutes.
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MS   = 15 * 60 * 1000L;

    private static class Entry {
        AtomicInteger count = new AtomicInteger(0);
        long windowStart = System.currentTimeMillis();
    }

    private final ConcurrentHashMap<String, Entry> map = new ConcurrentHashMap<>();

    public boolean isAllowed(String ip) {
        Entry entry = map.compute(ip, (k, e) -> {
            if (e == null) return new Entry();
            if (System.currentTimeMillis() - e.windowStart > WINDOW_MS) {
                Entry fresh = new Entry();
                fresh.windowStart = System.currentTimeMillis();
                return fresh;
            }
            return e;
        });
        return entry.count.incrementAndGet() <= MAX_ATTEMPTS;
    }

    public void reset(String ip) {
        map.remove(ip);
    }
}
