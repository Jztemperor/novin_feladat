package com.md.backend.service.impl;

import com.md.backend.dto.Role.RoleDto;
import com.md.backend.entity.Role;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves all roles from the repository and maps them to {@link RoleDto} objects.
     *
     * @return a list of {@link RoleDto} representing all roles
     */
    @Override
    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Maps a {@link Role} entity to a {@link RoleDto}.
     *
     * @param role the role entity to be mapped
     * @return a {@link RoleDto} representing the mapped role
     */
    private RoleDto mapToDto(Role role) {
        return new RoleDto(role.getRoleId(), role.getAuthority());
    }
}
