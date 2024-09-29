package com.md.backend.service;

import com.md.backend.dto.Role.RoleDto;

import java.util.List;

public interface RoleService {
    List<RoleDto> getAllRoles();
}
