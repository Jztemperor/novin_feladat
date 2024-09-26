package com.md.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.Authentication.LoginRequest;
import com.md.backend.dto.Authentication.LoginResponse;
import com.md.backend.dto.Authentication.RegistrationRequest;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private RegistrationRequest registrationRequest;

    private LoginRequest loginRequest;

    private String registrationRequestJson;

    private String loginRequestJson;


    @BeforeEach
    public void setUp() throws JsonProcessingException {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setName("name");
        registrationRequest.setUsername("username");
        registrationRequest.setPassword("name");
        registrationRequest.setPassword("password");
        registrationRequest.setRoleName(RoleNamesEnum.FELHASZNALO.getName());

        loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");

        registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);
        loginRequestJson = objectMapper.writeValueAsString(loginRequest);
    }

    @Test
    public void register_SignupSuccess_ReturnsSuccessStringAndCreatedHttpCode() throws Exception {
        doNothing().when(authenticationService).register(registrationRequest);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Sikeres regisztráció!"));
    }

    @Test
    public void register_EmptyCredentials_ReturnsErrorResponse() throws Exception {
        doNothing().when(authenticationService).register(registrationRequest);

        registrationRequest.setUsername("");
        registrationRequest.setName("");
        registrationRequest.setPassword("");
        registrationRequest.setRoleName("");
        registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.username").value("A felhasználónév megadása kötelező!"))
                .andExpect(jsonPath("$.errors.name").value("A név megadása kötelező!"))
                .andExpect(jsonPath("$.errors.roleName").value("A szerepkör kiválasztása kötelező!"))
                .andExpect(jsonPath("$.errors.password").value("A jelszó megadása kötelező!"));
    }

    @Test
    public void login_ValidInput_ReturnsLoginResponse() throws Exception {
        LoginResponse loginResponse = new LoginResponse("token", Instant.now(), "username", new HashSet<Role>(), LocalDateTime.now());

        when(authenticationService.login(loginRequest)).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(loginResponse.getToken()))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.username").value(loginResponse.getUsername()));
    }

    @Test
    public void login_BadCredentials_ThrowsException() throws  Exception {
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationService).login(any(LoginRequest.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.password").value("Hibás felhasználónév vagy jelszó!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
