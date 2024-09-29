package com.md.backend.dto.ApplicationUser;

import com.md.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ApplicationUserDto {
    private long id;
    private String username;
    private Set<Role> authorities;
}
