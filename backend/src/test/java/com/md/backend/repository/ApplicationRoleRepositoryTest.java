package com.md.backend.repository;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase()
public class ApplicationRoleRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setAuthority("test_role");
        testEntityManager.persist(role);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        ApplicationUser user = new ApplicationUser();
        user.setUsername("test");
        user.setPassword("test");
        user.setName("test");
        user.setAuthorities(roles);
        testEntityManager.persist(user);
    }

    @Test
    public void findByUsername_UserFound_ReturnsUserOptional() {
        Optional<ApplicationUser> userOptional = applicationUserRepository.findByUsername("test");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getUsername()).isEqualTo("test"));
    }

    @Test
    public void findByUsername_UserNotFound_ReturnsEmptyOptional() {
        Optional<ApplicationUser> userOptional = applicationUserRepository.findByUsername("x");
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void existsByUsername_UserFound_ReturnsTrue() {
        boolean doesExists = applicationUserRepository.existsByUsername("test");
        assertThat(doesExists).isTrue();
    }

    @Test
    public void existsByUsername_UserNotFound_ReturnsFalse() {
        boolean doesExists = applicationUserRepository.existsByUsername("x");
        assertThat(doesExists).isFalse();
    }
}
