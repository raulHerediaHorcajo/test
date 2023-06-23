package com.example.demo.security.config;

public class SecurityExpressions {

    public enum UserRole {
        ADMIN,
        USER
    }

    public enum Endpoint {
        USERS("/api/users"),
        USERS_DETAIL("/api/users/**"),
        SOCIETIES("/api/societies"),
        SOCIETIES_DETAIL("/api/societies/**"),
        GENERATOR_TYPES("/api/generator-types"),
        GENERATOR_TYPES_DETAIL("/api/generator-types/**"),
        AUTH("/api/auth/**");

        private final String pattern;

        Endpoint(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }
}