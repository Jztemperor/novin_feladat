package com.md.backend.controller;

import com.md.backend.dto.Role.RoleDto;
import com.md.backend.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void getAllRoles_RolesFound_ReturnsRoleDtoList() throws Exception {
        // arrange
        RoleDto roleDto1 = new RoleDto(1L, "role1");
        RoleDto roleDto2 = new RoleDto(2L, "role2");
        List<RoleDto> roles = Arrays.asList(roleDto1, roleDto2);

        when(roleService.getAllRoles()).thenReturn(roles);

        // Mock request
        mockMvc.perform(get("/api/role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authority").value("role1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].authority").value("role2"));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void getAllRoles_RolesNotFound_ReturnsEmpyList() throws Exception {
        // arrange
        List<RoleDto> roles = new ArrayList<>();
        when(roleService.getAllRoles()).thenReturn(roles);

        // Mock request
        mockMvc.perform(get("/api/role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
