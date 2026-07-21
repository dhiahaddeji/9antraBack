package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.ActivityLog;
import com.esprit.springjwt.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class ActivityLogController {

    @Autowired
    private ActivityLogService logService;

    /** Paginated list with optional filters */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public Page<ActivityLog> getLogs(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "50") int    size,
            @RequestParam(required = false)    String action,
            @RequestParam(required = false)    String username,
            @RequestParam(required = false)    String from,
            @RequestParam(required = false)    String to) {
        return logService.filter(action, username, from, to, page, size);
    }

    /** Action-type counts for the stats bar */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public Map<String, Long> stats() {
        return logService.stats();
    }
}
