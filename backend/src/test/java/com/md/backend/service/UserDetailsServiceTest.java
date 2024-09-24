package com.md.backend.service;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void loadUserByUsername_UserFound_ReturnsUserDetails() {
        ApplicationUser user = new ApplicationUser();
        user.setUsername("test");
        user.setPassword("pwd123");

        when(applicationUserRepository.findByUsername("test")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test");
    }

    @Test
    public void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {

        when(applicationUserRepository.findByUsername("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("x"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Hibás felhasználónév vagy jelszó!");
    }
}
