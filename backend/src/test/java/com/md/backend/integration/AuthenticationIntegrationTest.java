package com.md.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.Authentication.RegistrationRequest;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase()
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        applicationUserRepository.deleteAll();
        roleRepository.deleteAll();

        Role defaultRole = new Role();
        defaultRole.setAuthority(RoleNamesEnum.FELHASZNALO.getName());
        roleRepository.save(defaultRole);
    }

    @Test
    public void register_RegisterSuccess_SavesUser() throws Exception {
        // Setup DTO
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("username");
        registrationRequest.setName("name");
        registrationRequest.setRoleName(RoleNamesEnum.FELHASZNALO.getName());
        registrationRequest.setPassword("password");

        // Mock Request
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Sikeres regisztráció!"));

        // Check if user is created
        Optional<ApplicationUser> savedUser = applicationUserRepository.findByUsername("username");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo("username");
    }
}
