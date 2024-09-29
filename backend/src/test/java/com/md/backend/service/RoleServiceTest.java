package com.md.backend.service;

import com.md.backend.dto.Role.RoleDto;
import com.md.backend.entity.Role;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    public void getAllRoles_ReturnsRoleDtoList() {
        // arrange
        Role role1 = new Role(1L, "role1", "desc");
        Role role2 = new Role(2L, "role2", "desc");
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<RoleDto> roleDtoList = roleService.getAllRoles();

        // Assert
        assertEquals(2, roleDtoList.size());
        assertEquals("role1", roleDtoList.get(0).getAuthority());
        assertEquals("role2", roleDtoList.get(1).getAuthority());
    }
}
