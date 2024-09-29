package com.md.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.ApplicationUser.UpdateUserRequest;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ApplicationUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role1;

    private Role role2;

    private ApplicationUser user;

    @BeforeEach
    public void setUp() {
        applicationUserRepository.deleteAll();  // First delete all users to clear references
        roleRepository.deleteAll();  // Then delete all roles

        role1 = roleRepository.save(new Role(1L, "role1", "desc"));
        role2 = roleRepository.save(new Role(2L, "role2", "desc"));

        user = new ApplicationUser();
        user.setUsername("user");
        user.setName("user");
        user.setPassword("password");
        user.setAuthorities(Set.of(role1));  // Assign roles to the user
        applicationUserRepository.save(user);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", user.getUserId()))
                .andExpect(status().is(204))
                .andExpect(content().string("Felhasználó törölve!"));

        assertFalse(applicationUserRepository.existsById(user.getUserId()));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void deleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", user.getUserId()+1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors.message").value("A törölni kívánt felhasználó nem található!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRole_CorrectInput_Success() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(user.getUserId(), List.of(role1.getRoleId(), role2.getRoleId()));

        // Act & Assert
        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Felhasználó frissítve!"));

        // Fetch updated user from DB and verify roles
        ApplicationUser updatedUser = applicationUserRepository.findById(user.getUserId()).orElseThrow();
        Set<Long> updatedRoleIds = updatedUser.getAuthorities().stream().map(Role::getRoleId).collect(Collectors.toSet());

        assertThat(updatedRoleIds).containsExactlyInAnyOrder(role1.getRoleId(), role2.getRoleId());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRole_UserNotFound_ThrowsException() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(999L, List.of(role1.getRoleId(), role2.getRoleId()));  // Non-existent userId

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors.message").value("A frissíteni kívánt felhasználó nem található!"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRole_EmptyRolesList_ThrowsException() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(user.getUserId(), new ArrayList<>());

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors.message").value("A szerepek listája nem lehet üres!"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRole_MissingRoles_ThrowsException() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(user.getUserId(),List.of(role1.getRoleId(), role2.getRoleId()+1));

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
