package com.md.backend.dto.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDto {
    private long roleId;
    private String authority;
}
