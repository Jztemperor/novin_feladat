package com.md.backend.service;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private final Long jwtExpiration = 3600000L;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void setUp() {
        Role defaultRole = new Role();
        defaultRole.setAuthority(RoleNamesEnum.FELHASZNALO.getName());

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);

        applicationUser = new ApplicationUser();
        applicationUser.setUsername("username");
        applicationUser.setName("name");
        applicationUser.setPassword("password");
        applicationUser.setAuthorities(roles);
    }

    @Test
    void generateToken_ShouldReturnNonEmptyToken() {

        // Create User object from ApplicationUser
        User user = new User(
                applicationUser.getUsername(),
                applicationUser.getPassword(),
                applicationUser.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                        .collect(Collectors.toList())
        );

        // Create JwtClaimsSet with necessary claims
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(3600000L))
                .subject(user.getUsername())
                .claim("scope", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .build();

        // Mock the JwtEncoder to return a mock Jwt object with these claims
        Jwt jwt = new Jwt("mockTokenValue", Instant.now(), Instant.now().plusMillis(3600000L), Map.of("alg", "none"), jwtClaimsSet.getClaims());
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Mock the authentication to return the User object
        when(authentication.getPrincipal()).thenReturn(user);

        // Generate token
        String token = jwtService.generateToken(authentication);

        // Verify token is not empty
        assertFalse(token.isEmpty());

        // Verify that the encoder was called
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }
}
