package com.md.backend.service;

import com.md.backend.dto.Authentication.LoginRequest;
import com.md.backend.dto.Authentication.LoginResponse;
import com.md.backend.dto.Authentication.RegistrationRequest;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.exception.RegistrationException;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.impl.AuthenticationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequest registrationRequest;

    private LoginRequest loginRequest;

    private Role defaultRole;

    private Role adminRole;

    @BeforeEach
    public void setUp() {
        defaultRole = new Role();
        defaultRole.setAuthority(RoleNamesEnum.FELHASZNALO.getName());

        adminRole = new Role();
        adminRole.setAuthority(RoleNamesEnum.ADMINISZTRATOR.getName());

        registrationRequest = new RegistrationRequest();
        registrationRequest.setName("name");
        registrationRequest.setUsername("username");
        registrationRequest.setPassword("name");
        registrationRequest.setPassword("password");
        registrationRequest.setRoleName(RoleNamesEnum.FELHASZNALO.getName());

        loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");
    }

    @Test
    public void register_RegistrationSuccess_SavesUser() {
        when(applicationUserRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encoded");
        when(roleRepository.findByAuthority(RoleNamesEnum.FELHASZNALO.getName())).thenReturn(Optional.of(defaultRole));

        authenticationService.register(registrationRequest);

        verify(applicationUserRepository, times(1)).save(any(ApplicationUser.class));
    }

    @Test
    public void register_UsernameAlreadyExists_ThrowsRegistrationException() {
        when(applicationUserRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(RegistrationException.class);
    }

    @Test
    public void register_RoleNotFound_ThrowsRegistrationException() {
        when(applicationUserRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(roleRepository.findByAuthority(RoleNamesEnum.FELHASZNALO.getName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(RegistrationException.class);
    }

    @Test
    public void register_IncorrectRole_ThrowsRegistrationException() {
        registrationRequest.setRoleName(RoleNamesEnum.ADMINISZTRATOR.getName());
        when(applicationUserRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(roleRepository.findByAuthority(RoleNamesEnum.ADMINISZTRATOR.getName())).thenReturn(Optional.of(adminRole));

        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(RegistrationException.class);
    }

    @Test
    public void login_Success_ReturnsLoginResponse() {
        ApplicationUser user = new ApplicationUser();
        user.setUsername("username");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(applicationUserRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("dummyToken");
        when(jwtService.getExpiration()).thenReturn(3600000L); // 1 hour

        LoginResponse response = authenticationService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("dummyToken", response.getToken());
        assertEquals("username", response.getUsername());
        assertTrue(response.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    public void login_UserNotFound_ThrowsException() {
        ApplicationUser user = new ApplicationUser();
        user.setUsername("username");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(applicationUserRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(loginRequest);
        });
    }
}
