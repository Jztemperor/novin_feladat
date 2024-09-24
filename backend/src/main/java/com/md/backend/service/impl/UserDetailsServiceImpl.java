package com.md.backend.service.impl;

import com.md.backend.repository.ApplicationUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    /**
     * Loads the user by username.
     *
     * Fetches the user details from the repository and returns it.
     * If the user is not found, it throws {@link UsernameNotFoundException}.
     *
     * @param username the username identifying the user
     * @return the user details for the given username
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Hibás felhasználónév vagy jelszó!")
        );
    }
}
