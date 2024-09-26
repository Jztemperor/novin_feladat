package com.md.backend.dto.Authentication;

import com.md.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Instant expiresAt;
    private String username;
    private Set<Role> roles;
    private LocalDateTime lastLogin;
}
