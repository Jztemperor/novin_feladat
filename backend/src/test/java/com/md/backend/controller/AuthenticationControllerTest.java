package com.md.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.Authentication.RegistrationRequest;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.service.AuthenticationService;
import com.md.backend.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
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

    private String registrationRequestJson;

    private Map<String, String> fieldErrors;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setName("name");
        registrationRequest.setUsername("username");
        registrationRequest.setPassword("name");
        registrationRequest.setPassword("password");
        registrationRequest.setRoleName(RoleNamesEnum.FELHASZNALO.getName());

        registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);
        fieldErrors = new HashMap<>();
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
}
