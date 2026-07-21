package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.ActivityLog;
import com.esprit.springjwt.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repo;

    // ── Core write (async so it never slows down the request) ────────────
    @Async
    public void log(String action, String entityType, Long entityId,
                    String description, String username, String ip, Integer status) {
        try {
            ActivityLog entry = new ActivityLog(action, entityType, entityId,
                    description, username, ip, status);
            repo.save(entry);
        } catch (Exception ignored) {}
    }

    /** Convenience: resolves username + IP from current request context */
    @Async
    public void log(String action, String entityType, Long entityId, String description) {
        String username = resolveUsername();
        String ip       = resolveIp();
        log(action, entityType, entityId, description, username, ip, null);
    }

    // ── Reads ─────────────────────────────────────────────────────────────
    public Page<ActivityLog> getLogs(int page, int size) {
        return repo.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public Page<ActivityLog> filter(String action, String username,
                                    String from, String to,
                                    int page, int size) {
        Date dateFrom = parseDate(from, false);
        Date dateTo   = parseDate(to,   true);
        String a = (action   != null && !action.isBlank())   ? action   : null;
        String u = (username != null && !username.isBlank()) ? username : null;
        return repo.filter(a, u, dateFrom, dateTo, PageRequest.of(page, size));
    }

    public Map<String, Long> stats() {
        return repo.countByAction().stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long)   r[1]));
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private String resolveUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) return auth.getName();
        } catch (Exception ignored) {}
        return "anonymous";
    }

    private String resolveIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            String xff = attrs.getRequest().getHeader("X-Forwarded-For");
            return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim()
                    : attrs.getRequest().getRemoteAddr();
        } catch (Exception ignored) { return "unknown"; }
    }

    private Date parseDate(String s, boolean endOfDay) {
        if (s == null || s.isBlank()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(s);
            if (endOfDay) d = new Date(d.getTime() + 86399999L);
            return d;
        } catch (Exception ignored) { return null; }
    }
}
