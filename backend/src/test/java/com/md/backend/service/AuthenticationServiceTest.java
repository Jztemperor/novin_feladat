package com.md.backend.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequest registrationRequest;

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
}
