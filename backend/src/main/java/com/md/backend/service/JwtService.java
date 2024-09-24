package com.md.backend.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
    public String generateToken(Authentication authentication);
    public long getExpiration();
}
