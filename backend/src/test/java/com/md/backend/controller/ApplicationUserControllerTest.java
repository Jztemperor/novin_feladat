package com.md.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import com.md.backend.dto.ApplicationUser.UpdateUserRequest;
import com.md.backend.entity.Role;
import com.md.backend.service.ApplicationUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ApplicationUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationUserService applicationUserService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void getUsernamesWithRoles_AsAdmin_ReturnsCorrectResponse() throws Exception {
        // Setup
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "Felhasznalo", "description"));

        Pageable pageable = PageRequest.of(0, 10);

        ApplicationUserDto userDto1 = new ApplicationUserDto(1, "user1", roles);
        ApplicationUserDto userDto2 = new ApplicationUserDto(2, "user2", roles);
        List<ApplicationUserDto> userDtos = List.of(userDto1, userDto2);
        Page<ApplicationUserDto> page = new PageImpl<>(userDtos, pageable, userDtos.size());

        // Mock service
        lenient().when(applicationUserService.getAllUsersWithRoles(pageable)).thenReturn(page);

        // Mock request and assert
        mockMvc.perform(get("/api/user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Felhasznalo", "SCOPE_Konyvelo"})
    public void getUsernamesWithRoles_AsFelhasznaloOrKonyvelo_ReturnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/api/user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void deleteUser_AsAdmin_CorrectId_ReturnsSuccessResponse() throws Exception {
        // Mock void service call
        lenient().doNothing().when(applicationUserService).deleteUser(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/user/{id}", 1L))
                .andExpect(status().is(204))
                .andExpect(content().string("Felhasználó törölve!"));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Felhasznalo", "SCOPE_Konyvelo"})
    public void deleteUser_AsFelhasznaloOrKonyvelo_CorrectId_ReturnsForbiddenStatus() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/api/user/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void deleteUser_IncorrectId_ReturnsErrorResponse() throws Exception {
        // Mock void service call
        lenient().doThrow(new EntityNotFoundException("A törölni kívánt felhasználó nem található!"))
                .when(applicationUserService).deleteUser(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/user/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors.message").value("A törölni kívánt felhasználó nem található!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRoles_CorrectInput_ReturnsSuccessResponse() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserId(1L);
        updateUserRequest.setRoleIds(Arrays.asList(1L, 2L));

        // Mocking the service layer
        doNothing().when(applicationUserService).updateUserRole(1L, Arrays.asList(1L, 2L));

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"roleIds\": [1, 2]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRoles_UserNotFound_ThrowsException() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserId(1L);
        updateUserRequest.setRoleIds(Arrays.asList(1L, 2L));

        doThrow(new EntityNotFoundException()).when(applicationUserService).updateUserRole(200L, Arrays.asList(1L, 2L));

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 200, \"roleIds\": [1, 2]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void updateUserRoles_EmptyRoleIdsList_ThrowsException() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserId(1L);
        updateUserRequest.setRoleIds(Arrays.asList(1L, 2L));

        doThrow(new EntityNotFoundException()).when(applicationUserService).updateUserRole(1L, new ArrayList<>());

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"roleIds\": []}"))
                .andExpect(status().isNotFound());
    }
}
