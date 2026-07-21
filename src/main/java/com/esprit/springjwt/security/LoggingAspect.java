package com.esprit.springjwt.security;

import com.esprit.springjwt.service.ActivityLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Auto-logs every mutating HTTP call (POST/PUT/DELETE) in all controllers.
 * Reads (GET) are not logged to keep the table lean.
 */
@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private ActivityLogService logService;

    // ── pointcut: every public method in controllers package ─────────────
    @Pointcut("execution(public * com.esprit.springjwt.controllers..*(..))")
    private void allControllers() {}

    // ── after successful return ───────────────────────────────────────────
    @AfterReturning(pointcut = "allControllers()", returning = "result")
    public void afterSuccess(JoinPoint jp, Object result) {
        String method = jp.getSignature().getName().toLowerCase();
        if (isRead(method)) return;

        String action = resolveAction(method);
        if (action.equals("ACTION")) return; // skip unclassified calls

        String entityType = resolveEntity(jp.getSignature().getDeclaringTypeName());
        String desc       = action + " " + entityType + " via " + jp.getSignature().getName() + "()";

        logService.log(action, entityType, null, desc);
    }

    // ── helpers ───────────────────────────────────────────────────────────
    private boolean isRead(String method) {
        return method.startsWith("get") || method.startsWith("find") ||
               method.startsWith("load") || method.startsWith("search") ||
               method.startsWith("all") || method.startsWith("list") ||
               method.startsWith("fetch") || method.startsWith("count") ||
               method.startsWith("check");
    }

    private String resolveAction(String method) {
        if (method.contains("signin") || method.contains("login"))     return "LOGIN";
        if (method.contains("signup") || method.contains("register") ||
            method.contains("create") || method.contains("add") ||
            method.contains("save")   || method.contains("insert"))    return "CREATE";
        if (method.contains("update") || method.contains("edit") ||
            method.contains("modify") || method.contains("change") ||
            method.contains("enable") || method.contains("disable"))   return "UPDATE";
        if (method.contains("delete") || method.contains("remove"))    return "DELETE";
        if (method.contains("logout"))                                  return "LOGOUT";
        return "ACTION";
    }

    private String resolveEntity(String className) {
        String simple = className.substring(className.lastIndexOf('.') + 1)
                .replace("Controller", "").replace("controller", "");
        return simple.isEmpty() ? "Unknown" : simple;
    }

    private int extractStatus(Object result) {
        if (result instanceof ResponseEntity) return ((ResponseEntity<?>) result).getStatusCode().value();
        return 200;
    }
}
