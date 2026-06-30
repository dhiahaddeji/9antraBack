package com.esprit.springjwt.entity;

public enum AuthProvider {
    local,
    facebook,
    google,
    github;

    public static AuthProvider fromString(String value) {
        if (value == null) {
            return local; // default
        }
        try {
            return AuthProvider.valueOf(value.toLowerCase());
        } catch (IllegalArgumentException e) {
            return local; // fallback to local for invalid values
        }
    }
}
