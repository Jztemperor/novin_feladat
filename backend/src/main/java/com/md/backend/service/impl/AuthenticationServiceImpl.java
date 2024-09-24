package com.md.backend.service.impl;

import com.md.backend.dto.Authentication.RegistrationRequest;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.exception.RegistrationException;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ApplicationUserRepository applicationUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(ApplicationUserRepository applicationUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.applicationUserRepository = applicationUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * @param registrationRequest the registration request containing user details
     * @throws RegistrationException if username is already in use or role is incorrect
     */
    @Override
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
}
