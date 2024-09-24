package com.md.backend.service.impl;

import com.md.backend.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final long jwtExpiration = 3600000;

    public JwtServiceImpl(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Generates a JWT token based on the provided authentication.
     *
     * Creates a JWT with claims including issuer, issued at time,
     * expiration time, subject, and user roles.
     *
     * @param authentication the authentication object containing user details
     * @return the generated JWT token as a String
     */
    @Override
    public String generateToken(Authentication authentication) {
        // Get userDetails
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        // Create claims
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtExpiration))
                .subject(principal.getUsername())
                // Convert roles into a string array
                .claim("scope", principal.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .build();

        // create token
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    @Override
    public long getExpiration() {
        return jwtExpiration;
    }
}
