package com.md.backend.service.impl;

import com.md.backend.dto.Authentication.LoginRequest;
import com.md.backend.dto.Authentication.LoginResponse;
import com.md.backend.dto.Authentication.RegistrationRequest;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.exception.RegistrationException;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.AuthenticationService;
import com.md.backend.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ApplicationUserRepository applicationUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(ApplicationUserRepository applicationUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.applicationUserRepository = applicationUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user.
     *
     * @param registrationRequest the registration request containing user details
     * @throws RegistrationException if username is already in use or role is incorrect
     */
    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        Map<String, String> fieldErrors = new HashMap<>();

        // Validations
        if(applicationUserRepository.existsByUsername(registrationRequest.getUsername())) {
            fieldErrors.put("username", "Ez a felhasználónév már használatban van!");
            throw new RegistrationException(fieldErrors);
        }

        Optional<Role> role = roleRepository.findByAuthority(registrationRequest.getRoleName());
        if(role.isEmpty() || role.get().getAuthority().equalsIgnoreCase(RoleNamesEnum.ADMINISZTRATOR.getName())) {
            fieldErrors.put("role", "Nem választhatod ezt a szerepkört!");
            throw new RegistrationException(fieldErrors);
        }

        // Setup new user
        Set<Role> roles = new HashSet<>();
        roles.add(role.get());

        ApplicationUser user = new ApplicationUser();
        user.setName(registrationRequest.getName());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setAuthorities(roles);

        applicationUserRepository.save(user);
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param loginRequest the login request containing username and password
     * @return a {@link LoginResponse} containing the generated JWT token,
     *         token expiration time, and the username of the authenticated user
     * @throws EntityNotFoundException if the user is not found in the system
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // Set auth object in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get ApplicationUser entity corresponding to login user
        ApplicationUser user = applicationUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Felhasználó nem található!"));


        // Create JWT
        String token = jwtService.generateToken(authentication);

        return new LoginResponse(
                token,
                Instant.now().plusMillis(jwtService.getExpiration()),
                user.getUsername(),
                user.getAuthorities()
        );
    }
}
