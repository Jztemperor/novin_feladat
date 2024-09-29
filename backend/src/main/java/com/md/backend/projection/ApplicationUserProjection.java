package com.md.backend.projection;

import com.md.backend.entity.Role;

import java.util.Set;

public interface ApplicationUserProjection {
    Long getUserId();
    String getUsername();
    Set<Role> getAuthorities();
}
